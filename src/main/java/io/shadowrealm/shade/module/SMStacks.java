package io.shadowrealm.shade.module;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.sched.J;
import mortar.bukkit.command.Permission;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;

public class SMStacks extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = false;

	@Key("remove-glass-bottles")
	public static boolean removeBottles = true;

	@Key("prevent-destruction-from-world")
	public static boolean preventDestruction = true;

	//@builder
	@Key("override-max-size")
	public static GList<String> stackOverrides = new GList<String>()
	.qadd("POTION=64")
	.qadd("LINGERING_POTION=64")
	.qadd("SPLASH_POTION=64")
	.qadd("BUCKET=64")
	.qadd("LAVA_BUCKET=64")
	.qadd("WATER_BUCKET=64")
	.qadd("MILK_BUCKET=64")
	.qadd("SPLASH_POTION=64");
	//@done

	//@builder
	@Key("override-use-cooldown-ticks")
	public static GList<String> cooldownUseOverrides = new GList<String>()
	.qadd("ENDER_PEARL=35")
	.qadd("LINGERING_POTION=15")
	.qadd("SPLASH_POTION=7");
	//@done

	//@builder
	@Key("override-consume-cooldown-ticks")
	public static GList<String> cooldownConsumeOverrides = new GList<String>()
	.qadd("POTION=5")
	.qadd("GOLDEN_APPLE=65");
	//@done

	private GMap<Material, Integer> overridesStackSize;
	private GMap<Material, Integer> overridesCooldownUse;
	private GMap<Material, Integer> overridesCooldownConsume;

	@Permission
	public static PermissionShade perm;

	public SMStacks()
	{
		super("Stacks");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityDamageByBlockEvent e)
	{
		if(!preventDestruction)
		{
			return;
		}

		if(e.getEntityType().equals(EntityType.DROPPED_ITEM))
		{
			e.setCancelled(true);
		}
	}

	@Override
	public void start()
	{
		overridesCooldownUse = new GMap<>();
		overridesCooldownConsume = new GMap<>();
		overridesStackSize = new GMap<>();

		for(String i : stackOverrides)
		{
			if(i.contains("="))
			{
				try
				{
					overridesStackSize.put(Material.valueOf(i.split("\\Q=\\E")[0]), Integer.valueOf(i.split("\\Q=\\E")[1]));
				}

				catch(Throwable e)
				{
					f("Cannot process: " + i + ", Ignoring.");
				}
			}
		}

		for(String i : cooldownUseOverrides)
		{
			if(i.contains("="))
			{
				try
				{
					overridesCooldownUse.put(Material.valueOf(i.split("\\Q=\\E")[0]), Integer.valueOf(i.split("\\Q=\\E")[1]));
				}

				catch(Throwable e)
				{
					f("Cannot process: " + i + ", Ignoring.");
				}
			}
		}

		for(String i : cooldownConsumeOverrides)
		{
			if(i.contains("="))
			{
				try
				{
					overridesCooldownConsume.put(Material.valueOf(i.split("\\Q=\\E")[0]), Integer.valueOf(i.split("\\Q=\\E")[1]));
				}

				catch(Throwable e)
				{
					f("Cannot process: " + i + ", Ignoring.");
				}
			}
		}
	}

	@EventHandler
	public void on(PlayerItemConsumeEvent e)
	{
		if(removeBottles && e.getItem().getType().equals(Material.POTION))
		{
			J.s(() -> removeOneBottle(e.getPlayer()));
		}

		if(overridesCooldownConsume.containsKey(e.getItem().getType()))
		{
			J.s(() -> e.getPlayer().setCooldown(e.getItem().getType(), overridesCooldownConsume.get(e.getItem().getType())));
		}
	}

	@EventHandler
	public void on(ProjectileLaunchEvent e)
	{
		if(e.getEntity().getShooter() instanceof Player)
		{
			Player p = (Player) e.getEntity().getShooter();

			if(e.getEntity() instanceof EnderPearl)
			{
				used(p, Material.ENDER_PEARL);
			}

			if(e.getEntity() instanceof SplashPotion)
			{
				used(p, Material.SPLASH_POTION);
			}

			if(e.getEntity() instanceof LingeringPotion)
			{
				used(p, Material.LINGERING_POTION);
			}

			if(e.getEntity() instanceof Egg)
			{
				used(p, Material.EGG);
			}
		}
	}

	private void used(Player p, Material mat)
	{
		if(overridesCooldownUse.containsKey(mat))
		{
			J.s(() -> p.setCooldown(mat, overridesCooldownUse.get(mat)));
		}
	}

	private void removeOneBottle(Player p)
	{
		ItemStack[] is = p.getInventory().getContents();

		for(int i = 0; i < is.length; i++)
		{
			if(is[i] != null)
			{
				if(is[i].getType().equals(Material.GLASS_BOTTLE))
				{
					ItemStack stack = is[i].clone();
					if(stack.getAmount() > 1)
					{
						stack.setAmount(stack.getAmount() - 1);
					}

					else
					{
						stack = null;
					}

					is[i] = stack;
					break;
				}
			}
		}

		p.getInventory().setContents(is);
		p.updateInventory();
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	public int getMaxStackSize(ItemStack is)
	{
		return shouldOverride(is) ? overridesStackSize.get(is.getType()) : is.getType().getMaxStackSize();
	}

	public boolean shouldOverride(ItemStack is)
	{
		return overridesStackSize.containsKey(is.getType());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(InventoryDragEvent e)
	{
		ItemStack is = e.getOldCursor();

		if(is != null && shouldOverride(is))
		{
			int count = getMaxStackSize(is);
			int left = e.getOldCursor().getAmount();
			int div = e.getRawSlots().size();
			int f = left / div;

			if(f <= 1)
			{
				return;
			}

			e.setCancelled(true);
			for(int i : e.getRawSlots())
			{
				int place = Math.min(f, count);
				ItemStack ix = e.getView().getItem(i);

				if(ix == null || ix.getType().equals(Material.AIR))
				{
					ItemStack iv = is.clone();
					iv.setAmount(place);
					e.getView().setItem(i, iv);

					left -= place;
				}
			}

			int ll = left;

			J.s(() ->
			{
				if(ll == 0)
				{
					e.getWhoClicked().setItemOnCursor(null);
				}

				else
				{
					ItemStack ss = e.getWhoClicked().getItemOnCursor().clone();
					ss.setAmount(ll);
					e.getWhoClicked().setItemOnCursor(ss);
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(EntityPickupItemEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();

			if(shouldOverride(e.getItem().getItemStack()))
			{
				ItemStack is = e.getItem().getItemStack().clone();
				e.setCancelled(true);
				addToInventory(p.getInventory(), is);
				// Catalyst.host.sendRangedPacket(16, e.getEntity().getLocation(), new
				// PacketPlayOutCollect(e.getItem().getEntityId(), e.getEntity().getEntityId(),
				// 1));
				e.getItem().remove();
			}
		}
	}

	public void stack(ItemStack ist, Inventory inv, HumanEntity e, int hintSlot)
	{
		int count = 0;
		ItemStack[] isx = inv.getContents();
		ItemStack demo = ist.clone();

		for(int i = 0; i < isx.length; i++)
		{
			ItemStack is = isx[i];

			if(is != null && is.getType().equals(ist.getType()) && is.getDurability() == ist.getDurability())
			{
				count += is.getAmount();
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}

		while(count > 0)
		{
			int a = Math.min(count, 64);
			demo.setAmount(a);
			count -= a;
			addToInventory(inv, demo.clone(), hintSlot);
		}

		if(e instanceof Player)
		{
			((Player) e).updateInventory();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(InventoryClickEvent e)
	{
		if(e.getCurrentItem() == null)
		{
			return;
		}

		ItemStack is = e.getCurrentItem().clone();
		ItemStack cursor = e.getCursor();
		Inventory top = e.getView().getTopInventory();
		Inventory bottom = e.getView().getBottomInventory();
		Inventory clickedInventory = e.getClickedInventory();
		int clickedSlot = e.getSlot();

		if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
		{
			if(bottom != null && top != null)
			{
				Inventory other = bottom.equals(clickedInventory) ? top : bottom;

				if(is != null && shouldOverride(is))
				{
					J.s(() -> stack(is, other, e.getWhoClicked(), 0));
				}
			}
		}

		if(e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR))
		{
			if(cursor != null && shouldOverride(cursor))
			{
				int stack = getMaxStackSize(cursor);

				ItemStack[] isx = e.getClickedInventory().getContents();

				for(int i = 0; i < isx.length; i++)
				{
					ItemStack isv = isx[i];

					if(isv != null && isv.getType().equals(cursor.getType()) && isv.getDurability() == cursor.getDurability())
					{
						if(cursor.getAmount() < stack)
						{
							if(cursor.getAmount() + isv.getAmount() <= stack)
							{
								cursor.setAmount(cursor.getAmount() + isv.getAmount());
								e.getClickedInventory().setItem(i, new ItemStack(Material.AIR));
								e.setCursor(cursor.clone());
							}
						}

						else
						{
							break;
						}
					}
				}

				if(cursor.getAmount() < stack && bottom != null && top != null)
				{
					Inventory other = bottom.equals(clickedInventory) ? top : bottom;

					isx = other.getContents();

					for(int i = 0; i < isx.length; i++)
					{
						ItemStack isv = isx[i];

						if(isv != null && isv.getType().equals(cursor.getType()) && isv.getDurability() == cursor.getDurability())
						{
							if(cursor.getAmount() < stack)
							{
								if(cursor.getAmount() + isv.getAmount() <= stack)
								{
									cursor.setAmount(cursor.getAmount() + isv.getAmount());
									other.setItem(i, new ItemStack(Material.AIR));
									e.setCursor(cursor.clone());
								}
							}

							else
							{
								break;
							}
						}
					}
				}
			}
		}

		if(e.getAction().equals(InventoryAction.NOTHING) || e.getAction().equals(InventoryAction.PICKUP_SOME) || e.getAction().equals(InventoryAction.PICKUP_ONE))
		{
			if(e.getClick().equals(ClickType.RIGHT))
			{
				if(cursor != null && shouldOverride(cursor))
				{
					if(is != null && cursor.getType().equals(is.getType()) && cursor.getDurability() == is.getDurability())
					{
						e.setCancelled(true);

						if(cursor != null && shouldOverride(cursor))
						{
							if(is != null && cursor.getType().equals(is.getType()) && cursor.getDurability() == is.getDurability())
							{
								int count = getMaxStackSize(cursor);
								int maxPull = count - is.getAmount();

								if(cursor.getAmount() == 1 && is.getAmount() < count)
								{
									is.setAmount(is.getAmount() + 1);
									clickedInventory.setItem(clickedSlot, is.clone());
									e.setCursor(new ItemStack(Material.AIR));
								}

								else if(maxPull > 0 && cursor.getAmount() > 1)
								{
									cursor.setAmount(cursor.getAmount() - 1);
									e.setCursor(cursor.clone());
									is.setAmount(is.getAmount() + 1);
									clickedInventory.setItem(clickedSlot, is.clone());
								}
							}
						}

					}
				}
			}

			if(e.getClick().equals(ClickType.LEFT))
			{
				if(cursor != null && shouldOverride(cursor))
				{
					if(is != null && cursor.getType().equals(is.getType()) && cursor.getDurability() == is.getDurability())
					{
						e.setCancelled(true);

						if(cursor != null && shouldOverride(cursor))
						{
							if(is != null && cursor.getType().equals(is.getType()) && cursor.getDurability() == is.getDurability())
							{
								int count = getMaxStackSize(cursor);
								int maxPull = count - is.getAmount();

								while(maxPull > 0 && cursor.getAmount() > 1)
								{
									cursor.setAmount(cursor.getAmount() - 1);
									e.setCursor(cursor.clone());
									is.setAmount(is.getAmount() + 1);
									clickedInventory.setItem(clickedSlot, is.clone());
								}

								if(cursor.getAmount() == 1 && is.getAmount() < count)
								{
									is.setAmount(is.getAmount() + 1);
									clickedInventory.setItem(clickedSlot, is.clone());
									e.setCursor(new ItemStack(Material.AIR));
								}
							}
						}

					}
				}
			}
		}
	}

	public void addToInventory(Inventory inv, ItemStack is, int hint)
	{
		ItemStack[] iss = inv.getContents();
		int left = is.getAmount();
		int z = getMaxStackSize(is);

		for(int i = 0; i < iss.length; i++)
		{
			if(left == 0)
			{
				break;
			}

			if(iss[i] != null)
			{
				ItemStack ic = iss[i].clone();

				if(ic.getType().equals(is.getType()) && ic.getDurability() == is.getDurability())
				{
					while(ic.getAmount() < z && left > 0)
					{
						ic.setAmount(ic.getAmount() + 1);
						left--;
					}

					iss[i] = ic.clone();
				}
			}
		}

		inv.setContents(iss);

		while(left > 0)
		{
			ItemStack ix = is.clone();
			ix.setAmount(Math.min(left, z));
			left = left - Math.min(left, z);

			if(inv.getContents()[hint] == null)
			{
				inv.setItem(hint, ix);
			}

			else
			{
				inv.addItem(ix);
			}
		}
	}

	public void addToInventory(Inventory inv, ItemStack is)
	{
		addToInventory(inv, is, 0);
	}
}
