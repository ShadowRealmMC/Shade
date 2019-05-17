package io.shadowrealm.shade.common.messages;

import java.util.concurrent.TimeUnit;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ServerConfig;

public class RGetCycleData extends RestlessObject
{
	@Override
	public RestlessObject handle()
	{
		return new RCycleData().cycle(TimeUnit.valueOf(ServerConfig.RANKING__CYCLE_TIME__UNIT).toMillis(ServerConfig.RANKING__CYCLE_TIME__VALUE));
	}
}
