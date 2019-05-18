package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionBroadcast extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "broadcast";
	}

	@Override
	public String getDescription()
	{
		return "Broadcast a fancy bordered message";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
