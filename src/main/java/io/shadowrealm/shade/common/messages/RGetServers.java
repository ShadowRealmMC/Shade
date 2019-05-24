package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;

public class RGetServers extends RestlessObject
{
	@Override
	public RestlessObject handle()
	{
		return new RSendServers().servers(ShadeServer.instance.buildConnectableServers());
	}
}
