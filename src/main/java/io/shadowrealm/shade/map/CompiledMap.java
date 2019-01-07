package io.shadowrealm.shade.map;

import com.volmit.phantom.lang.GMap;

public class CompiledMap
{
	private MapData map;
	private GMap<MapPosition, String> warnings;

	public CompiledMap(MapData map)
	{
		this.map = map;
		warnings = new GMap<>();
	}

	public void warn(MapPosition pos, String warn)
	{
		warnings.put(pos, warn);
	}

	public MapData getMap()
	{
		return map;
	}

	public GMap<MapPosition, String> getWarnings()
	{
		return warnings;
	}
}
