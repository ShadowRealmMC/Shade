package io.shadowrealm.shade.client.command;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import mortar.api.world.P;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandChatClearAll extends MortarCommand
{
	public CommandChatClearAll()
	{
		super("clearall");
		requiresPermission(ShadeClient.perm.chat.clearEveryone);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		for(Player i : P.onlinePlayers())
		{
			Styles.cleared(i, sender.getName());
		}

		return true;
	}
}
