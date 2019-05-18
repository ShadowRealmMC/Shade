package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;

public class PermissionClearChatEveryone extends MortarPermission
{
	@Override
	protected String getNode()
	{
		return "clear-all";
	}

	@Override
	public String getDescription()
	{
		return "Grants the ability to clear EVERYONES CHAT.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
