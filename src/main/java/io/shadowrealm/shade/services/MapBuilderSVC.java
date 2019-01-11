package io.shadowrealm.shade.services;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.volmit.phantom.api.command.PhantomSender;
import com.volmit.phantom.api.lang.Callback;
import com.volmit.phantom.api.lang.F;
import com.volmit.phantom.api.lang.GList;
import com.volmit.phantom.api.lang.GMap;
import com.volmit.phantom.api.lang.Profiler;
import com.volmit.phantom.api.math.M;
import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.api.service.Service;
import com.volmit.phantom.api.sheduler.A;
import com.volmit.phantom.api.sheduler.S;
import com.volmit.phantom.util.text.C;
import com.volmit.phantom.util.world.Cuboid;
import com.volmit.phantom.util.world.WorldEditor;

import io.shadowrealm.shade.map.ColorSpace;
import io.shadowrealm.shade.map.CompiledMap;
import io.shadowrealm.shade.map.MapColor;
import io.shadowrealm.shade.map.MapData;
import io.shadowrealm.shade.map.MapMood;
import io.shadowrealm.shade.map.MapMusic;
import io.shadowrealm.shade.map.MapPosition;
import io.shadowrealm.shade.map.MapRegion;
import io.shadowrealm.shade.map.MapSpawn;
import io.shadowrealm.shade.map.MapWarp;

public class MapBuilderSVC extends Service
{
	@Override
	public void onStart()
	{

	}

	@Override
	public void onStop()
	{

	}

	public void doCompile(PhantomSender sender)
	{
		Profiler px = new Profiler();
		px.begin();
		sender.sendMessage("Compiling Map");
		Cuboid c = WorldEditor.getSelection(sender.player());
		sender.sendMessage("===== Compiling Map =====");
		SVC.get(MapBuilderSVC.class).compile(c, false, new Callback<CompiledMap>()
		{
			@Override
			public void run(CompiledMap t)
			{
				px.end();
				sender.sendMessage("Map compiled in " + F.time(px.getMilliseconds(), 1) + " with " + t.getWarnings().size() + " warning" + (t.getWarnings().size() == 1 ? "" : "s") + ".");
				sender.sendMessage("Spawns: " + t.getMap().getSpawns().size());
				sender.sendMessage("Regions: " + t.getMap().getRegions().size());
				sender.sendMessage("Musical Regions: " + t.getMap().getMusic().size());
				sender.sendMessage("Color Samples: " + t.getMap().getColors().size());

				if(t.getWarnings().size() > 0)
				{
					GMap<String, Integer> warnings = new GMap<>();
					sender.sendMessage("Map compiled with " + t.getWarnings().size() + " warning" + (t.getWarnings().size() == 1 ? "" : " "));

					for(String i : t.getWarnings().v())
					{
						if(!warnings.containsKey(i))
						{
							warnings.put(i, 0);
						}

						warnings.put(i, warnings.get(i) + 1);
					}

					for(String i : warnings.k())
					{
						sender.sendMessage(C.RED + "!! " + C.WHITE + i + C.YELLOW + " in " + warnings.get(i) + " position" + (warnings.get(i) == 1 ? "" : " "));
					}

					MapPosition mp = t.getWarnings().k().get(0);
					sender.player().teleport(mp.getLocation(sender.player().getWorld()));
				}
			}
		});
	}

