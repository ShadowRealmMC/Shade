package io.shadowrealm.shade.common;

public class VirtualServer
{
	private String id;
	private String name;
	private String route;
	private int port;
	private RestlessConnector connector;

	public VirtualServer(String id, String name, String route, int port)
	{
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
}
