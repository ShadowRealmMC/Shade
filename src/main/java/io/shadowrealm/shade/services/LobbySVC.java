package io.shadowrealm.shade.services;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.EditSession;
import com.volmit.phantom.json.JSONException;
import com.volmit.phantom.json.JSONObject;
import com.volmit.phantom.lang.Callback;
import com.volmit.phantom.lang.GList;
import com.volmit.phantom.lang.GMap;
import com.volmit.phantom.lang.VIO;
import com.volmit.phantom.plugin.A;
import com.volmit.phantom.plugin.AsyncTickService;
import com.volmit.phantom.plugin.S;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.rift.Rift;
import com.volmit.phantom.rift.RiftException;
import com.volmit.phantom.rift.VoidGenerator;
import com.volmit.phantom.services.RiftSVC;
import com.volmit.phantom.services.WorldEditSVC;
import com.volmit.phantom.text.C;
import com.volmit.phantom.text.ProgressSpinner;
import com.volmit.phantom.util.Cuboid;
import com.volmit.phantom.util.PE;

import io.shadowrealm.shade.map.ActiveMap;
import io.shadowrealm.shade.map.CompiledMap;
import io.shadowrealm.shade.map.PayloadThread;
import io.shadowrealm.shade.map.config.MapConfig;

public class LobbySVC extends AsyncTickService
{
	private Rift rift;
	private MapConfig conf;
	private CompiledMap map;
	private ActiveMap activeMap;
	private Cuboid region;
	private GList<MapConfig> configs;
	public boolean ready;
	public String status;

	public LobbySVC()
	{
		super(0);
		ready = false;
		status = "Please Wait";
		configs = new GList<>();
	}

	@Override
	public void onBegin()
	{
		ready = false;
		handleFiles();

		new S()
		{
			@Override
			public void run()
			{
				new A()
				{
					@Override
					public void run()
					{
						GMap<Player, ProgressSpinner> px = new GMap<>();

						new PayloadThread(new Runnable()
						{
							@Override
							public void run()
							{
								for(Player i : Bukkit.getOnlinePlayers())
								{
									if(!px.containsKey(i))
									{
										px.put(i, ProgressSpinner.DEFAULT);
									}

									new S()
									{
										@Override
										public void run()
										{
											i.teleport(i.getLocation().clone().add(0, 100, 0));
											PE.BLINDNESS.a(100).d(1000).apply(i);
										}
									};

									i.sendTitle("", C.LIGHT_PURPLE + px.get(i).toString() + " " + C.DARK_GRAY + "" + C.BOLD + status, 0, 10000, 20);
								}
							}
						}).start();

						try
						{
							status = "Searching for Maps";
							if(getPotentialMaps().isEmpty())
							{
								f("No potential maps found!");
								return;
							}

							l("Potential Maps: " + getPotentialMaps().size());
							conf = getPotentialMaps().pickRandom();
							l("Selected Map: " + conf.getMapName() + " // " + conf.getVariationName() + " (" + conf.getSchematic() + ")");

							new S()
							{
								@Override
								public void run()
								{
									rift = createRift();
									rift.load();

									w("No Cached Map! We must build it from scratch!");

									try
									{
										constructMap(new Runnable()
										{
											@Override
											public void run()
											{
												compileMap();
											}
										});
									}

									catch(Throwable e)
									{
										e.printStackTrace();
									}
								}
							};
						}

						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}
				};
			}
		};
	}