	public void compile(Cuboid cuboid, boolean prod, Callback<CompiledMap> mc)
	{
		MapData map = new MapData();
		CompiledMap c = new CompiledMap(map);
		MapPosition center = new MapPosition();
		center.setLocation(cuboid.getCenter());
		map.setCenter(center);
		scanForSigns(c, cuboid, new Callback<GList<Location>>()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run(GList<Location> t)
			{
				EditSession e = WorldEditor.getEditSession(cuboid.getWorld());
				e.setFastMode(true);
				e.enableQueue();

				for(Location i : t)
				{
					Sign s = (Sign) i.getBlock().getState();
					MapPosition pos = new MapPosition();

					if(s.getType().equals(Material.SIGN_POST))
					{
						org.bukkit.material.Sign ss = (org.bukkit.material.Sign) s.getData();
						Vector facing = new Vector(ss.getFacing().getModX(), ss.getFacing().getModY(), ss.getFacing().getModZ()).normalize();
						pos.setLocation(i.clone().setDirection(facing).add(0.5, 0.5, 0.5));
					}

					else
					{
						pos.setLocation(i.clone().add(0.5, 0.5, 0.5));
					}

					if(compile(s, pos, c) && prod)
					{
						try
						{
							e.setBlock(new com.sk89q.worldedit.Vector(i.getBlockX(), i.getBlockY(), i.getBlockZ()), new BaseBlock(0));
						}

						catch(MaxChangedBlocksException e1)
						{
							e1.printStackTrace();
						}
					}
				}

				if(map.getSpawns().isEmpty())
				{
					c.warn(new MapPosition(), "No Spawns Defined");
				}

				if(prod)
				{
					new A()
					{
						@Override
						public void run()
						{
							e.flushQueue();

							new S()
							{
								@Override
								public void run()
								{
									mc.run(c);
								}
							};
						}
					};
				}

				else
				{
					mc.run(c);
				}
			}
		});
	}

	private boolean compile(Sign s, MapPosition pos, CompiledMap c)
	{
		if(s.getLine(0).equalsIgnoreCase("[spawn]"))
		{
			if(s.getType().equals(Material.WALL_SIGN))
			{
				c.warn(pos, "Spawns should be marked with sign posts to indicate facing direction.");
			}

			MapSpawn spawn = new MapSpawn();
			spawn.setPosition(pos);
			c.getMap().getSpawns().add(spawn);
		}

		else if(s.getLine(0).equalsIgnoreCase("[region]"))
		{
			String mood = s.getLine(2);

			if(mood.trim().isEmpty())
			{
				c.warn(pos, "Regions should be marked with a mood on line 3 (light, dark or far).");
			}

			if(s.getLine(1).trim().isEmpty())
			{
				c.warn(pos, "Regions should be marked with a name on line 2");
			}

			MapRegion rg = new MapRegion();
			rg.setPosition(pos);
			rg.setName(s.getLine(1));
			rg.setMood(MapMood.LIGHT);

			try
			{
				rg.setMood(MapMood.parse(mood));
			}

			catch(Throwable e)
			{
				c.warn(pos, "Invalid Region Mood: " + mood);
			}

			c.getMap().getRegions().add(rg);
		}

		else if(s.getLine(0).trim().equalsIgnoreCase("[warp]"))
		{

			if(s.getLine(1).trim().trim().isEmpty())
			{
				c.warn(pos, "Warps should be marked with a name on line 2");
			}

			MapWarp rg = new MapWarp();
			rg.setPosition(pos);
			rg.setName(s.getLine(1).trim());

		}

		else if(s.getLine(0).trim().equalsIgnoreCase("[music]"))
		{
			String song = s.getLine(1).trim();
			MapMusic music = new MapMusic();
			music.setPosition(pos);
			boolean fail = false;

			try
			{
				Sound so = Sound.valueOf("RECORD_" + song.toUpperCase().trim());

				if(so == null)
				{
					f("Cannot find music '" + song + "'.");
					fail = true;
				}

				else
				{
					music.setMusic(so.name());
				}
			}

			catch(Throwable e)
			{
				f("Cannot find music '" + song + "'.");
				fail = true;
			}

			if(!fail)
			{
				try
				{
					music.setSpeed(Float.valueOf(s.getLine(2)));
				}

				catch(Throwable e)
				{
					music.setSpeed(1.15f);
				}

				c.getMap().getMusic().add(music);
			}

			else
			{
				f("Cannot find music '" + song + "'.");
			}
		}

		else
		{
			return false;
		}

		return true;
	}

	public void scanForSigns(CompiledMap c, Cuboid cuboid, Callback<GList<Location>> cb)
	{
		GList<Location> g = new GList<>();
		GList<ChunkSnapshot> snaps = new GList<>();

		for(Chunk i : cuboid.getChunks())
		{
			i.load();
			snaps.add(i.getChunkSnapshot(false, false, false));
		}

		new A()
		{
			@Override
			public void run()
			{
				for(ChunkSnapshot s : snaps)
				{
					GMap<Integer, GMap<C, Integer>> colorFound = new GMap<>();

					for(int i = 0; i < 16; i++)
					{
						for(int j = 0; j < 256; j++)
						{
							for(int k = 0; k < 16; k++)
							{
								if(s.getBlockType(i, j, k).equals(Material.AIR))
								{
									continue;
								}

								if(s.getBlockType(i, j, k).equals(Material.SIGN_POST) || s.getBlockType(i, j, k).equals(Material.WALL_SIGN))
								{
									g.add(new Location(cuboid.getWorld(), (s.getX() << 4) + i, j, (s.getZ() << 4) + k));
								}

								else
								{
									@SuppressWarnings("deprecation")
									C c = getColor(s.getBlockType(i, j, k), (byte) s.getBlockData(i, j, k));

									if(c != null)
									{
										int sec = j >> 4;

									if(!colorFound.containsKey(sec))
									{
										colorFound.put(sec, new GMap<>());
									}

									if(!colorFound.get(sec).containsKey(c))
									{
										colorFound.get(sec).put(c, 0);
									}

									colorFound.get(sec).put(c, colorFound.get(sec).get(c) + 1);
									}
								}
							}
						}
					}

					for(int i : colorFound.k())
					{
						MapPosition pos = new MapPosition();
						pos.setY(M.iclip((i << 4) + 8, 0, 256));
						pos.setX((s.getX() << 4) + 8);
						pos.setZ((s.getZ() << 4) + 8);
						MapColor color = new MapColor();
						color.setPosition(pos);
						color.setColor(colorFound.get(i).sortK().get(0).name());
						ColorSpace space = new ColorSpace();
						space.setX(s.getX() << 4);
						space.setY(i << 4);
						space.setZ(s.getZ() << 4);
						c.getMap().getColors().put(space, color);
					}
				}

				new S()
				{
					@Override
					public void run()
					{
						cb.run(g);
					}
				};
			}
		};
	}

	@SuppressWarnings("deprecation")
	protected C getColor(Material blockType, byte blockData)
	{
		try
		{
			switch(blockType)
			{
				case WOOL:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case CARPET:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case CONCRETE:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case CONCRETE_POWDER:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case STAINED_GLASS_PANE:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case STAINED_GLASS:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				case STAINED_CLAY:
					return C.dyeToChat(DyeColor.getByWoolData(blockData));
				default:
					break;
			}
		}

		catch(Throwable e)
		{

		}

		return null;
	}
}
