package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RCycleData extends RestlessObject
{
	private long cycle;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public long cycle()
	{
		return cycle;
	}

	public RCycleData cycle(long cycle)
	{
		this.cycle = cycle;
		return this;
	}
}
