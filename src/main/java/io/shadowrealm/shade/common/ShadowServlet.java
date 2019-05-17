package io.shadowrealm.shade.common;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mortar.logic.io.VIO;

public abstract class ShadowServlet extends HttpServlet implements ShadeWebHandler
{
	private static final long serialVersionUID = 229675254360342497L;
	private final String node;
	protected boolean compress = false;
	protected boolean minimize = false;

	public ShadowServlet(String node)
	{
		this.node = node;
	}

	protected boolean ensureParameters(HttpServletRequest r, String... pars)
	{
		for(String i : pars)
		{
			if(r.getParameter(i) == null)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		compress = req.getParameter("gzip") != null;
		minimize = req.getParameter("min") != null;
		on(req, resp);
	}

	protected void write(HttpServletResponse resp, InputStream in) throws IOException
	{
		VIO.fullTransfer(in, resp.getOutputStream(), 12192);
		in.close();
	}

	protected void write(HttpServletResponse resp, String string) throws IOException
	{
		resp.getWriter().println(string);
	}

	@Override
	public String getNode()
	{
		return node;
	}

	@Override
	public abstract void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}
