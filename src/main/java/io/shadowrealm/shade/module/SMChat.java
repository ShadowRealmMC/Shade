package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandChat;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;

public class SMChat extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Permission
	public static PermissionShade perm;

	public SMChat()
	{
		super("Chat");
	}

	@Override
	public void start()
	{
		l("Registering Chat Commands");
		registerCommand(new CommandChat());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
