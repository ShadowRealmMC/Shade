package io.shadowrealm.shade.object;

import mortar.lang.json.JSONObject;

public class XPLog
{
	private int change;
	private long epochDay;

	public XPLog(int change, long epochDay)
	{
		this.change = change;
		this.epochDay = epochDay;
	}

	public XPLog(JSONObject o)
	{
		this.change = o.getInt("c");
		this.epochDay = o.getLong("d");
	}

	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();
		j.put("c", change);
		j.put("d", epochDay);
		return j;
	}

	public int getChange()
	{
		return change;
	}

	public void setChange(int change)
	{
		this.change = change;
	}

	public long getEpochDay()
	{
		return epochDay;
	}

	public void setEpochDay(long epochDay)
	{
		this.epochDay = epochDay;
	}

	public void mod(int xp)
	{
		setChange(getChange() + xp);
	}
}
