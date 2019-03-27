package io.shadowrealm.shade.object;

import io.shadowrealm.shade.proxy.ShadowProxyConfig;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.json.JSONArray;

public class XPLedger
{
	private GList<XPLog> logs;

	public XPLedger()
	{
		logs = new GList<>();
	}

	public XPLedger(JSONArray o)
	{
		this();

		for(int i = 0; i < o.length(); i++)
		{
			logs.add(new XPLog(o.getJSONObject(i)));
		}
	}

	public JSONArray toJSON()
	{
		JSONArray ja = new JSONArray();

		for(XPLog i : logs)
		{
			ja.put(i.toJSON());
		}

		return ja;
	}

	public int computeRank()
	{
		clean();
		long d = M.epochDays();
		int rank = 0;
		double max = ShadowProxyConfig.SHADOW_PLAYER__RANK__AMPLIFIER_CEILING;
		double min = ShadowProxyConfig.SHADOW_PLAYER__RANK__AMPLIFIER_FLOOR;
		double mul = ShadowProxyConfig.SHADOW_PLAYER__RANK__AVERAGE_RADIUS;

		for(int i = 0; i <= mul; i++)
		{
			double progress = (double) (i) / (double) mul;
			double stepup = ((1D - progress) * (max - min)) + min;
			XPLog l = getLog(d - i);
			rank += l.getChange() * stepup;
		}

		return M.iclip(rank / mul, ShadowProxyConfig.SHADOW_PLAYER__RANK__MINIMUM, ShadowProxyConfig.SHADOW_PLAYER__RANK__MAXIMUM);
	}

	public void modXP(int xp)
	{
		clean();
		getLog(M.epochDays()).mod(xp);
	}

	private XPLog getLog(long epochDays)
	{
		for(XPLog i : logs.copy())
		{
			if(i.getEpochDay() == epochDays)
			{
				return i;
			}
		}

		XPLog x = new XPLog(0, epochDays);
		logs.add(x);

		return x;
	}

	private void clean()
	{
		GList<Long> has = new GList<>();

		for(XPLog i : logs.copy())
		{
			if(!has.contains(i.getEpochDay()))
			{
				has.add(i.getEpochDay());
			}

			else
			{
				logs.remove(i);
				continue;
			}

			if(i.getEpochDay() < M.epochDays() - ShadowProxyConfig.SHADOW_PLAYER__RANK__AVERAGE_RADIUS)
			{
				logs.remove(i);
			}
		}
	}
}
