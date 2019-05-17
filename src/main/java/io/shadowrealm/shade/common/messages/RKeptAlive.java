package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RKeptAlive extends RestlessObject
{
	private int id;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public int id()
	{
		return id;
	}

	public RKeptAlive id(int id)
	{
		this.id = id;
		return this;
	}
}
