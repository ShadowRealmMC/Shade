package io.shadowrealm.shade.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.volmit.phantom.lang.GList;

public class MapData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private String author;
	private int width;
	private int depth;
	private MapPosition center;
	private List<MapSpawn> spawns;
	private List<MapRegion> regions;
	private List<MapMusic> music;
	private Map<ColorSpace, MapColor> colors;

	public MapData()
	{
		spawns = new ArrayList<>();
		regions = new ArrayList<>();
		music = new ArrayList<>();
		colors = new HashMap<>();
	}

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

	public List<MapSpawn> getSpawns()
	{
		return spawns;
	}

	public void setSpawns(GList<MapSpawn> spawns)
	{
		this.spawns = spawns;
	}

	public List<MapRegion> getRegions()
	{
		return regions;
	}

	public void setRegions(GList<MapRegion> regions)
	{
		this.regions = regions;
	}

	public List<MapMusic> getMusic()
	{
		return music;
	}

	public void setMusic(GList<MapMusic> music)
	{
		this.music = music;
	}

	public Map<ColorSpace, MapColor> getColors()
	{
		return colors;
	}

	public void setColors(Map<ColorSpace, MapColor> colors)
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
