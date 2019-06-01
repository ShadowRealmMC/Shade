package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;

public class PermissionShade extends MortarPermission
{
	@Permission
	public PermissionChat chat;

	@Permission
	public PermissionStats stats;

	@Permission
	public PermissionUnlocks unlocks;

	@Override
	protected String getNode()
	{
		return "shade";
	}

	@Override
	public String getDescription()
	{
		return "Base shade permission";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
