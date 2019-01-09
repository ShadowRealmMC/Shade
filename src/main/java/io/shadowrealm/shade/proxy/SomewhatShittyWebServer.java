package io.shadowrealm.shade.proxy;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.proxy.servlet.GetAccountSLT;
import io.shadowrealm.shade.proxy.servlet.PingSLT;
import io.shadowrealm.shade.proxy.servlet.SetAccountSLT;

public class SomewhatShittyWebServer extends Thread
{
	private Server server;
	private int port;

	public SomewhatShittyWebServer(int port)
	{
		this.port = port;
		server = new Server(port);
		ServletContextHandler api = new ServletContextHandler(server, "/");
		registerServlet(api, GetAccountSLT.class, "account");
		registerServlet(api, SetAccountSLT.class, "setaccount");
		registerServlet(api, PingSLT.class, "ping");
	}

	public void stopServer()
	{
		try
		{
			server.stop();
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void registerServlet(ServletContextHandler api, Class<? extends HttpServlet> serv, String path)
	{
		api.addServlet(serv, "/" + path);
	}

	@Override
	public void run()
	{
		try
		{
			System.out.println("[SHADE]: Webserver bound to *:" + port);
			server.start();
			server.join();
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
