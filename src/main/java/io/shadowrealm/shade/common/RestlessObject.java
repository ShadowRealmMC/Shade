package io.shadowrealm.shade.common;

import mortar.api.sql.UniversalParser;
import mortar.lang.collection.Callback;
import mortar.lang.json.JSONObject;

public abstract class RestlessObject implements RestlessCompletable
{
	@Override
	public abstract RestlessObject handle();

	public RestlessObject()
	{

	}

	public String name()
	{
		return getClass().getSimpleName();
	}

	@Override
	public RestlessObject complete(RestlessConnector c)
	{
		RestlessObject rx = null;
		String whov = c.who();

		try
		{
			JSONObject compiled = UniversalParser.toJSON(this);
			compiled.put("sr-type", getClass().getCanonicalName());
			compiled.put("sr-who", RestlessServlet.who);

			if(CommonProperties.DEBUG_CONNECTION)
			{
				System.out.println("[INTERCOM]: " + RestlessServlet.who + " -> " + c.who() + " (" + name() + ")");
			}

			JSONObject r = c.request(compiled);

			if(r != null)
			{
				whov = r.getString("sr-who");
				rx = (RestlessObject) UniversalParser.fromJSON(r, Class.forName(r.getString("sr-type")));
			}
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		if(CommonProperties.DEBUG_CONNECTION)
		{
			System.out.println("[INTERCOM]: " + whov + " -> " + RestlessServlet.who + " (" + (rx != null ? rx.name() : "null") + ")");
		}

		return rx;
	}

	@Override
	public void complete(RestlessConnector co, Callback<RestlessObject> c)
	{
		co.queue(new Runnable()
		{
			@Override
			public void run()
			{
				c.run(complete(co));
			}
		});
	}
	
	@Override
	public void completeBlind(RestlessConnector co)
	{
		complete(co, (r) -> {});
	}
}
