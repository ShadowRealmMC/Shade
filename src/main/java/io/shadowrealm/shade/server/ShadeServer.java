package io.shadowrealm.shade.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import io.shadowrealm.shade.common.CommonProperties;
import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessServlet;
import io.shadowrealm.shade.common.RestlessSide;
import io.shadowrealm.shade.common.ServerEffect;
import io.shadowrealm.shade.common.ServerEffects;
import io.shadowrealm.shade.common.VirtualServer;
import io.shadowrealm.shade.common.messages.RBroadcast;
import io.shadowrealm.shade.common.messages.RKeepAlive;
import io.shadowrealm.shade.common.messages.RKeptAlive;
import io.shadowrealm.shade.common.messages.RReboot;
import io.shadowrealm.shade.common.messages.RServerEffects;
import io.shadowrealm.shade.common.messages.RServerStateChanged;
import io.shadowrealm.shade.common.table.ShadowIP;
import mortar.api.config.Configurator;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.util.text.C;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class ShadeServer extends Plugin implements Listener
{
	private long rebootsIn;
	private GMap<String, VirtualServer> serverPorts;
	private Server server;
	private ServerEffects effects;
	private Calendar scheduledTime;
	private Calendar currentTime;
	private ExecutorService svc;
	private OSQL osql;
	public static ShadeServer instance;

	@Override
	public void onEnable()
	{
		instance = this;
		rebootsIn = -1;
		serverPorts = new GMap<>();
		ServerConfig.PORT_OVERRIDES.copy();
		Configurator.DEFAULT.load(ServerConfig.class, new File(getDataFolder(), "config.json"));
		CommonProperties.DEBUG_CONNECTION = ServerConfig.WEBSERVER__CONNECTION_DEBUGGING;
		CommonProperties.SIDE = RestlessSide.SERVER;
		CommonProperties.DOWNLOAD = ServerConfig.UPDATE;
		CommonProperties.DOWNLOAD_UPDATES = ServerConfig.DOWNLOAD_UPDATES;
		server = new Server(ServerConfig.WEBSERVER__SERVER_PORT);
		ServletContextHandler api = new ServletContextHandler(server, "/");
		getLogger().info("Init Web Server");
		RestlessServlet.who = "proxy";
		RestlessServlet rl = new RestlessServlet();
		getLogger().info("Loading Effects");
		effects = ServerEffects.load();
		getLogger().info("Registering API Servlet @/" + rl.getNode());
		api.addServlet((Class<? extends Servlet>) rl.getClass(), "/" + rl.getNode());
		getLogger().info("Processing Configuration");
		getLogger().info("Connecting to MySQL jdbc:mysql://" + ServerConfig.DATABASE__ADDRESS + "/" + ServerConfig.DATABASE__NAME + "?username=" + ServerConfig.DATABASE__USER + "&password=" + F.repeat("*", ServerConfig.DATABASE__PASSWORD.length()));
		Connection sql = null;
		svc = Executors.newCachedThreadPool();
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

		if(ServerConfig.AUTO_RESTART__ENABLED)
		{
			scheduledTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
			int hourOfDayEST = Integer.valueOf(ServerConfig.AUTO_RESTART__TARGET_HOUR_EST.split(" ")[0].split(":")[0]);
			int minuteOfHourEST = Integer.valueOf(ServerConfig.AUTO_RESTART__TARGET_HOUR_EST.split(" ")[0].split(":")[1]);
			hourOfDayEST += ServerConfig.AUTO_RESTART__TARGET_HOUR_EST.split(" ")[1].toLowerCase().equals("pm") ? 12 : 0;
			System.out.println("Auto Restart scheduled at " + ServerConfig.AUTO_RESTART__TARGET_HOUR_EST.toUpperCase());
			scheduledTime.set(Calendar.HOUR_OF_DAY, hourOfDayEST);
			scheduledTime.set(Calendar.MINUTE, minuteOfHourEST);

			if(scheduledTime.getTimeInMillis() < currentTime.getTimeInMillis())
			{
				System.out.println("Scheduled time pushed back 1 calendar day, since the time for restart has already passed for today.");
				scheduledTime.roll(Calendar.DAY_OF_YEAR, 1);
			}

			runAutoRestartThread();
		}
	}

	private void runAutoRestartThread()
	{
		System.out.println("Restart Scheduled for " + (scheduledTime.get(Calendar.MONTH) + 1) + "/" + scheduledTime.get(Calendar.DAY_OF_MONTH) + " at " + ServerConfig.AUTO_RESTART__TARGET_HOUR_EST.toUpperCase());

		new Thread(() ->
		{
			GMap<Long, String> warnings = new GMap<>();
			warnings.put(5000L, "Restarting! Come back in a minute!");
			warnings.put(30000L, "Restart in 30 seconds!");
			warnings.put(60000L, "Restart in 1 minute!");
			warnings.put(5 * 60000L, "Restart in 5 minutes!");
			warnings.put(10 * 60000L, "Restart in 10 minutes!");
			warnings.put(20 * 60000L, "Restart in 20 minutes!");
			warnings.put(25 * 60000L, "Restart in 25 minutes!");
			warnings.put(30 * 60000L, "Restart in 30 minutes!");

			while(!Thread.interrupted())
			{
				try
				{
					currentTime.setTimeInMillis(M.ms());
					Thread.sleep(1000);
					long diff = Math.abs(scheduledTime.getTimeInMillis() - currentTime.getTimeInMillis());
					rebootsIn = M.ms() + diff;

					for(long i : warnings.k())
					{
						if(i > diff)
						{
							String warn = warnings.get(i);
							warnings.remove(i);

							if(diff < 10000)
							{
								for(VirtualServer j : serverPorts.v())
								{
									try
									{
										System.out.println("Stopping " + j.getName());
										new RReboot().complete(j.connector());
										j.connector().flushAndDie();
									}

									catch(Throwable e)
									{

									}
								}

								effects.addAll(TimeUnit.MINUTES.toMillis(5));
								System.out.println("All servers should be rebooting.");
								Thread.sleep(2000);
								System.out.println("I'm Out.");
								ProxyServer.getInstance().stop();
							}

							else
							{
								for(VirtualServer j : serverPorts.v())
								{
									new RBroadcast().colorBright(C.YELLOW).colorDark(C.GOLD).type(diff < 10000 ? "massive" : "toast").message(warn).completeBlind(j.connector());
								}
							}
						}
					}

					if(diff > TimeUnit.HOURS.toMillis(1))
					{
						Thread.sleep(60000);
					}
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	public GMap<String, VirtualServer> getServers()
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
				new RServerEffects().effects(effects).completeBlind(serverPorts.get(serverID).connector());
			}
		})
		, 250, TimeUnit.MILLISECONDS);
		//@done
	}

	public void broadcastServerEffects()
	{
		for(VirtualServer i : getServers().v())
		{
			new RServerEffects().effects(effects).completeBlind(i.connector());
		}
	}

	public GList<ConnectableServer> buildConnectableServers()
	{
		GList<ConnectableServer> s = new GList<>();

		for(VirtualServer i : getServers().v())
		{
			s.add(new ConnectableServer(i.getName(), i.getId(), i.getStatus(), i.getTagline(), i.getSince(), i.getCount()));
		}

		return s;
	}

	public boolean updateServer(ConnectableServer server)
	{
		if(server == null)
		{
			return false;
		}

		if(getServers().containsKey(server.getId()))
		{
			VirtualServer s = getServers().get(server.getId());
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

			return true;
		}

		return false;
	}

	public long getScheduledRebootTime()
	{
		return rebootsIn;
	}

	public void rescheduleReboot(long time)
	{
		if(time == 0)
		{
			for(VirtualServer j : serverPorts.v())
			{
				try
				{
					System.out.println("Stopping " + j.getName());
					new RReboot().complete(j.connector());
					j.connector().flushAndDie();
				}

				catch(Throwable e)
				{

				}
			}

			System.out.println("All servers should be rebooting.");
			try
			{
				Thread.sleep(2000);
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("I'm Out.");
			ProxyServer.getInstance().stop();
		}

		scheduledTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		scheduledTime.setTimeInMillis(currentTime.getTimeInMillis() + time);

		if(!ServerConfig.AUTO_RESTART__ENABLED)
		{
			ServerConfig.AUTO_RESTART__ENABLED = true;
			runAutoRestartThread();
		}
	}

	@EventHandler
	public void on(ServerConnectEvent e)
	{
		UUID id = e.getPlayer().getUniqueId();
		String ip = e.getPlayer().getAddress().getHostName();
		ShadowIP sip = new ShadowIP(id, ip);
		svc.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					getSQL().getKit().set(sip);
				}

				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void updateServerEffects(ServerEffects effects, ServerEffect added, String name, String bname)
	{
		this.effects = effects;
		effects.save();
		getProxy().getScheduler().schedule(this, () -> broadcastServerEffects(), 250, TimeUnit.MILLISECONDS);
		broadcastServerEffects();
		for(VirtualServer i : getServers().v())
		{
			new RBroadcast().colorBright(C.LIGHT_PURPLE).colorDark(C.DARK_PURPLE).message(name + " used \"" + bname + "\"").type("super").completeBlind(i.connector());
		}
	}
}
