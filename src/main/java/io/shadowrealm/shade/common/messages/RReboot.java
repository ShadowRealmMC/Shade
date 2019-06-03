package io.shadowrealm.shade.common.messages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.shadowrealm.shade.common.RestlessObject;
import mortar.api.sched.J;
import mortar.api.world.P;
import mortar.util.text.C;

public class RReboot extends RestlessObject
{
	public RReboot()
	{

	}

	@Override
	public RestlessObject handle()
	{
		for(Player i : P.onlinePlayers())
		{
			J.s(() -> i.kickPlayer(C.LIGHT_PURPLE + "Restarting!\nWe'll be back in under a minute!"));
		}

		J.s(() -> Bukkit.shutdown(), 20);

		return new ROK();
	}
}
