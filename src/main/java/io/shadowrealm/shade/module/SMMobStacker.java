package io.shadowrealm.shade.module;

import java.util.List;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.world.Area;
import mortar.bukkit.data.DataRipper;
import mortar.lang.collection.GMap;

public class SMMobStacker extends ShadeModule
{
	@Key("search-radius")
	public static double stackSearchRadius = 16;

	@Key("max-stack-size")
	public static int maxStackSize = 16;

	@Key("enable")
	public static boolean enabled = true;
	private GMap<World, List<Integer>> stackedMobs;

	public SMMobStacker()
	{
		super("MobStacker");
	}

	public void setCount(Entity a, int count)
	{
		a.setCustomName(count + "x");
		a.setCustomNameVisible(true);

		a.setMetadata("stack-count", new FixedMetadataValue(ShadeClient.instance, Math.max(1, count)));
	}

	public int getCount(Entity a)
	{
		if(isStacked(a))
		{
			return a.getMetadata("stack-count").get(0).asInt();
		}

		return 1;
	}

	private boolean isStacked(Entity a)
	{
		return a.hasMetadata("stack-count");
	}

	public boolean stack(Entity a, Entity b)
	{
		if(!a.getType().equals(b.getType()))
		{
			return false;
		}

		int newSize = getCount(a) + getCount(b);

		if(newSize > maxStackSize)
		{
			return false;
		}

		setCount(a, newSize);
		b.remove();
		return true;
	}

	public void setSpawnerEntity(Entity a, boolean b)
	{
		a.setMetadata("spawner-entity", new FixedMetadataValue(ShadeClient.instance, b));
	}

	public boolean isSpawnerEntity(Entity a)
	{
		return a.hasMetadata("spawner-entity") ? a.getMetadata("spawner-entity").get(0).asBoolean() : false;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void on(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) e.getEntity();

			if(getCount(e.getEntity()) > 1 && le.getHealth() - e.getFinalDamage() <= 0)
			{
				le.setHealth(le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				DataRipper.forceDeathSequence(le, le);
				setCount(e.getEntity(), getCount(e.getEntity()) - 1);
			}
		}
	}

	@EventHandler
	public void on(SpawnerSpawnEvent e)
	{
		setSpawnerEntity(e.getEntity(), true);

		for(Entity i : new Area(e.getLocation(), stackSearchRadius).getNearbyEntities())
		{
			if(i.getType().equals(e.getEntityType()) && isSpawnerEntity(i))
			{
				stack(i, e.getEntity());
				break;
			}
		}
	}

	@Override
	public void start()
	{

	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