	public void close()
	{
		try
		{
			if(activeMap != null)
			{
				activeMap.shutdown();
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private GList<MapConfig> getPotentialMaps()
	{
		GList<MapConfig> c = new GList<>();
		Calendar cal = Calendar.getInstance();
		int dom = cal.get(Calendar.DAY_OF_MONTH);
		int moy = cal.get(Calendar.MONTH) + 1;
		String s = moy + "/" + dom;
		String sa = "*/" + dom;
		String sb = moy + "/*";

		for(MapConfig i : configs)
		{
			if(i.getActivation().equalsIgnoreCase(s) || i.getActivation().equalsIgnoreCase(sa) || i.getActivation().equalsIgnoreCase(sb) || i.getActivation().equalsIgnoreCase("always") || i.getActivation().equalsIgnoreCase("*/*"))
			{
				c.add(i);
			}
		}

		return c;
	}

	private void handleFiles()
	{
		getSchematicFolder().mkdirs();
		configs = new GList<>();
		File folder = getMapFolder();

		for(File i : folder.listFiles())
		{
			if(i.isFile() && i.getName().endsWith(".json"))
			{
				try
				{
					MapConfig c = new MapConfig(new JSONObject(VIO.readAll(i)));
					File schem = new File(getSchematicFolder(), c.getSchematic());

					if(!schem.exists())
					{
						w("Cannot find schematic " + schem.getName() + " for map " + i.getName());
						continue;
					}

					configs.add(c);
					l("Loaded Map Config: " + i.getName());
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}

		writeExamples();
	}

	private void writeExamples()
	{
		File f1 = new File(new File(getMapFolder(), "examples"), "example-winter-break.json");
		File f2 = new File(new File(getMapFolder(), "examples"), "example.json");
		File f3 = new File(new File(getMapFolder(), "examples"), "example-new-years.json");
		MapConfig c1 = new MapConfig();
		f1.getParentFile().mkdirs();
		c1.setActivation("12/*  (all of december)");
		c1.getReplacements().add("EXAMPLE -- search1,search2,search3 -> replace1,replace2,replace3");
		c1.getReplacements().add("ward.ogg,strad.ogg -> stal.ogg");
		c1.getReplacements().add("Some Region Name.reg -> Wintery Region Name.reg");
		c1.getReplacements().add("light.mood -> dark.mood,far.mood");
		c1.getReplacements().add("blue.color -> red.color,green.color");
		c1.getReplacements().add("1 -> 2 (replace blocks with 1:x with 2:0)");
		c1.getReplacements().add("1:1 -> 2 (replace blocks with 1:1 with 2:0)");
		c1.getReplacements().add("1:x -> 2:x (replace blocks with 1:x with 2:x... keeps data values)");
		c1.getReplacements().add("1:x -> 2:x,3:x (replace blocks with 1:x with 2:x and 3:x... keeps data values)");
		try
		{
			VIO.writeAll(f1, c1.toJSON().toString(4));
			c1.setActivation("*/*");
			VIO.writeAll(f2, c1.toJSON().toString(4));
			c1.setActivation("1/1");
			VIO.writeAll(f3, c1.toJSON().toString(4));
		}

		catch(JSONException | IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onEnd()
	{

	}

	private File getSchematicFolder()
	{
		return new File(getMapFolder(), "schematics");
	}

	private File getMapFolder()
	{
		return new File("maps");
	}

	@Override
	public void onAsyncTick()
	{

		return;
	}

	private void startMap()
	{
		activeMap = new ActiveMap(false, map, rift);
		activeMap.forceAll();
		int x = 0;
		int z = 0;

		for(Chunk i : region.getChunks())
		{
			if(Math.abs(i.getX()) > x)
			{
				x = Math.abs(i.getX());
			}

			if(Math.abs(i.getZ()) > z)
			{
				z = Math.abs(i.getZ());
			}
		}

		rift.setForceLoadX(x);
		rift.setForceLoadZ(z);
		rift.setPhysicsThrottle(10);
		rift.slowlyPreload();
		status = "Preloading";

		l("Lobby Service Fully Operational!");
	}

	private void compileMap()
	{
		status = "Compiling";
		l("Compiling Map");
		SVC.get(MapBuilderSVC.class).compile(region, true, new Callback<CompiledMap>()
		{
			@Override
			public void run(CompiledMap t)
			{
				l("Map Compiled");
				map = t;
				l(map.getWarnings().size() + " Warnings");
				status = "Starting Rift";
				l("Starting Map!");
				startMap();
			}
		});
	}

	private void constructMap(Runnable done) throws Throwable
	{
		new A()
		{
			@Override
			public void run()
			{
				try
				{
					status = "Fabricating";
					l("Constructing Map from schematic: " + conf.getSchematic());
					File schematic = new File(getSchematicFolder(), conf.getSchematic());
					WorldEditSVC w = SVC.get(WorldEditSVC.class);
					EditSession e = w.getEditSession(rift.getWorld());
					l("Reading Map Schematic " + conf.getSchematic());
					status = "Reading Blueprint";
					Vector v = w.getOffset(schematic);
					Cuboid rg = w.getCuboid(rift.getWorld(), w.getSchematic(schematic).getClipboard().getRegion());
					//@builder
					region = new Cuboid(
							new Location(rift.getWorld(), rg.getSizeX() /2, 255, rg.getSizeZ()/2).clone().add(v),
							new Location(rift.getWorld(), -rg.getSizeX()/2, 0, -rg.getSizeZ()/2).clone().add(v));
					//@done

					l(region.getLowerNE(), region.getUpperSW());
					status = "Setting Border";
					rift.setWorldBorderCenter(region.getCenter().getX(), region.getCenter().getZ());
					rift.setWorldBorderEnabled(true);
					rift.setWorldBorderSize(Math.max(region.getSizeX(), region.getSizeZ()) * 3);
					l("Streaming Blocks from schematic into rift");
					status = "Streaming Blocks";
					l(v.toString());
					w.pasteSchematic(schematic, e, new Location(rift.getWorld(), v.getX(), -v.getY(), v.getZ()));
					status = "Flushing Blocks";
					e.flushQueue();
				}

				catch(Throwable e1)
				{
					e1.printStackTrace();
				}

				new S()
				{
					@Override
					public void run()
					{
						done.run();
					}
				};
			}
		};
	}

	private Rift createRift()
	{
		try
		{
			//@builder
			return SVC.get(RiftSVC.class).getOrCreate("lobby/" + conf.id() + "-" + UUID.randomUUID().toString())
					.setAllowBosses(false)
					.setMaxTNTUpdatesPerTick(1)
					.setArrowDespawnRate(5)
					.setXPMergeRadius(10)
					.setItemMergeRadius(1)
					.setItemDespawnRate(1)
					.setEntityTickLimit(0.51)
					.setTileTickLimit(0.51)
					.setPhysicsThrottle(100)
					.setAnimalActivationRange(32)
					.setPlayerTrackingRange(64)
					.setMiscActivationRange(32)
					.setMonsterActivationRange(32)
					.setTemporary(false)
					.setGenerator(VoidGenerator.class)
					.setRule("doTileDrops", "false")
					.setRule("doMobDrops", "false")
					.setRule("randomTickSpeed", "false")
					.setRule("doDaylightCycle", "false")
					.setRule("keepInventory", "false")
					.setRandomLightUpdates(false)
					.setHopperCheckRate(100)
					.setHopperTransferAmount(64)
					.setHopperTransferRate(100)
					.setNerfSpawnerMobs(true)
					.setTemporary(true)
					.setForcedGameMode(GameMode.ADVENTURE);
			//@done
		}

		catch(RiftException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public MapConfig getConfig()
	{
		return conf;
	}
}
