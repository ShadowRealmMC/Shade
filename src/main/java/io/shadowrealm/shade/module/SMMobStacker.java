package io.shadowrealm.shade.module;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.sched.J;
import mortar.api.world.Area;
import mortar.compute.math.M;
import mortar.logic.format.F;
import mortar.util.text.C;

public class SMMobStacker extends ShadeModule
{
	@Key("search-radius")
	public static double stackSearchRadius = 16;

	@Key("max-stack-size")
	public static int maxStackSize = 30;

	@Key("enable")
	public static boolean enabled = true;

	public SMMobStacker()
	{
		super("MobStacker");
	}

	public void setCount(Entity a, int count)
	{
		if(a instanceof LivingEntity)
		{
			// Prevent entities from duping equipment drops
			LivingEntity le = (LivingEntity) a;
			le.getEquipment().setBootsDropChance(0f);
			le.getEquipment().setHelmetDropChance(0f);
			le.getEquipment().setChestplateDropChance(0f);
			le.getEquipment().setLeggingsDropChance(0f);
			le.getEquipment().setItemInMainHandDropChance(0f);
			le.getEquipment().setItemInOffHandDropChance(0f);
		}

		a.setMetadata("stack-count", new FixedMetadataValue(ShadeClient.instance, Math.max(1, count)));
		showStack(a);
	}

	public void showStack(Entity e)
	{
		int stackSize = getCount(e);

		if(stackSize > 1)
		{
			String sx = F.capitalizeWords(C.WHITE + e.getType().toString().toLowerCase().replaceAll("_", " ")) + " " + C.GRAY + M.toRoman(stackSize);
			e.setCustomName(sx);
			J.s(() ->
			{
				if(!e.isDead() && e.getCustomName().equals(sx))
				{
					e.setCustomName("");
				}
			}, 50);
		}
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void on(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && !(e.getEntity() instanceof Player))
		{
			showStack(e.getEntity());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDeathEvent e)
	{
		if(e.getEntity() instanceof LivingEntity)
		{
			if(getCount(e.getEntity()) > 1)
			{
				for(ItemStack i : e.getDrops())
				{
					i.setAmount((int) (i.getAmount() * getCount(e.getEntity()) * (1D - (Math.random() * Math.random()))));
				}

				e.setDroppedExp(e.getDroppedExp() * getCount(e.getEntity()));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(SpawnerSpawnEvent e)
	{
		setSpawnerEntity(e.getEntity(), true);

		for(Entity i : new Area(e.getLocation(), stackSearchRadius).getNearbyEntities())
		{
			if(i.getType().equals(e.getEntityType()) && isSpawnerEntity(i))
			{
				if(stack(i, e.getEntity()))
				{
					break;
				}
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
