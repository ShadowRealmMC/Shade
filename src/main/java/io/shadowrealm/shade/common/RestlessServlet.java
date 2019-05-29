package io.shadowrealm.shade.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.shadowrealm.shade.common.messages.RError;
import mortar.api.sql.UniversalParser;
import mortar.lang.json.JSONObject;

public class RestlessServlet extends ShadowServlet
{
	private static final long serialVersionUID = 1687139144990962048L;
	public static String who;

	public RestlessServlet()
	{
		super("wire");
	}

	@Override
	public void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if(ensureParameters(req, "j"))
		{
			JSONObject j = new JSONObject(RestlessConnector.decode(req.getParameter("j")));
			try
			{
				RestlessObject o = (RestlessObject) UniversalParser.fromJSON(j, Class.forName(j.getString("sr-type")));
				String w = j.getString("sr-who");

				if(CommonProperties.DEBUG_CONNECTION)
				{
					System.out.println("[INTERCOM]: " + w + " -> " + who + " (" + o.name() + ")");
				}

				try
				{
					RestlessObject r = o.handle();

					if(r == null)
					{
						if(CommonProperties.DEBUG_CONNECTION)
						{
							System.out.println("[INTERCOM]: " + who + " -> " + w + " (" + "null" + ")");
						}

						write(resp, "\n");
					}

					else
					{
						if(CommonProperties.DEBUG_CONNECTION)
						{
							System.out.println("[INTERCOM]: " + who + " -> " + w + " (" + r.name() + ")");
						}

						JSONObject x = UniversalParser.toJSON(r);
						x.put("sr-type", r.getClass().getCanonicalName());
						x.put("sr-who", who);
						write(resp, x.toString(0) + "\n");
					}
				}

				catch(Throwable e)
				{
					e.printStackTrace();

					try
					{
						if(CommonProperties.DEBUG_CONNECTION)
						{
							System.out.println("[INTERCOM]: " + who + " -> " + w + " (" + "RError" + ")");
						}

						JSONObject x = UniversalParser.toJSON(new RError("Exception thrown while handling response. " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")"));
						x.put("sr-type", RError.class.getCanonicalName());
						x.put("sr-who", who);
						write(resp, x.toString(0) + "\n");
					}

					catch(Throwable ex)
					{
						ex.printStackTrace();
					}
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();

				try
				{
					if(CommonProperties.DEBUG_CONNECTION)
					{
						System.out.println("[INTERCOM]: " + who + " -> " + "???" + " (" + "RError" + ")");
					}

					JSONObject x = UniversalParser.toJSON(new RError("Exception thrown while handling request. " + e.getMessage() + " (" + e.getClass().getSimpleName() + ")"));
					x.put("sr-type", RError.class.getCanonicalName());
					x.put("sr-who", who);
					write(resp, x.toString(0) + "\n");
				}

				catch(Throwable ex)
				{
					ex.printStackTrace();
				}
			}
		}

		else
		{
			try
			{
				if(CommonProperties.DEBUG_CONNECTION)
				{
					System.out.println("[INTERCOM]: " + who + " -> " + "???" + " (" + "RError" + ")");
				}

				JSONObject x = UniversalParser.toJSON(new RError("Missing parameter j"));
				x.put("sr-type", RError.class.getCanonicalName());
				x.put("sr-who", who);
				write(resp, x.toString(0) + "\n");
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

}
