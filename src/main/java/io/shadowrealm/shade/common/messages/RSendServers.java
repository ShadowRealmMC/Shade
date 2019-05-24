package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessObject;
import mortar.api.sql.UniversalType;
import mortar.lang.collection.GList;

public class RSendServers extends RestlessObject
{
	@UniversalType(ConnectableServer.class)
	private GList<ConnectableServer> servers = new GList<>();

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public RSendServers add(ConnectableServer s)
	{
		servers().add(s);
		return this;
	}

	public GList<ConnectableServer> servers()
	{
		return servers;
	}

	public RSendServers servers(GList<ConnectableServer> servers)
	{
		this.servers = servers;
		return this;
	}
}
