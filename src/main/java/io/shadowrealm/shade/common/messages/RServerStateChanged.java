package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessObject;

public class RServerStateChanged extends RestlessObject
{
	private ConnectableServer server;

	@Override
	public RestlessObject handle()
	{
		Shade.updateServer(server());
		return new ROK();
	}

	public ConnectableServer server()
	{
		return server;
	}

	public RServerStateChanged server(ConnectableServer server)
	{
		this.server = server;
		return this;
	}

}
