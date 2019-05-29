package io.shadowrealm.shade.client;

import java.io.File;

import javax.servlet.Servlet;

import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.client.command.CommandChat;
import io.shadowrealm.shade.client.command.CommandSR;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.common.CommonProperties;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.RestlessServlet;
import io.shadowrealm.shade.common.RestlessSide;
import io.shadowrealm.shade.common.messages.RInit;
import io.shadowrealm.shade.common.messages.RInitialized;
import io.shadowrealm.shade.common.messages.RKeepAlive;
import io.shadowrealm.shade.common.messages.RKeptAlive;
import mortar.api.config.Configurator;
import mortar.api.sched.J;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.Permission;
import mortar.bukkit.plugin.Control;
import mortar.bukkit.plugin.Instance;
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

	@Instance
	public static ShadeClient instance;

	@Command("SR")
	public CommandSR sr;

	@Command("Chat")
	public CommandChat chat;

	@Permission
	public static PermissionShade perm;

	@Override
	public void start()
	{
		ready = false;
		//@builder
		l("Loading Configuration");
		Configurator.BUKKIT.load(ClientConfig.class, new File(getDataFolder(), "config.yml"));
		CommonProperties.DEBUG_CONNECTION = ClientConfig.WEBSERVER__CONNECTION_DEBUGGING;
		CommonProperties.SIDE = RestlessSide.CLIENT;
		CommonProperties.DOWNLOAD = ClientConfig.UPDATE;
		CommonProperties.DOWNLOAD_UPDATES = ClientConfig.DOWNLOAD_UPDATES;
		RestlessServlet.who = ClientConfig.SERVER__ID;
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
							J.s(() -> Bukkit.getPluginManager().disablePlugin(this));
						}
					});

				}

				catch(Exception e)
				{
					e.printStackTrace();
					f("ERROR! Couldnt bind port!!!");
					J.s(() -> Bukkit.getPluginManager().disablePlugin(this));
				}
			}

			else
			{
				f("ERROR! Proxy couldnt establish a port!!!");
				J.s(() -> Bukkit.getPluginManager().disablePlugin(this));
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
}
