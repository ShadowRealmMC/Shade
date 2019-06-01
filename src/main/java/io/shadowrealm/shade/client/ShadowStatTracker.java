package io.shadowrealm.shade.client;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import mortar.api.sched.J;
import mortar.bukkit.plugin.Controller;
import mortar.logic.format.F;

@SuppressWarnings("deprecation")
public class ShadowStatTracker extends Controller
{
	@Override
	public void start()
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> start(), 2);
			return;
		}
	}

	@Override
	public void stop()
	{

	}

	@Override
	public void tick()
	{

	}

	private void s(Player p, String s, double v)
	{
		Shade.getStatistics(p).get(s).add(v);
	}

	private void smax(Player p, String s, double v)
	{
		if(v > Shade.getStatistics(p).get(s).doubleValue())
		{
			Shade.getStatistics(p).get(s).set(v);
		}
	}

	@EventHandler
	public void on(PlayerStatisticIncrementEvent e)
	{
		switch(e.getStatistic())
		{
			case ANIMALS_BRED:
				s(e.getPlayer(), F.capitalizeWords(e.getStatistic().name().toLowerCase().replaceAll("_", " ")), e.getNewValue() - e.getPreviousValue());
				break;
			case ARMOR_CLEANED:
				s(e.getPlayer(), F.capitalizeWords(e.getStatistic().name().toLowerCase().replaceAll("_", " ")), e.getNewValue() - e.getPreviousValue());
				break;
			case BANNER_CLEANED:
				s(e.getPlayer(), F.capitalizeWords(e.getStatistic().name().toLowerCase().replaceAll("_", " ")), e.getNewValue() - e.getPreviousValue());
				break;
			case BEACON_INTERACTION:
				s(e.getPlayer(), F.capitalizeWords(e.getStatistic().name().toLowerCase().replaceAll("_", " ")), e.getNewValue() - e.getPreviousValue());
				break;
			case BREWINGSTAND_INTERACTION:
				s(e.getPlayer(), "Potions Brewed", e.getNewValue() - e.getPreviousValue());
				break;
			case CAKE_SLICES_EATEN:
				s(e.getPlayer(), "Cake Eaten", e.getNewValue() - e.getPreviousValue());
				break;
			case CAULDRON_FILLED:
				s(e.getPlayer(), "Cauldrons Filled", e.getNewValue() - e.getPreviousValue());
				break;
			case CAULDRON_USED:
				s(e.getPlayer(), "Cauldrons Used", e.getNewValue() - e.getPreviousValue());
				break;
			case CHEST_OPENED:
				s(e.getPlayer(), "Chests Opened", e.getNewValue() - e.getPreviousValue());
				break;
			case CRAFT_ITEM:
				s(e.getPlayer(), "Items Crafted", e.getNewValue() - e.getPreviousValue());
				break;
			case DAMAGE_DEALT:
				s(e.getPlayer(), "Damage Dealt", (e.getNewValue() - e.getPreviousValue()) / 10D);
				break;
			case FISH_CAUGHT:
				s(e.getPlayer(), "Fish Caught", e.getNewValue() - e.getPreviousValue());
				break;
			case FLOWER_POTTED:
				s(e.getPlayer(), "Flowers Potted", e.getNewValue() - e.getPreviousValue());
				break;
			case ITEM_ENCHANTED:
				s(e.getPlayer(), "Items Enchanted", e.getNewValue() - e.getPreviousValue());
				break;
			case JUMP:
				s(e.getPlayer(), "Jumps", e.getNewValue() - e.getPreviousValue());
				break;
			case MOB_KILLS:
				s(e.getPlayer(), "Mobs Slain", e.getNewValue() - e.getPreviousValue());
				break;
			case NOTEBLOCK_TUNED:
				s(e.getPlayer(), "Noteblocks Tuned", e.getNewValue() - e.getPreviousValue());
				break;
			case PLAYER_KILLS:
				s(e.getPlayer(), "Players Slain", e.getNewValue() - e.getPreviousValue());
				break;
			case RECORD_PLAYED:
				s(e.getPlayer(), "Records Played", e.getNewValue() - e.getPreviousValue());
				break;
			case SHULKER_BOX_OPENED:
				s(e.getPlayer(), "Shulker Boxes Opened", e.getNewValue() - e.getPreviousValue());
				break;
			case TALKED_TO_VILLAGER:
				s(e.getPlayer(), "Villager Conversations", e.getNewValue() - e.getPreviousValue());
				break;
			case TRADED_WITH_VILLAGER:
				s(e.getPlayer(), "Villager Trades", e.getNewValue() - e.getPreviousValue());
				break;
			case TRAPPED_CHEST_TRIGGERED:
				s(e.getPlayer(), "Trapped Chests Triggered", e.getNewValue() - e.getPreviousValue());
				break;
			case USE_ITEM:
				s(e.getPlayer(), "Items Used", e.getNewValue() - e.getPreviousValue());
				break;
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			s((Player) e.getEntity(), "Damage Taken", e.getFinalDamage());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerToggleSneakEvent e)
	{
		s(e.getPlayer(), "Tactical Crouches", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerShearEntityEvent e)
	{
		s(e.getPlayer(), "Mobs Sheared", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerPickupItemEvent e)
	{
		s(e.getPlayer(), "Items Collected", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerLevelChangeEvent e)
	{
		s(e.getPlayer(), "XP Level Ups", 1);
		smax(e.getPlayer(), "Highest XP Level", e.getNewLevel());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerLeashEntityEvent e)
	{
		s(e.getPlayer(), "Mobs Leashed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerPickupArrowEvent e)
	{
		s(e.getPlayer(), "Arrows Recycled", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerKickEvent e)
	{
		s(e.getPlayer(), "Times Kicked", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerItemConsumeEvent e)
	{
		s(e.getPlayer(), "Items Consumed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerItemBreakEvent e)
	{
		s(e.getPlayer(), "Tools Destroyed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerFishEvent e)
	{
		s(e.getPlayer(), "Fishing Rods Cast", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerEggThrowEvent e)
	{
		s(e.getPlayer(), "Eggs Thrown", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerExpChangeEvent e)
	{
		s(e.getPlayer(), "XP Earned", e.getAmount());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerEditBookEvent e)
	{
		s(e.getPlayer(), "Books Edited", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerDropItemEvent e)
	{
		s(e.getPlayer(), "Items Dropped", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerDeathEvent e)
	{
		s(e.getEntity(), "Deaths", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e)
	{
		s(e.getPlayer(), "Chat Messages Sent", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerCommandPreprocessEvent e)
	{
		s(e.getPlayer(), "Commands Executed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerBucketEmptyEvent e)
	{
		s(e.getPlayer(), "Buckets Emptied", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerBucketFillEvent e)
	{
		s(e.getPlayer(), "Buckets Filled", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerBedEnterEvent e)
	{
		s(e.getPlayer(), "Naps (Bed Use)", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerAdvancementDoneEvent e)
	{
		s(e.getPlayer(), "Advancements Completed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockBreakEvent e)
	{
		s(e.getPlayer(), "Blocks Broken", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(BlockPlaceEvent e)
	{
		s(e.getPlayer(), "Blocks Placed", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerChangedWorldEvent e)
	{
		s(e.getPlayer(), "World Joins", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerJoinEvent e)
	{
		s(e.getPlayer(), ClientConfig.SERVER__NAME + " Joins", 1);
		s(e.getPlayer(), "Server Joins", 1);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerMoveEvent e)
	{
		if(!e.getTo().getWorld().equals(e.getFrom().getWorld()))
		{
			return;
		}

		s(e.getPlayer(), "Blocks Traveled", e.getFrom().distance(e.getTo()));
	}
}
