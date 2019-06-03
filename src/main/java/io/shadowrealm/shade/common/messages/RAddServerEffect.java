package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.ServerEffect;
import io.shadowrealm.shade.common.ServerEffects;
import io.shadowrealm.shade.server.ShadeServer;

public class RAddServerEffect extends RestlessObject
{
	private ServerEffects effects;
	private ServerEffect effect;
	private String player;

	@Override
	public RestlessObject handle()
	{
		ShadeServer.instance.updateServerEffects(effects(), effect(), player(), ShadeServer.instance.getSQL().getUnlock(effect.getId()).getFormattedName());
		return new ROK();
	}

	public ServerEffects effects()
	{
		return effects;
	}

	public RAddServerEffect effects(ServerEffects effects)
	{
		this.effects = effects;
		return this;
	}

	public ServerEffect effect()
	{
		return effect;
	}

	public RAddServerEffect effect(ServerEffect effect)
	{
		this.effect = effect;
		return this;
	}

	public String player()
	{
		return player;
	}

	public RAddServerEffect player(String player)
	{
		this.player = player;
		return this;
	}
}
