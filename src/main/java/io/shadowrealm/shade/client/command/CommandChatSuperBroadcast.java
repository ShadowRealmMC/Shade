package io.shadowrealm.shade.client.command;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import mortar.api.world.P;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandChatSuperBroadcast extends MortarCommand
{
	public CommandChatSuperBroadcast()
	{
		super("broadcast");
		requiresPermission(ShadeClient.perm.chat.broadcast);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		String m = "";

		for(String i : args)
		{
			m += i + " ";
		}

		for(Player i : P.onlinePlayers())
		{
			Styles.superBorder(i, F.color(m), C.YELLOW, C.GOLD);
		}

		return true;
	}
}
