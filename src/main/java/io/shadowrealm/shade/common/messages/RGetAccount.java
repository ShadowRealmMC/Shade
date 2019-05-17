package io.shadowrealm.shade.common.messages;

import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;

public class RGetAccount extends RestlessObject
{
	private UUID player;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player());
		ShadeServer.instance.getSQL().cycle(a);

		if(a != null)
		{
			return new RAccount().shadowAccount(a);
		}

		return new RError().message("Could not find or create account. Check proxy console.");
	}

	public UUID player()
	{
		return player;
	}

	public RGetAccount player(UUID player)
	{
		this.player = player;
		return this;
	}
}
