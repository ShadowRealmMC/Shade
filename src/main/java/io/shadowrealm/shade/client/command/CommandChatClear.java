package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandChatClear extends MortarCommand
{
	public CommandChatClear()
	{
		super("clear");
		requiresPermission(ShadeClient.perm.chat.clear);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		Styles.clearSelf(sender.player());

		return true;
	}
}
