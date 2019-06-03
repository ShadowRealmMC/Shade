package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.ShadeClient;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandBooster extends MortarCommand
{
	public CommandBooster()
	{
		super("boost", "booster", "boosters");
		requiresPermission(ShadeClient.perm.booster);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{


		return true;
	}
}
