package io.shadowrealm.shade.client;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.table.ShadowRank;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.util.text.C;

public class Shade
{
	private static final GMap<String, ConnectableServer> servers = new GMap<>();

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

	}
}
