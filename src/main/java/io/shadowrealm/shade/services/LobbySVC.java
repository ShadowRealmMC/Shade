package io.shadowrealm.shade.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.nustaq.serialization.FSTConfiguration;

import com.sk89q.worldedit.EditSession;
import com.volmit.phantom.json.JSONException;
import com.volmit.phantom.json.JSONObject;
import com.volmit.phantom.lang.Callback;
import com.volmit.phantom.lang.GList;
import com.volmit.phantom.lang.VIO;
import com.volmit.phantom.plugin.AsyncTickService;
import com.volmit.phantom.plugin.S;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.rift.Rift;
import com.volmit.phantom.rift.RiftException;
import com.volmit.phantom.rift.VoidGenerator;
import com.volmit.phantom.services.LightSVC;
import com.volmit.phantom.services.RiftSVC;
import com.volmit.phantom.services.WorldEditSVC;
import com.volmit.phantom.util.Cuboid;

import io.shadowrealm.shade.map.ActiveMap;
import io.shadowrealm.shade.map.CompiledMap;
import io.shadowrealm.shade.map.config.MapConfig;

public class LobbySVC extends AsyncTickService
{
	private Rift rift;
	private MapConfig conf;
	private CompiledMap map;
	private ActiveMap activeMap;
	private Cuboid region;
	private GList<MapConfig> configs;

	public LobbySVC()
	{
		super(0);
		configs = new GList<>();
	}

	@Override
	public void onBegin()
	{
		handleFiles();

		new S(20)
		{
			@Override
			public void run()
			{
				if(rift == null)
				{
					try
					{
						if(getPotentialMaps().isEmpty())
						{
							f("No potential maps found!");
							return;
						}

						conf = getPotentialMaps().pickRandom();
						l("Selected Map: " + conf.getMapName() + " // " + conf.getVariationName() + " (" + conf.getSchematic() + ")");
					}

					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}

				rift = createRift();
				rift.load();

				w("No Cached Map! We must build it from scratch!");

				try
				{
					constructMap();
					compileMap();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
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

		l("Lobby Service Fully Operational!");
	}

	private void compileMap()
	{
		l("Compiling Map");
		SVC.get(MapBuilderSVC.class).compile(region, true, new Callback<CompiledMap>()
		{
			@Override
			public void run(CompiledMap t)
			{
				l("Map Compiled");
				map = t;
				l(map.getWarnings().size() + " Warnings");
				l("Saving Cache...");

				try
				{
					FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
					Object p = map.getMap();
					ByteArrayInputStream os = new ByteArrayInputStream(conf.asByteArray(p));
					File f = new File(rift.getWorldFolder(), "cache.fst");
					FileOutputStream fos = new FileOutputStream(f);
					VIO.fillTransfer(os, fos);
					fos.close();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}

				l("Starting Map!");
				startMap();

			}
		});
	}

	private void constructMap() throws Throwable
	{
		l("Constructing Map from schematic: " + conf.getSchematic());
		File schematic = new File(getSchematicFolder(), conf.getSchematic());
		WorldEditSVC w = SVC.get(WorldEditSVC.class);
		EditSession e = w.getEditSession(rift.getWorld());
		l("Reading Map Schematic " + conf.getSchematic());
		Vector v = w.getOffset(schematic);
		Cuboid rg = w.getCuboid(rift.getWorld(), w.getSchematic(schematic).getClipboard().getRegion());
		//@builder
		region = new Cuboid(
				new Location(rift.getWorld(), rg.getSizeX() /2, 255, rg.getSizeZ()/2).clone().add(v),
				new Location(rift.getWorld(), -rg.getSizeX()/2, 0, -rg.getSizeZ()/2).clone().add(v));
		//@done

		l(region.getLowerNE(), region.getUpperSW());
		rift.setWorldBorderCenter(region.getCenter().getX(), region.getCenter().getZ());
		rift.setWorldBorderEnabled(true);
		rift.setWorldBorderSize(Math.max(region.getSizeX(), region.getSizeZ()) * 3);
		l("Streaming Blocks from schematic into rift");
		l(v.toString());
		w.pasteSchematic(schematic, e, new Location(rift.getWorld(), v.getX(), -v.getY(), v.getZ()));
		e.flushQueue();
		l("Touching up map lighting");
		SVC.get(LightSVC.class).relight(region);
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
					.setItemMergeRadius(10)
					.setItemDespawnRate(100)
					.setEntityTickLimit(0.51)
					.setTileTickLimit(0.51)
					.setPhysicsThrottle(100)
					.setAnimalActivationRange(5)
					.setPlayerTrackingRange(64)
					.setMiscActivationRange(5)
					.setMonsterActivationRange(5)
					.setTemporary(false)
					.setGenerator(VoidGenerator.class)
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
}
