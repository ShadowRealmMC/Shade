package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandBooster;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;

public class SMBoosters extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Permission
	public static PermissionShade perm;

	public SMBoosters()
	{
		super("Boosters");
	}

	@Override
	public void start()
	{
		registerCommand(new CommandBooster());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
