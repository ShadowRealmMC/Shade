package io.shadowrealm.shade.common;

import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONObject;

public class Statistics
{
	private GMap<String, StatisticTracker> trackers;

	public Statistics()
	{
		trackers = new GMap<>();
	}

	public StatisticTracker get(String id)
	{
		if(!trackers.containsKey(id))
		{
			trackers.put(id, new StatisticTracker());
		}

		return trackers.get(id);
	}

	public void clear()
	{
		trackers.clear();
	}

	public GList<String> k()
	{
		return trackers.k();
	}

	public GList<StatisticTracker> v()
	{
		return trackers.v();
	}

	public GMap<String, StatisticTracker> m()
	{
		return trackers;
	}

	public Statistics fill(String stats)
	{
		try
		{
			clear();
			JSONObject j = new JSONObject(stats);

			for(String i : j.keySet())
			{
				get(i).set(j.getDouble(i));
			}
		}

		catch(Throwable e)
		{

		}

		return this;
	}

	@Override
	public String toString()
	{
		JSONObject o = new JSONObject();

		for(String i : m().keySet())
		{
			o.put(i, get(i).doubleValue());
		}

		return o.toString(0);
	}
}
