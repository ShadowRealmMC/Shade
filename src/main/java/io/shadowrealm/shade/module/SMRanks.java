package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandSR;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;

public class SMRanks extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Permission
	public static PermissionShade perm;

	public SMRanks()
	{
		super("Ranks");
	}

	@Override
	public void start()
	{
		l("Registering Shadow Rank Commands");
		registerCommand(new CommandSR());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
