package io.shadowrealm.shade.client;

import java.io.File;
import java.io.IOException;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.common.CommonProperties;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.RestlessServlet;
import io.shadowrealm.shade.common.RestlessSide;
import io.shadowrealm.shade.common.messages.RInit;
import io.shadowrealm.shade.common.messages.RInitialized;
import io.shadowrealm.shade.common.messages.RKeepAlive;
import io.shadowrealm.shade.common.messages.RKeptAlive;
import io.shadowrealm.shade.common.messages.RReceiveMessage;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Configurator;
import mortar.bukkit.command.Permission;
import mortar.bukkit.plugin.Control;
import mortar.bukkit.plugin.Instance;
import mortar.bukkit.plugin.JarScannerSpecial;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.util.text.C;
import mortar.util.text.TXT;

public class ShadeClient extends MortarPlugin
{
	public static boolean ready;
	private Server server;
	private RestlessConnector c;

	@Control
	public ShadowPlayerController shadowPlayerController;

	@Control
	public ShadowServerController shadowServerController;

	@Instance
	public static ShadeClient instance;

	@Permission
	public static PermissionShade perm;

	@Override
	public void start()
	{
		ready = false;
		l("Loading Configuration");
		Configurator.BUKKIT.load(ClientConfig.class, new File(getDataFolder(), "config.yml"));
		CommonProperties.DEBUG_CONNECTION = ClientConfig.WEBSERVER__CONNECTION_DEBUGGING;
		CommonProperties.SIDE = RestlessSide.CLIENT;
		CommonProperties.DOWNLOAD = ClientConfig.UPDATE;
		CommonProperties.DOWNLOAD_UPDATES = ClientConfig.DOWNLOAD_UPDATES;
		RestlessServlet.who = ClientConfig.SERVER__ID;
		establishConnection();
		startMicroModules();
	}

	private void startMicroModules()
	{
		JarScannerSpecial s = new JarScannerSpecial(getFile(), "io.shadowrealm.shade.module");
		try
		{
			s.scan();
		}

		catch(IOException e1)
		{
			e1.printStackTrace();
		}

		for(Class<?> i : s.getClasses())
		{
			try
			{
				ShadeModule mod = (ShadeModule) i.getConstructor().newInstance();
				mod.v("Loading SubModule: " + mod.getName());
				mod.loadConfiguration();

				if(mod.isEnabled())
				{
					mod.v("Enabling SubModule: " + mod.getName());
					registerListener(mod);
					mod.start();
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	public void establishConnection()
	{
		//@builder
		l("Establishing connection with proxy " + ClientConfig.WEBSERVER__SERVER_ADDRESS + ":" + ClientConfig.WEBSERVER__SERVER_PORT);
		c = new RestlessConnector(ClientConfig.WEBSERVER__SERVER_ADDRESS, ClientConfig.WEBSERVER__SERVER_PORT, "proxy");
		l("Initializing with Proxy as " + ClientConfig.SERVER__ID + " (" + ClientConfig.SERVER__NAME + ")");
		new RInit()
		.serverID(ClientConfig.SERVER__ID)
		.serverName(ClientConfig.SERVER__NAME)
		.route(ClientConfig.WEBSERVER__CLIENT_ROUTE)
		.complete(c, (r) -> {
			if(r != null && r instanceof RInitialized)
			{
				RInitialized init = (RInitialized) r;
				server = new Server(init.port());
				ServletContextHandler api = new ServletContextHandler(server, "/");
				RestlessServlet rl = new RestlessServlet();
				api.addServlet((Class<? extends Servlet>) rl.getClass(), "/" + rl.getNode());

				try
				{
					server.start();
					l("Started Webserver at " + ClientConfig.WEBSERVER__SERVER_ADDRESS + ":" + init.port());
					l("Testing connection proxy -> " + ClientConfig.SERVER__ID);
					new RKeepAlive()
					.randomId()
					.complete(c, (rx) -> {
						if(rx instanceof RKeptAlive)
						{
							ready = true;
							l("Established a connection with proxy at " + ClientConfig.WEBSERVER__SERVER_ADDRESS + ":" + init.port());
						}

						else
						{
							f("Proxy cant seem to communicate with us!");
						}
					});

				}

				catch(Exception e)
				{
					e.printStackTrace();
					f("ERROR! Couldnt bind port!!!");
				}
			}

			else
			{
				f("ERROR! Proxy couldnt establish a port!!!");
			}
		});
		//@done
	}

	@Override
	public void stop()
	{

	}

	@Override
	public void onDisable()
	{
		CommonProperties.downloadUpdates();
		super.onDisable();
	}

	public RestlessConnector getConnector()
	{
		return c;
	}

	@Override
	public String getTag(String subTag)
	{
		return TXT.makeTag(C.DARK_GRAY, C.LIGHT_PURPLE, C.GRAY, "Shadow");
	}

	public RestlessObject receive(RReceiveMessage rReceiveMessage)
	{
		return null;
	}
}
