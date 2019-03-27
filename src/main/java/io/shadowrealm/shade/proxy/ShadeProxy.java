package io.shadowrealm.shade.proxy;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.object.ShadowPlayerLevel;
import io.shadowrealm.shade.object.ShadowRealm;
import io.shadowrealm.shade.object.proxy.ProxyShadowPlayer;
import io.shadowrealm.shade.object.proxy.ProxyShadowRealm;
import mortar.api.config.Configurator;
import mortar.bukkit.plugin.JarScannerSpecial;
import mortar.lang.collection.GSet;
import mortar.logic.format.F;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class ShadeProxy extends Plugin implements Listener
{
	public static final String SERVLET_PACKAGE = "io.shadowrealm.shade.proxy.servlet";
	public static Connection sql;
	private Server server;
	private ShadowRealm realm;

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable()
	{
		server = new Server(PORT);
		ServletContextHandler api = new ServletContextHandler(server, "/");
		getLogger().info("Init Web Server");

		for(Class<?> f : getClassesInPackage(SERVLET_PACKAGE, ShadeProxy.class))
		{
			try
			{
				ShadowServlet sl = (ShadowServlet) f.getConstructor().newInstance();
				getLogger().info("Registering API Servlet @/" + sl.getNode());
				api.addServlet((Class<? extends Servlet>) f, "/" + sl.getNode());
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		getLogger().info("Processing Configuration");
		File f = new File(getDataFolder(), "config.json");
		Configurator.JSON.load(ProxyConfig.class, f);
		getLogger().info("Connecting to MySQL jdbc:mysql://" + ProxyConfig.dbAddress + "/" + ProxyConfig.db + "?username=" + ProxyConfig.dbUser + "&password=" + F.repeat("*", ProxyConfig.dbPass.length()));

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Properties p = new Properties();
			p.setProperty("user", ProxyConfig.dbUser);

			if(!ProxyConfig.dbPass.equals("."))
			{
				p.setProperty("password", ProxyConfig.dbPass);
			}

			sql = DriverManager.getConnection("jdbc:mysql://" + ProxyConfig.dbAddress + "/" + ProxyConfig.db, p);
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

		getLogger().info("Starting Webserver for Shade on *:" + PORT);

		new Thread(() ->
		{
			try
			{
				server.start();
				getLogger().info("Webserver for Shade online *:" + PORT);
				server.join();
				getLogger().info("Webserver for Shade Shut Down");
			}

			catch(Exception e)
			{
				e.printStackTrace();
			}
		}).start();

		getLogger().info("Starting Shadow Realm");
		realm = new ProxyShadowRealm();
	}

	public static File getJar(Class<?> src)
	{
		try
		{
			return new File(new File(src.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
		}

		catch(URISyntaxException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static GSet<Class<?>> getClassesInPackage(String p, Class<?> src)
	{
		JarScannerSpecial s = new JarScannerSpecial(getJar(src), p);

		try
		{
			s.scan();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		return s.getClasses();
	}

	@Override
	public void onDisable()
	{

	}

	// Connection handed over to server -> player
	// Basically its player login event
	@EventHandler
	public void on(ServerConnectedEvent e)
	{
		realm.verifyServer(e.getServer().getInfo().getName());
		ProxyShadowPlayer player = new ProxyShadowPlayer(e.getPlayer().getDisplayName(), e.getPlayer().getUniqueId());
		realm.login(player);
		player.assignServer(realm.getServer(e.getServer().getInfo().getName()));
	}

	public static void setLevel(ShadowPlayerLevel level)
	{
		// TODO do it
	}

	public static ShadowPlayerLevel getLevel(UUID id)
	{
		// TODO do it
		return null;
	}
}
