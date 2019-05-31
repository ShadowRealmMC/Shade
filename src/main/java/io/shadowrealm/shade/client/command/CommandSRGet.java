package io.shadowrealm.shade.client.command;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandSRGet extends MortarCommand
{
	public CommandSRGet()
	{
		super("get", "g", "?");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		// String name = args[0];
		sender.sendMessage("NYI");

		return true;
	}

}
