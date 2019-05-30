package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;

public class RStateChanged extends RestlessObject
{
	private ConnectableServer server;

	@Override
	public RestlessObject handle()
	{
		if(ShadeServer.instance.updateServer(server))
		{
			return new ROK();
		}

		return new RError("AUTH");
	}

	public ConnectableServer server()
	{
		return server;
	}

	public RStateChanged server(ConnectableServer server)
	{
		this.server = server;
		return this;
	}

}
