package io.shadowrealm.shade.common;

import mortar.compute.math.M;

public class VirtualServer
{
	private String id;
	private String name;
	private String route;
	private String status;
	private String tagline;
	private long since;
	private int count;
	private int port;
	private RestlessConnector connector;

	public VirtualServer(String id, String name, String route, int port)
	{
		status = "&eSyncronizing...";
		tagline = "";
		since = M.ms();
		count = 0;
		this.id = id;
		this.name = name;
		this.port = port;
		this.route = route;
	}

	public RestlessConnector connector()
	{
		if(connector == null)
		{
			System.out.println("Establishing New Connection " + getRoute() + ":" + getPort());
			connector = new RestlessConnector(getRoute(), getPort(), getId());
		}

		return connector;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getRoute()
	{
		return route == null ? "localhost" : route;
	}

	public int getPort()
	{
		return port;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getTagline()
	{
		return tagline;
	}

	public void setTagline(String tagline)
	{
		this.tagline = tagline;
	}

	public long getSince()
	{
		return since;
	}

	public void setSince(long since)
	{
		this.since = since;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public RestlessConnector getConnector()
	{
		return connector;
	}

	public void setConnector(RestlessConnector connector)
	{
		this.connector = connector;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setRoute(String route)
	{
		this.route = route;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
}
