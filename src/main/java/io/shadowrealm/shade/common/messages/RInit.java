package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.VirtualServer;
import io.shadowrealm.shade.server.ServerConfig;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.lang.collection.GMap;

public class RInit extends RestlessObject
{
	private String serverID;
	private String serverName;
	private String route;

	@Override
	public RestlessObject handle()
	{
		int min = ServerConfig.WEBSERVER__CLIENT_PORT_POOL__MIN;
		int max = ServerConfig.WEBSERVER__CLIENT_PORT_POOL__MAX;
		GMap<String, VirtualServer> ports = ShadeServer.instance.getServers();

		for(String i : ServerConfig.PORT_OVERRIDES)
		{
			String s = i.split("\\Q=\\E")[0];
			int p = Integer.valueOf(i.split("\\Q=\\E")[1]);

			if(serverID().equals(s))
			{
				ports.put(serverID, new VirtualServer(serverID(), serverName(), route(), p));
				ShadeServer.instance.initialized(serverID);

				if(!ports.containsKey(serverID))
				{
					return new RError().message("Could not find an open port. Surry, but Furk U");
				}

				return new RInitialized().rebootingAt(ShadeServer.instance.getScheduledRebootTime()).port(ports.get(serverID).getPort());
			}
		}

		searching: for(int i = min; i < max; i++)
		{
			for(VirtualServer j : ports.v())
			{
				if(j.getPort() == i)
				{
					continue searching;
				}
			}
			ports.put(serverID, new VirtualServer(serverID(), serverName(), route(), i));
			ShadeServer.instance.initialized(serverID);
			break;
		}

		if(!ports.containsKey(serverID))
		{
			return new RError().message("Could not find an open port. Surry, but Furk U");
		}

		return new RInitialized().port(ports.get(serverID).getPort());
	}

	public String serverID()
	{
		return serverID;
	}

	public RInit serverID(String serverID)
	{
		this.serverID = serverID;
		return this;
	}

	public String serverName()
	{
		return serverName;
	}

	public RInit serverName(String serverName)
	{
		this.serverName = serverName;
		return this;
	}

	public String route()
	{
		return route;
	}

	public RInit route(String route)
	{
		this.route = route;
		return this;
	}
}
