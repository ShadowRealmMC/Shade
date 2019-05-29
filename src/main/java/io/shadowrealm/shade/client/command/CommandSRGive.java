package io.shadowrealm.shade.client.command;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandSRGive extends MortarCommand
{
	public CommandSRGive()
	{
		super("give", "+", "add", "earn");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		String name = args[0];
		long amount = Long.valueOf(args[1]);
		sender.sendMessage("NYI");

		return true;
	}
}
