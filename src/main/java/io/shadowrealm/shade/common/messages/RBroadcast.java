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

public class RBroadcast extends RestlessObject
{
	private String message;
	private String colorBright;
	private String colorDark;
	private String type;

	public RBroadcast()
	{
		type = "";
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
				if(type.equals("toast"))
				{
					Styles.toastBroadcast(i, colorBright() + message());
				}

				else if(type.equals("chat"))
				{
					Styles.chatBroadcast(i, colorBright() + message());
				}

				else
				{
					Styles.superBorder(i, message(), colorBright(), colorDark());
				}
			}
		}

		return new ROK();
	}

	public C colorBright()
	{
		return C.valueOf(colorBright);
	}

	public RBroadcast colorBright(C colorBright)
	{
		this.colorBright = colorBright.name();
		return this;
	}

	public C colorDark()
	{
		return C.valueOf(colorDark);
	}

	public RBroadcast colorDark(C colorDark)
	{
		this.colorDark = colorDark.name();
		return this;
	}

	public String message()
	{
		return message;
	}

	public RBroadcast message(String message)
	{
		this.message = message;
		return this;
	}

	public String type()
	{
		return type;
	}

	public RBroadcast type(String type)
	{
		this.type = type;
		return this;
	}
}
