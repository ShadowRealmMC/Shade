package io.shadowrealm.shade.common.messages;

import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;

public class RGiveSXP extends RestlessObject
{
	private UUID player;
	private long amount;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player());
		a.setShadowXP(a.getShadowXP() + amount());
		a.setShadowXPEarned(a.getShadowXPEarned() + amount());
		ShadeServer.instance.getSQL().setAccount(a);
		return new RSXPChanged().current(a.getShadowXP()).earned(a.getShadowXPEarned());
	}

	public long amount()
	{
		return amount;
	}

	public RGiveSXP amount(long amount)
	{
		this.amount = amount;
		return this;
	}

	public UUID player()
	{
		return player;
	}

	public RGiveSXP player(UUID player)
	{
		this.player = player;
		return this;
	}
}
