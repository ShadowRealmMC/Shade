package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.ServerEffects;

public class RServerEffects extends RestlessObject
{
	private ServerEffects effects;

	@Override
	public RestlessObject handle()
	{
		Shade.updateServerEffects(effects());
		return new ROK();
	}

	public ServerEffects effects()
	{
		return effects;
	}

	public RServerEffects effects(ServerEffects effects)
	{
		this.effects = effects;
		return this;
	}
}
