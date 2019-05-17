package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;

public class RGetRanks extends RestlessObject
{
	@Override
	public RestlessObject handle()
	{
		return new RRanks().ranks(ShadeServer.instance.getSQL().getRanks());
	}
}
