package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.api.sql.UniversalParser;
import mortar.lang.json.JSONObject;
import mortar.logic.io.Hasher;

public class RSendMessage extends RestlessObject
{
	private String message;
	private String toServer;
	private String fromServer;

	@Override
	public RestlessObject handle()
	{
		if(ShadeServer.instance.getServers().containsKey(to()))
		{
			//@builder
			new RReceiveMessage()
			.from(from())
			.message(message())
			.completeBlind(ShadeServer.instance.getServers().get(to()).getConnector());
			//@done
		}

		return new ROK();
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

	public RSendMessage message(RestlessObject message)
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

	public String to()
	{
		return toServer;
	}

	public RSendMessage to(String toServer)
	{
		this.toServer = toServer;
		return this;
	}

	public String from()
	{
		return fromServer;
	}

	public RSendMessage from(String fromServer)
	{
		this.fromServer = fromServer;
		return this;
	}

}
