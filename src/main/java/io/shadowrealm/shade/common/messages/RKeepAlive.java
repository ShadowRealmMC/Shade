package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RKeepAlive extends RestlessObject
{
	private int id;

	@Override
	public RestlessObject handle()
	{
		return new RKeptAlive().id(id());
	}

	public int id()
	{
		return id;
	}

	public RKeepAlive id(int id)
	{
		this.id = id;
		return this;
	}

	public RKeepAlive randomId()
	{
		id((int) (Math.random() * 9000000));
		return this;
	}
}
