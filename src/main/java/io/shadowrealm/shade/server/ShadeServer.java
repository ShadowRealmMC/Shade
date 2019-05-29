package io.shadowrealm.shade.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.common.CommonProperties;
import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessServlet;
import io.shadowrealm.shade.common.RestlessSide;
import io.shadowrealm.shade.common.VirtualServer;
import io.shadowrealm.shade.common.messages.RKeepAlive;
import io.shadowrealm.shade.common.messages.RKeptAlive;
import io.shadowrealm.shade.common.messages.RServerStateChanged;
import mortar.api.config.Configurator;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class ShadeServer extends Plugin implements Listener
{
	private GMap<String, VirtualServer> serverPorts;
	private Server server;
	private OSQL osql;
	public static ShadeServer instance;

	@Override
	public void onEnable()
	{
		instance = this;
		serverPorts = new GMap<>();
		Configurator.DEFAULT.load(ServerConfig.class, new File(getDataFolder(), "config.yml"));
		CommonProperties.DEBUG_CONNECTION = ServerConfig.WEBSERVER__CONNECTION_DEBUGGING;
		CommonProperties.SIDE = RestlessSide.SERVER;
		CommonProperties.DOWNLOAD = ServerConfig.UPDATE;
		CommonProperties.DOWNLOAD_UPDATES = ServerConfig.DOWNLOAD_UPDATES;
		server = new Server(ServerConfig.WEBSERVER__SERVER_PORT);
		ServletContextHandler api = new ServletContextHandler(server, "/");
		getLogger().info("Init Web Server");
		RestlessServlet.who = "proxy";
		RestlessServlet rl = new RestlessServlet();
		getLogger().info("Registering API Servlet @/" + rl.getNode());
		api.addServlet((Class<? extends Servlet>) rl.getClass(), "/" + rl.getNode());
		getLogger().info("Processing Configuration");
		getLogger().info("Connecting to MySQL jdbc:mysql://" + ServerConfig.DATABASE__ADDRESS + "/" + ServerConfig.DATABASE__NAME + "?username=" + ServerConfig.DATABASE__USER + "&password=" + F.repeat("*", ServerConfig.DATABASE__PASSWORD.length()));
		Connection sql = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Properties p = new Properties();
			p.setProperty("user", ServerConfig.DATABASE__USER);

			if(!ServerConfig.DATABASE__PASSWORD.equals("."))
			{
				p.setProperty("password", ServerConfig.DATABASE__PASSWORD);
			}

			sql = DriverManager.getConnection("jdbc:mysql://" + ServerConfig.DATABASE__ADDRESS + "/" + ServerConfig.DATABASE__NAME, p);
		}

		catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
			getLogger().warning("Failed to instantiate com.mysql.jdbc.Driver");
		}

		catch(SQLException e)
		{
			e.printStackTrace();
			getLogger().warning("SQLException: " + e.getMessage());
			getLogger().warning("SQLState: " + e.getSQLState());
			getLogger().warning("VendorError: " + e.getErrorCode());
		}

		try
		{
			if(!sql.isValid(10000))
			{
				getLogger().warning("Failed to connect to MySQL! Waited for a response for 10 seconds!");
			}

			else
			{
				getLogger().info("MySQL Online!");
			}
		}

		catch(SQLException e)
		{
			getLogger().warning("Failed to test MySQL Connection");
			e.printStackTrace();
			getLogger().warning("SQLException: " + e.getMessage());
			getLogger().warning("SQLState: " + e.getSQLState());
			getLogger().warning("VendorError: " + e.getErrorCode());
		}

		osql = new OSQL(sql);
		getLogger().info("Starting Webserver for Shade on *:" + ServerConfig.WEBSERVER__SERVER_PORT);

		new Thread(() ->
		{
			try
			{
				server.start();
				getLogger().info("Webserver for Shade online *:" + ServerConfig.WEBSERVER__SERVER_PORT);
				server.join();
				getLogger().info("Webserver for Shade Shut Down");
			}

			catch(Exception e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public GMap<String, VirtualServer> getServerPorts()
	{
		return serverPorts;
	}

	public OSQL getSQL()
	{
		return osql;
	}

	@Override
	public void onDisable()
	{
		CommonProperties.downloadUpdates();
	}

	// Connection handed over to server -> player
	// Basically its player login event
	@EventHandler
	public void on(ServerConnectedEvent e)
	{

	}

	public void initialized(String serverID)
	{
		//@builder
		getProxy().getScheduler().schedule(this, () ->
		new RKeepAlive()
		.randomId()
		.complete(serverPorts.get(serverID).connector(), (k) -> {
			if(k != null && k instanceof RKeptAlive)
			{
				System.out.println("Established Connection with " + serverID);
			}
		})
		, 250, TimeUnit.MILLISECONDS);
		//@done
	}

	public GList<ConnectableServer> buildConnectableServers()
	{
		GList<ConnectableServer> s = new GList<>();

		for(VirtualServer i : getServerPorts().v())
		{
			s.add(new ConnectableServer(i.getName(), i.getId(), i.getStatus(), i.getTagline(), i.getSince(), i.getCount()));
		}

		return s;
	}

	public void updateServer(ConnectableServer server)
	{
		if(server == null)
		{
			return;
		}

		if(getServerPorts().containsKey(server.getId()))
		{
			VirtualServer s = getServerPorts().get(server.getId());
			s.setCount(server.getOnline());
			s.setSince(server.getSince());
			s.setStatus(server.getStatus());
			s.setTagline(server.getTagline());

			for(String i : serverPorts.k())
			{
				if(i.equals(server.getId()))
				{
					continue;
				}

				new RServerStateChanged().server(server).completeBlind(serverPorts.get(i).getConnector());
			}
		}
	}
}
