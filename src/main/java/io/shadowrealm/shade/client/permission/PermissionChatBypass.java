package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionChatBypass extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "bypass-limits";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to bypass chat delays & Move-first limits.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
