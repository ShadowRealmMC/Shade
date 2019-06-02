package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionCommandBypass extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "bypass-command-limits";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to bypass command delays & cooldown limits.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
