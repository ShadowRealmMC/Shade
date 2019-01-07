package io.shadowrealm.shade.permission;

import com.volmit.phantom.plugin.PhantomPermission;
import com.volmit.phantom.plugin.Scaffold.Permission;

public class PermissionMap extends PhantomPermission
{
	@Permission
	public PermissionMapCompile compile;

	@Permission
	public PermissionMapTest test;

	@Permission
	public PermissionMapExport export;

	@Override
	protected String getNode()
	{
		return "map";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to all of shade maps";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
