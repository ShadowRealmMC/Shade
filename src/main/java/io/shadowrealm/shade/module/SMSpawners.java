package io.shadowrealm.shade.module;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.sched.J;
import mortar.lang.collection.GMap;

public class SMSpawners extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	public GMap<EntityType, SpawnerStackingConfig> spawnerConfig;

	public SMSpawners()
	{
		super("Spawners");
	}

	@Override
	public void start()
	{
		spawnerConfig = new GMap<>();
		for(EntityType i : EntityType.values())
		{
			if(i.isAlive())
			{
				spawnerConfig.put(i, new SpawnerStackingConfig(i));

				try
				{
					spawnerConfig.get(i).load(ShadeClient.instance.getDataFile("modules", getName()));
					v("Loaded Entity Stack Spawner: " + i.name().toLowerCase());
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockPlaceEvent e)
	{
		if(e.getBlock().getType().equals(Material.MOB_SPAWNER))
		{
			J.s(() ->
			{
				if(e.getBlock().getType().equals(Material.MOB_SPAWNER))
				{
					CreatureSpawner s = (CreatureSpawner) e.getBlock().getState();
					EntityType type = s.getSpawnedType();
					SpawnerStackingConfig config = spawnerConfig.get(type);

					if(config != null)
					{
						if(config.isDisabledSpawner())
						{
							// TODO Better preventing & notify
							e.getBlock().breakNaturally();
						}

						else
						{
							updateSpawner(s);
						}
					}
				}
			});
		}
	}

	public int getSpawnerLevel(CreatureSpawner s)
	{
		try
		{
			BlockMetaStackedSpawner m = new BlockMetaStackedSpawner(s);
			m.load();
			return m.getLevel();
		}

		catch(Throwable e)
		{

		}

		return 1;
	}

	public void setSpawnerLevel(CreatureSpawner s, int level)
	{
		BlockMetaStackedSpawner m = new BlockMetaStackedSpawner(s);
		m.setLevel(level);
		m.save();
	}

	private void updateSpawner(CreatureSpawner s)
	{

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

				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(SpawnerSpawnEvent e)
	{
		CreatureSpawner s = e.getSpawner();
		Entity entity = e.getEntity();
		entity.setSilent(true);

		if(entity instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity;
			le.setCollidable(false);
			le.setCanPickupItems(false);
			le.setRemoveWhenFarAway(true);
		}
	}
}
