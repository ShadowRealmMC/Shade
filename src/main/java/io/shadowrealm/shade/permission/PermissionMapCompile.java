package io.shadowrealm.shade.permission;

import com.volmit.phantom.plugin.PhantomPermission;

public class PermissionMapCompile extends PhantomPermission
{
	@Override
	protected String getNode()
	{
		return "compile";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to compile shade maps";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
