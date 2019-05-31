package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionChangeColor extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "changecolor";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to change your chat color.";
	}

	@Override
	public boolean isDefault()
	{
		return true;
	}
}
