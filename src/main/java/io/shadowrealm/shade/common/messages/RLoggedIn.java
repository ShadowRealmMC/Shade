package io.shadowrealm.shade.common.messages;

import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;

public class RLoggedIn extends RestlessObject
{
	private UUID player;
	private String name;
	private String server;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player);

		if(a.getCachedName().equals(name()) && a.getCachedServer().equals(server()))
		{
			return null;
		}

		a.setCachedName(name());
		a.setCachedServer(server());
		ShadeServer.instance.getSQL().setAccount(a);

		return null;
	}

	public String server()
	{
		return server;
	}

	public RLoggedIn server(String server)
	{
		this.server = server;
		return this;
	}

	@Override
	public String name()
	{
		return name;
	}

	public RLoggedIn name(String name)
	{
		this.name = name;
		return this;
	}

	public UUID player()
	{
		return player;
	}

	public RLoggedIn player(UUID player)
	{
		this.player = player;
		return this;
	}
}
