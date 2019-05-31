package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionUnlocks extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "unlocks";
	}

	@Override
	public String getDescription()
	{
		return "Unlock command (admin)";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
