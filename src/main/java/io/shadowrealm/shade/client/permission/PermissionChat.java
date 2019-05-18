package io.shadowrealm.shade.client.permission;

import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;

public class PermissionChat extends MortarPermission
{
	@Permission
	public PermissionClearChat clear;

	@Permission
	public PermissionClearChatEveryone clearEveryone;

	@Permission
	public PermissionBroadcast broadcast;

	@Override
	protected String getNode()
	{
		return "chat";
	}

	@Override
	public String getDescription()
	{
		return "Base chat permission";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}