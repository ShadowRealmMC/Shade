package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionReboot extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "reboot";
	}

	@Override
	public String getDescription()
	{
		return "Base admin reboot permission";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
