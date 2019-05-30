package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.RestlessObject;
import mortar.api.sql.UniversalParser;
import mortar.lang.json.JSONObject;
import mortar.logic.io.Hasher;

public class RReceiveMessage extends RestlessObject
{
	private String message;
	private String fromServer;

	@Override
	public RestlessObject handle()
	{
		return ShadeClient.instance.receive(this);
	}

	@SuppressWarnings("unchecked")
	public RestlessObject message()
	{
		try
		{
			JSONObject object = new JSONObject(Hasher.decompress(message));
			Class<? extends RestlessObject> clazz = (Class<? extends RestlessObject>) Class.forName(object.getString("class"));
			JSONObject body = object.getJSONObject("body");
			return UniversalParser.fromJSON(body, clazz);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public RReceiveMessage message(RestlessObject message)
	{
		try
		{
			JSONObject object = new JSONObject();
			object.put("class", message.getClass().getCanonicalName());
			object.put("body", UniversalParser.toJSON(message));
			this.message = Hasher.compress(object.toString(0));
		}

		catch(Throwable e)
		{
			System.out.println("Cannot write message " + message.getClass());
			e.printStackTrace();
		}

		return this;
	}

	public String from()
	{
		return fromServer;
	}

	public RReceiveMessage from(String fromServer)
	{
		this.fromServer = fromServer;
		return this;
	}

}
