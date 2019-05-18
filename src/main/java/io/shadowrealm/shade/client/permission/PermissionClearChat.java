package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionClearChat extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "clear";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to clear YOUR OWN chat ONLY.";
	}

	@Override
	public boolean isDefault()
	{
		return true;
	}
}
