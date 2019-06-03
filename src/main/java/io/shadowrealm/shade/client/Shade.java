package io.shadowrealm.shade.client;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.ServerEffect;
import io.shadowrealm.shade.common.ServerEffects;
import io.shadowrealm.shade.common.Statistics;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.messages.RAccount;
import io.shadowrealm.shade.common.messages.RError;
import io.shadowrealm.shade.common.messages.RGetAccount;
import io.shadowrealm.shade.common.messages.ROK;
import io.shadowrealm.shade.common.messages.RScheduleReboot;
import io.shadowrealm.shade.common.messages.RSetSettings;
import io.shadowrealm.shade.common.messages.RSpendUnlock;
import io.shadowrealm.shade.common.messages.RUnlock;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowRank;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.sched.J;
import mortar.api.world.P;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.MortarAPIPlugin;
import mortar.compute.math.M;
import mortar.lang.collection.Callback;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONObject;
import mortar.lib.control.MojangProfileController;
import mortar.logic.format.F;
import mortar.util.text.C;

public class Shade
{
	private static ServerEffects effects = new ServerEffects();
	private static final GMap<String, ConnectableServer> servers = new GMap<>();

	/**
	 * Check if a booster is active
	 *
	 * @param id
	 *            the id of the booster
	 * @return true if it is
	 */
	public static boolean isBoosterActive(String id)
	{
		for(ServerEffect i : effects.getEffects())
		{
			if(i.getId().equals(id))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Get active booster time remaining ms
	 *
	 * @param id
	 *            the booster
	 * @return the time remaining or negative
	 */
	public static long getActiveBoosterTimeRemaining(String id)
	{
		for(ServerEffect i : effects.getEffects())
		{
			if(i.getId().equals(id))
			{
				return i.getEndsAt() - M.ms();
			}
		}

		return -1;
	}

	/**
	 * Get all active boosters
	 *
	 * @return the boosters
	 */
	public static GList<ServerEffect> getActiveBoosters()
	{
		return new GList<>(effects.getEffects());
	}

	/**
	 * Get all unlocks by category
	 *
	 * @param category
	 *            the category
	 * @return a list of unlocks. Or an empty list if the category doesnt exist
	 */
	public static GList<ShadowUnlock> getUnlocksForType(String category)
	{
		GList<ShadowUnlock> u = new GList<>();

		for(ShadowUnlock i : getUnlocks())
		{
			if(i.getType().equals(category))
			{
				u.add(i);
			}
		}

		return u;
	}

	/**
	 * Get the total amount of shadow xp the player has ever earned
	 *
	 * @param player
	 *            the player
	 * @return the total xp
	 */
	public static long getTotalShadowXPEarned(Player player)
	{
		return getAccount(player).getShadowXP();
	}

	/**
	 * Get the total amount of shadow xp the player has ever earned
	 *
	 * @param player
	 *            the player
	 * @return the total xp
	 */
	public static long getTotalShadowXPEarned(UUID player)
	{
		return getAccount(player).getShadowXP();
	}

	/**
	 * Compute the player's rank based on SXP
	 *
	 * @param player
	 *            the player
	 * @return the shadow rank
	 */
	public static ShadowRank computeRank(Player player)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).computeRank(getAccount(player));
	}

