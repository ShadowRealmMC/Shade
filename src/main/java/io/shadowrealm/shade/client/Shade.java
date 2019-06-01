package io.shadowrealm.shade.client;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.Statistics;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.messages.RAccount;
import io.shadowrealm.shade.common.messages.RError;
import io.shadowrealm.shade.common.messages.RGetAccount;
import io.shadowrealm.shade.common.messages.ROK;
import io.shadowrealm.shade.common.messages.RSetSettings;
import io.shadowrealm.shade.common.messages.RUnlock;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowRank;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.world.P;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONObject;
import mortar.logic.format.F;
import mortar.util.text.C;

public class Shade
{
	private static final GMap<String, ConnectableServer> servers = new GMap<>();

	public static Statistics getStatistics(Player p)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getStats(p);
	}

	public static ShadowAccount getAccount(Player id)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(id);
	}

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

	public static GList<ConnectableServer> getServers()
	{
		return servers.v();
	}

	public static ConnectableServer getServer(String id)
	{
		return servers.get(id);
	}

	public static void updateServer(ConnectableServer i)
	{
		servers.put(i.getId(), i);
	}

	public static GList<ShadowUnlock> getUnlocks()
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getUnlocks();
	}

	public static Map<String, UnlockedItem> getUnlocks(Player p)
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getAccount(p).getUnlocks();
	}

	public static JSONObject getSettings(Player p)
	{
		return getAccount(p).getSettings();
	}

	public static JSONObject getSettings(UUID p)
	{
		return getAccount(p).getSettings();
	}

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

	public static GList<ShadowRank> getRanks()
	{
		return ((ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class)).getRanks();
	}

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

	public static RestlessConnector connect()
	{
		return ShadeClient.instance.getConnector();
	}

	public static void changeChatColor(C i, Player player)
	{
		JSONObject o = getSettings(player).put("chat-color", i.name());
		setSettings(player.getUniqueId(), o);
		player.closeInventory();

		if(i.equals(C.MAGIC))
		{
			Styles.superBorderRGB(player, "Chat Color Changed to RGB");
		}

		else
		{
			Styles.superBorder(player, i + "Chat Color Changed to " + F.capitalizeWords(i.name().toLowerCase().replaceAll("_", " ")), i, C.DARK_GRAY);
		}
	}
}
