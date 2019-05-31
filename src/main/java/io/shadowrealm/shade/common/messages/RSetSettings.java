package io.shadowrealm.shade.common.messages;

import java.io.IOException;
import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.lang.json.JSONException;
import mortar.lang.json.JSONObject;
import mortar.logic.io.Hasher;

public class RSetSettings extends RestlessObject
{
	private UUID player;
	private String settings;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player);
		a.setSettings(settings());
		ShadeServer.instance.getSQL().setAccount(a);
		return new ROK();
	}

	public UUID player()
	{
		return player;
	}

	public String settings()
	{
		return settings;
	}

	public RSetSettings player(UUID player)
	{
		this.player = player;
		return this;
	}

	public RSetSettings settings(String settings)
	{
		this.settings = settings;
		return this;
	}

	public RSetSettings settings(JSONObject settings)
	{
		try
		{
			return settings(Hasher.compress(settings.toString(0)));
		}

		catch(JSONException | IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
