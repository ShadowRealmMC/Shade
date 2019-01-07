package io.shadowrealm.shade.permission;

import com.volmit.phantom.plugin.PhantomPermission;

public class PermissionMapExport extends PhantomPermission
{
	@Override
	protected String getNode()
	{
		return "export";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to export and save shade maps";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
