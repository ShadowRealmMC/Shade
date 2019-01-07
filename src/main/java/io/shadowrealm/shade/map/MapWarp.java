package io.shadowrealm.shade.map;

import java.io.Serializable;

public class MapWarp extends MapWorldObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
