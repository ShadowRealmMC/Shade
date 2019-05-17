package io.shadowrealm.shade.common.messages;

import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;

public class RSpendAmethyst extends RestlessObject
{
	private UUID player;
	private long amount;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player());
		a.setAmethyst(a.getAmethyst() - amount());
		ShadeServer.instance.getSQL().setAccount(a);
		return new RAmethystChanged().current(a.getAmethyst());
	}

	public long amount()
	{
		return amount;
	}

	public RSpendAmethyst amount(long amount)
	{
		this.amount = amount;
		return this;
	}

	public UUID player()
	{
		return player;
	}

	public RSpendAmethyst player(UUID player)
	{
		this.player = player;
		return this;
	}
}
