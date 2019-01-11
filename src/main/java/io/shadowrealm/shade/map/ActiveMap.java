package io.shadowrealm.shade.map;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.lang.Callback;
import com.volmit.phantom.api.lang.D;
import com.volmit.phantom.api.lang.GList;
import com.volmit.phantom.api.lang.GMap;
import com.volmit.phantom.api.math.M;
import com.volmit.phantom.api.rift.Rift;
import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.api.sheduler.AR;
import com.volmit.phantom.api.sheduler.S;
import com.volmit.phantom.imp.rift.RiftException;
import com.volmit.phantom.lib.service.RiftSVC;
import com.volmit.phantom.main.PhantomPlugin;
import com.volmit.phantom.util.text.C;
import com.volmit.phantom.util.world.Cuboid;
import com.volmit.phantom.util.world.VectorMath;
import com.volmit.phantom.util.world.WorldEditor;

import io.shadowrealm.shade.Shade;
import io.shadowrealm.shade.services.LobbySVC;
import io.shadowrealm.shade.services.MapBuilderSVC;

public class ActiveMap implements Listener
{
	private CompiledMap compiled;
	private MapData mapData;
	private Rift rift;
	private boolean debug;
	private AR ar;
	private GMap<Player, ActivePlayer> players;
	private boolean forceAll;

	public void forceAll()
	{
		forceAll = true;
	}