	/**
	 * Compute the player's rank based on SXP
	 *
	 * @param player
	 *            the player
	 * @return the shadow rank
	 */
	public static ShadowRank computeRank(UUID player)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).computeRank(getAccount(player));
	}

	/**
	 * Schedules a network organized reboot to the proxy
	 *
	 * @param inMillisecondsUntilReboot
	 *            the amount of milliseconds before the reboot. Overwrites the
	 *            previous scheduled reboot.
	 */
	public static void scheduleNetworkReboot(long inMillisecondsUntilReboot)
	{
		new RScheduleReboot().in(inMillisecondsUntilReboot).completeBlind(Shade.connect());
	}

	/**
	 * The proxy auto-schedules reboots for servers. This is the next scheduled time
	 * for the proxy
	 *
	 * @return if the value is negative, there is no scheduled reboot
	 */
	public static long getTimeUntilReboot()
	{
		return ShadeClient.rebootSchedule - M.ms();
	}

	/**
	 * Call this async as it runs netcode on the run thread. Check the return
	 * result. The proxy may deny you for several reasons, do not give the reward if
	 * it returns false, this will desync the player's account from the network
	 *
	 * @param p
	 *            the player
	 * @param id
	 *            the unlock id
	 * @return true if the proxy has accepted this transaction
	 */
	public static boolean consumeUnlock(Player p, String id)
	{
		if(!getUnlock(id).isConsumable())
		{
			return false;
		}

		UnlockedItem item = getUnlock(p, id);

		if(item == null)
		{
			return false;
		}

		RestlessObject res = new RSpendUnlock().player(p.getUniqueId()).unlock(id.contains(":") ? id.split(":")[1] : id).complete(Shade.connect());
		if(res != null && res instanceof ROK)
		{
			Map<String, UnlockedItem> items = getAccount(p).getUnlocks();
			UnlockedItem t = items.get("shout");
			t.setAmount(t.getAmount() - 1);

			if(t.getAmount() > 0)
			{
				items.put(id.contains(":") ? id.split(":")[1] : id, t);
			}

			else
			{
				items.remove(id.contains(":") ? id.split(":")[1] : id);
			}

			getAccount(p).setUnlocks(items);
			return true;
		}

		return false;
	}

	/**
	 * Get the current holdings a player has of a current unlock. Note that if the
	 * unlock is a singleton, it will either return 1 or 0
	 *
	 * @param p
	 *            the player
	 * @param id
	 *            the unlock id
	 * @return returns the amount or 0 if they do not have the unlock
	 */
	public static int getUnlockAmount(Player p, String id)
	{
		return hasUnlock(p, id) ? getUnlock(p, id).getAmount() : 0;
	}

	/**
	 * Get the unlock item data associated with this player
	 *
	 * @param p
	 *            the player
	 * @param id
	 *            the unlock id
	 * @return the unlock or null if they do not have the unlock
	 */
	public static UnlockedItem getUnlock(Player p, String id)
	{
		return getAccount(p).getUnlock(id);
	}

	/**
	 * Check if the player has the given unlock
	 *
	 * @param p
	 *            the player
	 * @param id
	 *            the unlock id
	 * @return true if the player has 1 or more of the given unlock
	 */
	public static boolean hasUnlock(Player p, String id)
	{
		return getAccount(p).hasUnlock(id);
	}

	/**
	 * Get player statistics data
	 *
	 * @param p
	 *            the player
	 * @return the statistics wrapper
	 */
	public static Statistics getStatistics(Player p)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getStats(p);
	}

	/**
	 * Syncronize the player's statistics with the proxy (async forking task)
	 *
	 * @param p
	 *            the player
	 * @param stats
	 *            the stats
	 */
	public static void syncronizeStatistics(Player p, Callback<Statistics> stats)
	{
		J.a(() ->
		{
			((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).syncronizeStatistics(p);
			stats.run(getStatistics(p));
		});
	}

	/**
	 * Get statistics for any player. Will return empty statistics for a valid
	 * mojang player.
	 *
	 * @param p
	 *            the player's id
	 * @return the statistics wrapper
	 */
	public static Statistics getStatistics(UUID p)
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getUniqueId().equals(p))
			{
				return getAccount(i).getStatistics();
			}
		}

		return getAccount(p).getStatistics();
	}

	/**
	 * Get syncronized statistics, forcing a sync before returning statistics
	 *
	 * @param p
	 *            the player id
	 * @param s
	 *            the callback for the result
	 */
	public static void getStatisticsSynced(UUID p, Callback<Statistics> s)
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getUniqueId().equals(p))
			{
				syncronizeStatistics(i, (v) -> s.run(v));
				return;
			}
		}

		s.run(getAccount(p).getStatistics());
	}

	/**
	 * Get the shadow account for any player. If the player does not have an
	 * account, one will be created first then returned
	 *
	 * @param id
	 *            the player
	 * @return the account
	 */
	public static ShadowAccount getAccount(Player id)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(id);
	}

	/**
	 * Get the shadow account for any player. If the player does not have an
	 * account, one will be created first then returned
	 *
	 * @param id
	 *            the player's id
	 * @return the account
	 */
	public static ShadowAccount getAccount(UUID id)
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getUniqueId().equals(id))
			{
				return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(i);
			}
		}

		return ((RAccount) new RGetAccount().player(id).complete(connect())).shadowAccount();
	}

	/**
	 * Unlock an item for the given player
	 *
	 * @param sender
	 *            the sender who is unlocking the item
	 * @param playername
	 *            the player's name
	 * @param player
	 *            the player id
	 * @param item
	 *            the item to unlock
	 */
	public static void unlock(MortarSender sender, String playername, UUID player, UnlockedItem item)
	{
		new RUnlock().player(player).unlock(item).complete(Shade.connect(), (r) ->
		{
			if(r != null && r instanceof ROK)
			{
				for(Player i : P.onlinePlayers())
				{
					if(i.getUniqueId().equals(player))
					{
						((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(i).setUnlocks(((RAccount) new RGetAccount().player(i.getUniqueId()).complete(connect())).shadowAccount().getUnlocks());
						break;
					}
				}

				sender.sendMessage(playername + " was given " + (item.getAmount() > 1 ? (item.getAmount() + "x ") : "") + item.getId());
			}

			else if(r != null && r instanceof RError)
			{
				RError error = (RError) r;
				sender.sendMessage("Failed to give " + (item.getAmount() > 1 ? (item.getAmount() + "x ") : "") + item.getId() + " to " + playername + ". Reason: " + error.message());
			}

			else
			{
				sender.sendMessage("Failed to give " + (item.getAmount() > 1 ? (item.getAmount() + "x ") : "") + item.getId() + " to " + playername + ". Reason: NULL RESPONSE");
			}
		});
	}

	/**
	 * Get a list of connectable servers with meta
	 *
	 * @return the live list
	 */
	public static GList<ConnectableServer> getServers()
	{
		return servers.v();
	}

	/**
	 * Get a connectable server by id
	 *
	 * @param id
	 *            the id
	 * @return the server or null
	 */
	public static ConnectableServer getServer(String id)
	{
		return servers.get(id);
	}

	/**
	 * Update this server's reference of another server (probably dont use this)
	 *
	 * @param i
	 *            the server to update
	 */
	public static void updateServer(ConnectableServer i)
	{
		servers.put(i.getId(), i);
	}

	/**
	 * Get all known unlock types from the proxy
	 *
	 * @return the unlocks
	 */
	public static GList<ShadowUnlock> getUnlocks()
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getUnlocks();
	}

	/**
	 * Get a mapping of all unlocks from a player
	 *
	 * @param p
	 *            the player
	 * @return the unlocks
	 */
	public static Map<String, UnlockedItem> getUnlocks(Player p)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(p).getUnlocks();
	}

	/**
	 * Get the settings for the given player
	 *
	 * @param p
	 *            the player
	 * @return low level settings
	 */
	public static JSONObject getSettings(Player p)
	{
		return getAccount(p).getSettings();
	}

	/**
	 * Get the settings for the given player
	 *
	 * @param p
	 *            the player
	 * @return low level settings
	 */
	public static JSONObject getSettings(UUID p)
	{
		return getAccount(p).getSettings();
	}

	/**
	 * Set low level settings for the given player
	 *
	 * @param p
	 *            the player
	 * @param o
	 *            the settings
	 */
	public static void setSettings(UUID p, JSONObject o)
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getUniqueId().equals(p))
			{
				getAccount(i).setSettings(o);
				break;
			}
		}

		new RSetSettings().player(p).settings(o).completeBlind(connect());
	}

	/**
	 * Get the unlock mapping for a given player
	 *
	 * @param p
	 *            the player
	 * @return the unlocks
	 */
	public static Map<String, UnlockedItem> getUnlocks(UUID p)
	{
		for(Player i : P.onlinePlayers())
		{
			if(i.getUniqueId().equals(p))
			{
				return getUnlocks(i);
			}
		}

		return getAccount(p).getUnlocks();
	}

	/**
	 * Get all known ranks
	 *
	 * @return the ranks synced by the proxy
	 */
	public static GList<ShadowRank> getRanks()
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getRanks();
	}

	/**
	 * Get unlock information by id
	 *
	 * @param cid
	 *            the id
	 * @return the unlock data (table)
	 */
	public static ShadowUnlock getUnlock(String cid)
	{
		String category = cid.contains(":") ? cid.split("\\Q:\\E")[0] : "";
		String id = cid.contains(":") ? cid.split("\\Q:\\E")[1] : cid;

		for(ShadowUnlock i : getUnlocks())
		{
			if(cid.contains(":") && i.getId().equals(id) && i.getType().equals(category))
			{
				return i;
			}

			else if(!cid.contains(":") && i.getId().equals(id))
			{
				return i;
			}
		}

		return null;
	}

	/**
	 * Get the restless connection for sending messages to the proxy
	 *
	 * @return the restless (proxy) connection
	 */
	public static RestlessConnector connect()
	{
		return ShadeClient.instance.getConnector();
	}

	/**
	 * Change a player's chat color
	 *
	 * @param i
	 *            the color id (unlock)
	 * @param player
	 *            the player
	 */
	public static void changeChatColor(String i, Player player)
	{
		JSONObject o = getSettings(player).put("chat-color", i);
		setSettings(player.getUniqueId(), o);
		player.closeInventory();

		try
		{
			C c = C.valueOf(i);
			Styles.superBorder(player, i + "Chat Color Changed to " + F.capitalizeWords(i.toLowerCase().replaceAll("_", " ")), c, C.DARK_GRAY);
		}

		catch(Throwable e)
		{
			try
			{
				TextFilter f = TextFilter.valueOf(i);
				Styles.superBorderFiltered(player, i + "Chat Color Changed to " + F.capitalizeWords(i.toLowerCase().replaceAll("_", " ")), f);
			}

			catch(Throwable ee)
			{
				ee.printStackTrace();
			}
		}
	}

	/**
	 * Get a player's online UUID from mojang servers. These are cached, but be
	 * careful to not get rate limited
	 *
	 * @param name
	 *            the name
	 * @return the UUID if found
	 */
	public static UUID getUUID(String name)
	{
		return ((MojangProfileController) MortarAPIPlugin.p.getController(MojangProfileController.class)).getOnlineUUID(name);
	}

	/**
	 * Register a listener through mortar (for shade modules)
	 *
	 * @param l
	 *            the listener
	 */
	public static void listen(Listener l)
	{
		MortarAPIPlugin.p.registerListener(l);
	}

	/**
	 * Unregister a listener through mortar (for shade modules)
	 *
	 * @param l
	 *            the listener
	 */
	public static void unlisten(Listener l)
	{
		MortarAPIPlugin.p.unregisterListener(l);
	}

	/**
	 * Update the servers effects
	 *
	 * @param effects
	 *            the new effects
	 */
	public static void updateServerEffects(ServerEffects effects)
	{
		Shade.effects = effects;
	}
}
