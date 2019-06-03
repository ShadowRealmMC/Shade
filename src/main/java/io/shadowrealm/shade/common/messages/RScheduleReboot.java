package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;

public class RScheduleReboot extends RestlessObject
{
	private long in;

	@Override
	public RestlessObject handle()
	{
		ShadeServer.instance.rescheduleReboot(in());
		return new ROK();
	}

	public long in()
	{
		return in;
	}

	public RScheduleReboot in(long in)
	{
		this.in = in;
		return this;
	}
}
