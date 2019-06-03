package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionBooster extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "boost";
	}

	@Override
	public String getDescription()
	{
		return "Base booster permission";
	}

	@Override
	public boolean isDefault()
	{
		return true;
	}
}
