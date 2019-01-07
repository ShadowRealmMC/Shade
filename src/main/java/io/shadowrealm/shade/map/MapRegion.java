package io.shadowrealm.shade.map;

import java.io.Serializable;

public class MapRegion extends MapWorldObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private MapMood mood;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public MapMood getMood()
	{
		return mood;
	}

	public void setMood(MapMood mood)
	{
		this.mood = mood;
	}
}
