package io.shadowrealm.shade.map;

import java.io.Serializable;

import com.volmit.phantom.lang.GList;
import com.volmit.phantom.lang.GMap;

public class MapData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private String author;
	private int width;
	private int depth;
	private MapPosition center;
	private GList<MapSpawn> spawns = new GList<>();
	private GList<MapRegion> regions = new GList<>();
	private GList<MapMusic> music = new GList<>();
	private GMap<ColorSpace, MapColor> colors = new GMap<>();

	public String getName()
	{
		return name;
	}

	public MapPosition getCenter()
	{
		return center;
	}

	public void setCenter(MapPosition center)
	{
		this.center = center;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public GList<MapSpawn> getSpawns()
	{
		return spawns;
	}

	public void setSpawns(GList<MapSpawn> spawns)
	{
		this.spawns = spawns;
	}

	public GList<MapRegion> getRegions()
	{
		return regions;
	}

	public void setRegions(GList<MapRegion> regions)
	{
		this.regions = regions;
	}

	public GList<MapMusic> getMusic()
	{
		return music;
	}

	public void setMusic(GList<MapMusic> music)
	{
		this.music = music;
	}

	public GMap<ColorSpace, MapColor> getColors()
	{
		return colors;
	}

	public void setColors(GMap<ColorSpace, MapColor> colors)
	{
		this.colors = colors;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}
}
