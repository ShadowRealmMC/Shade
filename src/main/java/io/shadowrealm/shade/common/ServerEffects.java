package io.shadowrealm.shade.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.shadowrealm.shade.common.table.ShadowUnlock;
import io.shadowrealm.shade.server.ServerConfig;
import io.shadowrealm.shade.server.ShadeServer;
import mortar.api.sql.UniversalParser;
import mortar.api.sql.UniversalType;
import mortar.compute.math.M;
import mortar.lang.json.JSONObject;
import mortar.logic.format.F;
import mortar.logic.io.VIO;

public class ServerEffects
{
	@UniversalType(ServerEffect.class)
	private List<ServerEffect> effects;

	public ServerEffects()
	{
		effects = new ArrayList<ServerEffect>();
	}

	public void addAll(long time)
	{
		for(ServerEffect i : effects)
		{
			i.setEndsAt(i.getEndsAt() + time);
		}

		save();
	}

	public boolean addEffect(ShadowUnlock u, int points)
	{
		JSONObject meta = new JSONObject();
		long timeAdd = points * meta.getInt("duration");

		for(ServerEffect i : effects)
		{
			if(i.getId().equals(u.getId()))
			{
				if((i.getEndsAt() + timeAdd) - M.ms() > TimeUnit.MINUTES.toMillis(ServerConfig.BOOSTERS__MAXIMUM_MINUTES))
				{
					return false;
				}

				i.setEndsAt(i.getEndsAt() + timeAdd);
				save();
				return true;
			}
		}

		if(timeAdd > TimeUnit.MINUTES.toMillis(ServerConfig.BOOSTERS__MAXIMUM_MINUTES))
		{
			return false;
		}

		effects.add(new ServerEffect(u.getId(), M.ms() + timeAdd));
		save();
		return true;
	}
	
	public static ServerEffects load()
	{
		File f = new File(ShadeServer.instance.getDataFolder(), "active-effects.json");
		f.getParentFile().mkdirs();
		
		if(f.exists())
		{
			try
			{
				return UniversalParser.fromJSON(new JSONObject(VIO.readAll(f)), ServerEffects.class);
			}
			
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public void save()
	{
		File f = new File(ShadeServer.instance.getDataFolder(), "active-effects.json");
		f.getParentFile().mkdirs();

		try
		{
			JSONObject o = UniversalParser.toJSON(this);
			o.put("last-modified", F.stamp(M.ms()));
			VIO.writeAll(f, o.toString(4));
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public List<ServerEffect> getEffects()
	{
		return effects;
	}

	public void setEffects(List<ServerEffect> effects)
	{
		this.effects = effects;
	}
}
