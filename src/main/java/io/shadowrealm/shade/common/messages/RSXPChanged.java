package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RSXPChanged extends RestlessObject
{
	private long current;
	private long earned;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public long earned()
	{
		return earned;
	}

	public RSXPChanged earned(long earned)
	{
		this.earned = earned;
		return this;
	}

	public long current()
	{
		return current;
	}

	public RSXPChanged current(long current)
	{
		this.current = current;
		return this;
	}
}
