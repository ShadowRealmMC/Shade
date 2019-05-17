package io.shadowrealm.shade.proxy.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.shadowrealm.shade.proxy.ShadowServlet;
import mortar.lang.json.JSONObject;

public class SyncSLT extends ShadowServlet
{
	private static final long serialVersionUID = 1L;

	public SyncSLT()
	{
		super("sync");
	}

	@Override
	public void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		JSONObject r = new JSONObject();

		if(ensureParameters(req, "token", "server"))
		{
			r.put("type", "ok");
		}

		else
		{
			r.put("type", "error");
			r.put("reason", "Missing token");
		}

		resp.getWriter().println(r.toString(0));
	}
}
