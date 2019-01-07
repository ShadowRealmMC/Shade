package io.shadowrealm.shade.map;

import java.io.Serializable;

public class MapMusic extends MapWorldObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String music;
	private float speed;

	public String getMusic()
	{
		return music;
	}

	public void setMusic(String music)
	{
		this.music = music;
	}

	public float getSpeed()
	{
		return speed;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
