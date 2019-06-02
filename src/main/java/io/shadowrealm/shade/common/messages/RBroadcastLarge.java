package io.shadowrealm.shade.common.messages;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.common.CommonProperties;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.RestlessSide;
import io.shadowrealm.shade.common.VirtualServer;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.api.world.P;
import mortar.util.text.C;

public class RBroadcastLarge extends RestlessObject
{
	private String message;
	private String colorBright;
	private String colorDark;

	public RBroadcastLarge()
	{

	}

	@Override
	public RestlessObject handle()
	{
		if(CommonProperties.SIDE.equals(RestlessSide.SERVER))
		{
			for(VirtualServer i : ShadeServer.instance.getServers().v())
			{
				complete(i.getConnector());
			}
		}

		else
		{
			for(Player i : P.onlinePlayers())
			{
				Styles.superBorder(i, message(), colorBright(), colorDark());
			}
		}

		return new ROK();
	}

	public C colorBright()
	{
		return C.valueOf(colorBright);
	}

	public RBroadcastLarge colorBright(C colorBright)
	{
		this.colorBright = colorBright.name();
		return this;
	}

	public C colorDark()
	{
		return C.valueOf(colorDark);
	}

	public RBroadcastLarge colorDark(C colorDark)
	{
		this.colorDark = colorDark.name();
		return this;
	}

	public String message()
	{
		return message;
	}

	public RBroadcastLarge message(String message)
	{
		this.message = message;
		return this;
	}
}
