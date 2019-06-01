package io.shadowrealm.shade.common.messages;

import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;

public class RSetStatistics extends RestlessObject
{
	private UUID player;
	private String statistics;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player);
		a.setStatistics(statistics());
		ShadeServer.instance.getSQL().setAccount(a);
		return new ROK();
	}

	public UUID player()
	{
		return player;
	}

	public String statistics()
	{
		return statistics;
	}

	public RSetStatistics player(UUID player)
	{
		this.player = player;
		return this;
	}

	public RSetStatistics statistics(String statistics)
	{
		this.statistics = statistics;
		return this;
	}
}
