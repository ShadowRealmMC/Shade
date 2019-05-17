package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RAmethystChanged extends RestlessObject
{
	private long current;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public long current()
	{
		return current;
	}

	public RAmethystChanged current(long current)
	{
		this.current = current;
		return this;
	}
}
