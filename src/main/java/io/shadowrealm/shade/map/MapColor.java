package io.shadowrealm.shade.map;

import java.io.Serializable;

public class MapColor extends MapWorldObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String color;

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}
}
