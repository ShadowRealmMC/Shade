package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandUnlock;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;

public class SMUnlocks extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Permission
	public static PermissionShade perm;

	public SMUnlocks()
	{
		super("Unlocks");
	}

	@Override
	public void start()
	{
		l("Registering Shadow Unlock Commands");
		registerCommand(new CommandUnlock());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
