package io.shadowrealm.shade.common;

public class ServerEffect
{
	private String id;
	private long endsAt;

	public ServerEffect()
	{

	}

	public ServerEffect(String id, long endsAt)
	{
		this.id = id;
		this.endsAt = endsAt;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public long getEndsAt()
	{
		return endsAt;
	}

	public void setEndsAt(long endsAt)
	{
		this.endsAt = endsAt;
	}
}
