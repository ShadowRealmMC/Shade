package io.shadowrealm.shade.permission;

import com.volmit.phantom.api.command.PhantomPermission;
import com.volmit.phantom.api.module.Permission;

public class PermissionShade extends PhantomPermission
{
	@Permission
	public PermissionMap map;

	@Permission
	public PermissionReload reload;

	@Override
	protected String getNode()
	{
		return "shade";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to all of shade";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
