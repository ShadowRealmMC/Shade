package io.shadowrealm.shade.common.messages;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.common.RestlessObject;
import mortar.api.sched.J;
import mortar.api.world.P;

public class RShouting extends RestlessObject
{
	private String message;

	public RShouting()
	{

	}

	@Override
	public RestlessObject handle()
	{
		J.a(() ->
		{
			for(Player i : P.onlinePlayers())
			{
				Styles.superBorderShout(i, message());
			}
		}, 60);

		return new ROK();
	}

	public String message()
	{
		return message;
	}

	public RShouting message(String message)
	{
		this.message = message;
		return this;
	}
}
