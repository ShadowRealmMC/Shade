package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.VirtualServer;
import io.shadowrealm.shade.server.ServerConfig;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.compute.math.M;

public class RShout extends RestlessObject
{
	public static long lastShout = M.ms();
	private String message;

	public RShout()
	{

	}

	@Override
	public RestlessObject handle()
	{
		if(M.ms() - lastShout > ServerConfig.SHOUTING_INTERVAL)
		{
			lastShout = M.ms();

			for(VirtualServer i : ShadeServer.instance.getServers().v())
			{
				new RShouting().message(message()).completeBlind(i.connector());
			}

			return new ROK();
		}

		else
		{
			return new RError();
		}
	}

	public String message()
	{
		return message;
	}

	public RShout message(String message)
	{
		this.message = message;
		return this;
	}
}
