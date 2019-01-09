package io.shadowrealm.shade.map.config;

import com.volmit.phantom.json.JSONObject;
import com.volmit.phantom.lang.GList;

public class MapConfig
{
	private String schematic;
	private String mapName;
	private String variationName;
	private String mapAuthors;
	private int time;
	private String activation; // always || MONTH/DAY
	private GList<String> replacements;

	public MapConfig()
	{
		this.schematic = "null.schematic";
		this.mapName = "Map Name";
		this.time = 6000;
		this.variationName = "Variation Name";
		this.mapAuthors = "Tragic_Psycho & SwiftSweat";
		this.activation = "ALWAYS";
		this.replacements = new GList<>();
	}

	public String id()
	{
		return schematic.replaceAll(".schematic", "") + "-" + mapName.toLowerCase().replaceAll(" ", "") + "-" + variationName.toLowerCase().replaceAll(" ", "");
	}

	public MapConfig(JSONObject j)
	{
		this();
		fromJSON(j);
	}

	public void fromJSON(JSONObject j)
	{
		schematic = j.getString("schematic");
		time = j.getInt("time");
		mapName = j.getString("name");
		variationName = j.getString("variation");
		mapAuthors = j.getString("authors");
		activation = j.getString("activation");
		replacements = GList.from(j.getJSONArray("replacements"));
	}

	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();
		j.put("schematic", schematic);
		j.put("name", mapName);
		j.put("time", time);
		j.put("variation", variationName);
		j.put("authors", mapAuthors);
		j.put("activation", activation);
		j.put("replacements", replacements.toJSONStringArray());

		return j;
	}

	public String getSchematic()
	{
		return schematic;
	}

	public void setSchematic(String schematic)
	{
		this.schematic = schematic;
	}

	public String getMapName()
	{
		return mapName;
	}

	public void setMapName(String mapName)
	{
		this.mapName = mapName;
	}

	public String getVariationName()
	{
		return variationName;
	}

	public void setVariationName(String variationName)
	{
		this.variationName = variationName;
	}

	public String getMapAuthors()
	{
		return mapAuthors;
	}

	public void setMapAuthors(String mapAuthors)
	{
		this.mapAuthors = mapAuthors;
	}

	public String getActivation()
	{
		return activation;
	}

	public void setActivation(String activation)
	{
		this.activation = activation;
	}

	public GList<String> getReplacements()
	{
		return replacements;
	}

	public void setReplacements(GList<String> replacements)
	{
		this.replacements = replacements;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}
}
