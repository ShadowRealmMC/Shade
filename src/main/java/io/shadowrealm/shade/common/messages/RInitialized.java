package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RInitialized extends RestlessObject
{
	private long rebootingAt;
	private int port;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public int port()
	{
		return port;
	}

	public RInitialized port(int port)
	{
		this.port = port;
		return this;
	}

	public long rebootingAt()
	{
		return rebootingAt;
	}

	public RInitialized rebootingAt(long rebootingAt)
	{
		this.rebootingAt = rebootingAt;
		return this;
	}
}
