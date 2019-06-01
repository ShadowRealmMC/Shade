package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionStats extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "stats";
	}

	@Override
	public String getDescription()
	{
		return "Read statistics on anyone.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
