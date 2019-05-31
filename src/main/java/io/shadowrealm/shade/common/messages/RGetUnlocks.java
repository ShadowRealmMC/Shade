package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;

public class RGetUnlocks extends RestlessObject
{
	@Override
	public RestlessObject handle()
	{
		return new RUnlocks().unlocks(ShadeServer.instance.getSQL().getUnlocks());
	}
}
