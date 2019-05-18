package io.shadowrealm.shade.client.command;

import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandSRDamage extends MortarCommand
{
	public CommandSRDamage()
	{
		super("damage", "-", "remove", "take", "harm");
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
