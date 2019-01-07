package io.shadowrealm.shade.map;

import java.io.Serializable;

public class MapWorldObject implements Serializable
{
	private static final long serialVersionUID = 6247567941602765212L;

	private MapPosition position;

	public MapPosition getPosition()
	{
		return position;
	}

	public void setPosition(MapPosition position)
	{
		this.position = position;
	}
}
