package io.shadowrealm.shade.module;

import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;
import mortar.lang.collection.GMap;

public class SMBoosters extends ShadeModule
{
	private GMap<Player, Long> lastDurabilityWarning;

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

	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