	public ActiveMap(boolean debug, Player player)
	{
		try
		{
			players = new GMap<>();
			PhantomSender s = new PhantomSender(player, Shade.instance.getTag("Map"));
			//@builder
			rift = SVC.get(RiftSVC.class).createRift("active-map/" + UUID.randomUUID())
					.setEnvironment(Environment.NORMAL)
					.setAllowBosses(false)
					.setEntityTickLimit(0.1)
					.setTileTickLimit(0.1)
					.setDifficulty(Difficulty.EASY)
					.setNerfSpawnerMobs(true)
					.setPhysicsThrottle(200)
					.setMaxTNTUpdatesPerTick(1)
					.load();
			//@done
			s.sendMessage("Rift has Opened");
			try
			{
				s.sendMessage("Streaming Blocks");
				Cuboid c = WorldEditor.streamClipboardAnvil(rift, player);
				s.sendMessage("Committing Rift");

				new S(35)
				{
					@Override
					public void run()
					{
						s.sendMessage("Compiling Inflated Map");
						SVC.get(MapBuilderSVC.class).compile(c, true, new Callback<CompiledMap>()
						{
							@Override
							public void run(CompiledMap t)
							{
								compiled = t;
								mapData = compiled.getMap();

								if(compiled.getWarnings().isEmpty())
								{
									s.sendMessage("Rift Map is starting...");
									rift.setSpawn(getRandomSpawn());
									player.teleport(getRandomSpawn());
									startMap();
								}

								else
								{
									GMap<String, Integer> warnings = new GMap<>();
									s.sendMessage("Map compiled with " + compiled.getWarnings().size() + " warning" + (compiled.getWarnings().size() == 1 ? "" : " "));

									for(String i : compiled.getWarnings().v())
									{
										if(!warnings.containsKey(i))
										{
											warnings.put(i, 0);
										}

										warnings.put(i, warnings.get(i) + 1);
									}

									for(String i : warnings.k())
									{
										s.sendMessage(C.RED + "!! " + C.WHITE + i + C.YELLOW + " in " + warnings.get(i) + " position" + (warnings.get(i) == 1 ? "" : " "));
									}
								}
							}
						});
					}
				};
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		catch(RiftException e)
		{
			e.printStackTrace();
		}
	}

	public ActiveMap(boolean debug, CompiledMap t, Rift rift)
	{
		players = new GMap<>();
		this.rift = rift;
		this.debug = debug;
		try
		{
			compiled = t;
			mapData = compiled.getMap();

			if(compiled.getWarnings().isEmpty())
			{
				rift.setSpawn(getRandomSpawn());
				startMap();
			}

			else
			{
				GMap<String, Integer> warnings = new GMap<>();

				for(String i : compiled.getWarnings().v())
				{
					if(!warnings.containsKey(i))
					{
						warnings.put(i, 0);
					}

					warnings.put(i, warnings.get(i) + 1);
				}

				for(String i : warnings.k())
				{
					D.as("Active Map").f("Found Warning: " + i + " x" + warnings.get(i));
				}
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private void startMap()
	{
		players = new GMap<>();
		Bukkit.getPluginManager().registerEvents(ActiveMap.this, PhantomPlugin.plugin);

		ar = new AR(0)
		{
			@Override
			public void run()
			{
				tick();
			}
		};
	}

	private void tick()
	{
		try
		{
			if(forceAll)
			{
				for(Player i : Bukkit.getOnlinePlayers())
				{
					if(!i.getPlayer().getWorld().equals(rift.getWorld()))
					{
						new S()
						{
							@Override
							public void run()
							{
								i.teleport(getRandomSpawn());
							}
						};
					}
				}
			}

			for(Player i : rift.getWorld().getPlayers())
			{
				if(!players.containsKey(i))
				{
					players.put(i, new ActivePlayer(i, this));
				}
			}

			for(Player i : players.k())
			{
				if(!i.getWorld().equals(rift.getWorld()))
				{
					players.get(i).destroy();
					players.remove(i);
				}
			}

			if(rift.getWorld().getPlayers().isEmpty())
			{
				if(debug)
				{
					shutdown();
				}
			}

			if(M.interval(20))
			{
				new S()
				{
					@Override
					public void run()
					{
						rift.getWorld().setFullTime(rift.getWorld().getFullTime() + 24000);
						rift.getWorld().setTime(SVC.get(LobbySVC.class).getConfig().getTime());
					}
				};
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
			shutdown();
		}
	}

	@EventHandler
	public void on(ItemMergeEvent e)
	{
		if(e.getEntity().getWorld().equals(getRift().getWorld()))
		{
			e.setCancelled(true);
		}
	}

	public void shutdown()
	{
		try
		{
			for(Player i : players.k())
			{
				if(!i.getWorld().equals(rift.getWorld()))
				{
					players.get(i).destroy();
					players.remove(i);
				}
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		if(rift.isLoaded())
		{
			rift.unload();
		}

		try
		{
			ar.cancel();
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		HandlerList.unregisterAll(this);
	}

	public CompiledMap getCompiled()
	{
		return compiled;
	}

	public MapData getMapData()
	{
		return mapData;
	}

	public Rift getRift()
	{
		return rift;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public Location getRandomSpawn()
	{
		Location ll = new GList<>(mapData.getSpawns()).pickRandom().getPosition().getLocation(rift.getWorld());
		ll.setDirection(VectorMath.reverseXZ(ll.getDirection()));
		return ll;
	}

	public MapColor sampleColor(Location currentLocation)
	{
		return (MapColor) getClosest(currentLocation, new GList<MapColor>(mapData.getColors().values()));
	}

	public MapRegion sampleRegion(Location currentLocation)
	{
		return (MapRegion) getClosest(currentLocation, mapData.getRegions());
	}

	public MapMusic sampleMusic(Location currentLocation)
	{
		return (MapMusic) getClosest(currentLocation, mapData.getMusic());
	}

	public MapWorldObject getClosest(Location currentLocation, List<? extends MapWorldObject> pos)
	{
		double m = Double.MAX_VALUE;
		MapWorldObject closest = null;

		for(MapWorldObject i : pos)
		{
			double d = currentLocation.distanceSquared(i.getPosition().getLocation(currentLocation.getWorld()));
			if(d < m)
			{
				m = d;
				closest = i;
			}
		}

		return closest;
	}
}
