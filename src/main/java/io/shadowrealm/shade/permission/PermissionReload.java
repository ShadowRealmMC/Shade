package io.shadowrealm.shade.permission;

import com.volmit.phantom.api.command.PhantomPermission;

public class PermissionReload extends PhantomPermission
{
	@Override
	protected String getNode()
	{
		return "reload";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to reload shade.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
