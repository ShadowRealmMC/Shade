package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionChatShout extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "shout";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to shout if you have the unlock.";
	}

	@Override
	public boolean isDefault()
	{
		return true;
	}
}
