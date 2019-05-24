package io.shadowrealm.shade.client;

import io.shadowrealm.shade.common.ConnectableServer;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;

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
}
