package io.shadowrealm.shade.common.messages;

import java.util.ArrayList;
import java.util.List;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessObject;
import mortar.api.sql.UniversalType;
import mortar.lang.collection.GList;

public class RSendServers extends RestlessObject
{
	@UniversalType(ConnectableServer.class)
	private List<ConnectableServer> servers;

	public RSendServers()
	{
		servers = new ArrayList<>();
	}

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

	public List<ConnectableServer> servers()
	{
		return servers;
	}

	public RSendServers servers(GList<ConnectableServer> servers)
	{
		this.servers = servers;
		return this;
	}
}
