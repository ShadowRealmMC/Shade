package io.shadowrealm.shade.permission;

import com.volmit.phantom.api.command.PhantomPermission;

public class PermissionMapTest extends PhantomPermission
{
	@Override
	protected String getNode()
	{
		return "test";
	}

	@Override
	public String getDescription()
	{
		return "Gives permission to test all shade maps.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
