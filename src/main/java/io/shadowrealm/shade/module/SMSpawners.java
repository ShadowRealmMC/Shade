package io.shadowrealm.shade.module;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.fx.EffectCauldronAcceptRecipe;
import mortar.api.fx.EffectCauldronRejectRecipe;
import mortar.api.nms.Catalyst;
import mortar.api.sched.J;
import mortar.api.world.W;
import mortar.compute.math.M;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.util.text.C;

public class SMSpawners extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Key("engine-throttle")
	public static double throttle = 0.5;

	@Key("tweaks.player-max-distance")
	public static double maxDistance = 16;

	@Key("tweaks.spawn-interval")
	public static int spawnInterval = 60;

	@Key("tweaks.max-entities-nearby")
	public static int maxNearby = 5;

	@Key("optimizations.fast-entities")
	public static boolean fastEntities = true;

	private GMap<Chunk, Integer> beaconCache;

	public SMSpawners()
	{
		super("Spawners");
	}

	@Override
	public void start()
	{
		beaconCache = new GMap<>();
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@EventHandler
	public void on(ChunkUnloadEvent e)
	{
		beaconCache.remove(e.getChunk());
	}

	@EventHandler
	public void on(ChunkLoadEvent e)
	{
		getTier(e.getChunk());
	}

	public int getTier(Chunk c)
	{
		if(beaconCache.containsKey(c))
		{
			return beaconCache.get(c);
		}

		int tier = 0;

		for(BlockState i : c.getTileEntities())
		{
			if(i instanceof Beacon)
			{
				Beacon b = (Beacon) i;

				if(b.getTier() > tier)
				{
					tier = b.getTier();
				}
			}
		}

		beaconCache.put(c, tier);

		return tier;
	}

	@EventHandler
	public void on(BlockBreakEvent e)
	{
		J.s(() -> beaconCache.remove(e.getBlock().getChunk()), 20);
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		J.s(() -> beaconCache.remove(e.getBlock().getChunk()), 20);

		if(e.getBlock().getType().equals(Material.MOB_SPAWNER))
		{
			J.s(() ->
			{
				Block b = e.getBlock();
				if(b.getType().equals(Material.MOB_SPAWNER))
				{
					CreatureSpawner spawner = (CreatureSpawner) b.getState();
					spawner.setMaxNearbyEntities(maxNearby);
					spawner.setMinSpawnDelay(spawnInterval);
					spawner.setMaxSpawnDelay(spawnInterval);
					spawner.setRequiredPlayerRange((int) (maxDistance * maxDistance));
					spawner.setSpawnCount(1);
					spawner.update();
				}
			}, 5);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent e)
	{
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(e.getItem() == null || e.getItem().getType().equals(Material.AIR) || !e.getItem().getType().isBlock())
			{
				if(e.getClickedBlock().getType().equals(Material.MOB_SPAWNER))
				{
					CreatureSpawner spawner = (CreatureSpawner) e.getClickedBlock().getState();
					Catalyst.host.sendPacket(e.getPlayer(), Catalyst.host.packetActionBarMessage(C.GRAY + F.capitalizeWords(spawner.getSpawnedType().toString().toLowerCase().replaceAll("_", " ")) + " Spawner " + M.toRoman(spawner.getSpawnCount())));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(SpawnerSpawnEvent e)
	{
		J.a(() -> updateSpawnerLevelAsync(e.getSpawner()));
		Entity entity = e.getEntity();
		entity.setSilent(true);

		if(entity instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity;
			le.setAI(false);
			le.setCollidable(false);
			le.setCanPickupItems(false);
			le.setRemoveWhenFarAway(true);
		}
	}

	private void updateSpawnerLevelAsync(CreatureSpawner spawner)
	{
		try
		{
			int tier = 0;

			for(Chunk i : W.chunkRadius(spawner.getChunk(), 2))
			{
				int ct = getTier(i);

				if(ct > tier)
				{
					tier = ct;
				}
			}

			int level = tier + 1;

			if(spawner.getSpawnCount() != level)
			{
				J.s(() ->
				{
					if(level > spawner.getSpawnCount())
					{
						new EffectCauldronAcceptRecipe().play(spawner.getLocation().clone().add(0.5, 0.5, 0.5));
					}

					else
					{
						new EffectCauldronRejectRecipe().play(spawner.getLocation().clone().add(0.5, 0.5, 0.5));
					}

					spawner.setSpawnCount(level);
					spawner.setDelay(spawnInterval);
					spawner.update();
				});
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
