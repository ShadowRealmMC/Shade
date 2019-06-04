package io.shadowrealm.shade.common;

import java.util.ArrayList;
import java.util.List;

import mortar.api.sql.UniversalType;

public class ServerEffect
{
	@UniversalType(String.class)
	private List<String> contributors;
	private String id;
	private long endsAt;

	public ServerEffect()
	{
		contributors = new ArrayList<>();
	}

	public ServerEffect(String id, long endsAt)
	{
		this();
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

	public List<String> getContributors()
	{
		return contributors;
	}

	public void setContributors(List<String> contributors)
	{
		this.contributors = contributors;
	}
}
