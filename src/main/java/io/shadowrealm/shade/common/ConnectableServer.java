package io.shadowrealm.shade.common;

public class ConnectableServer
{
	private String name;
	private String id;
	private String status;
	private String tagline;
	private long since;
	private int online;

	public ConnectableServer(String name, String id, String status, String tagline, long since, int online)
	{
		this.name = name;
		this.id = id;
		this.status = status;
		this.since = since;
		this.online = online;
		this.tagline = tagline;
	}

	public ConnectableServer()
	{
		name = "";
		id = "";
		status = "";
		tagline = "";
		since = -1;
		online = -1;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
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

	public int getOnline()
	{
		return online;
	}

	public void setOnline(int online)
	{
		this.online = online;
	}
}
