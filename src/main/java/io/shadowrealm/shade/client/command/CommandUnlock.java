package io.shadowrealm.shade.client.command;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandUnlock extends MortarCommand
{
	public CommandUnlock()
	{
		super("unlock");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		sender.sendMessage("NYI");

		return true;
	}

}
