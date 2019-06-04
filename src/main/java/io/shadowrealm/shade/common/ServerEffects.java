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
import mortar.lang.collection.GList;
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

	public long getTimeMSLeft(String id)
	{
		ServerEffect e = get(id);

		return e != null ? (e.getEndsAt() - M.ms()) : -1;
	}

	public boolean has(String id)
	{
		return get(id) != null;
	}

	public ServerEffect get(String id)
	{
		update();

		for(ServerEffect i : effects)
		{
			if(i.getId().equals(id) || (id.contains(":") && id.split(":")[1].equals(i.getId())))
			{
				return i;
			}
		}

		return null;
	}

	public void update()
	{
		boolean save = false;

		for(ServerEffect i : new GList<>(effects))
		{
			if(M.ms() >= i.getEndsAt())
			{
				System.out.println("Removed Effect " + i.getId() + " TIME: " + F.stamp(M.ms()) + " Ends At " + F.stamp(i.getEndsAt()));
				effects.remove(i);
				save = true;
			}
		}

		if(save)
		{
			save();
		}
	}

	public void addAll(long time)
	{
		update();
		for(ServerEffect i : effects)
		{
			i.setEndsAt(i.getEndsAt() + time);
		}

		save();
	}

	public boolean addEffect(ShadowUnlock u, int points, String playername)
	{
		update();
		JSONObject meta = new JSONObject(u.getMeta());
		long timeAdd = points * (meta.getInt("duration") * TimeUnit.MINUTES.toMillis(1));

		for(ServerEffect i : effects)
		{
			if(i.getId().equals(u.getId()))
			{
				if((i.getEndsAt() + timeAdd) - M.ms() > TimeUnit.MINUTES.toMillis(ServerConfig.BOOSTERS__MAXIMUM_MINUTES))
				{
					System.out.println("Would Exceed the server time limit for a single booster.");
					return false;
				}

				i.setEndsAt(Math.max(i.getEndsAt(), M.ms()) + timeAdd);

				if(!i.getContributors().contains(playername))
				{
					i.getContributors().add(playername);
				}

				save();
				return true;
			}
		}

		if(timeAdd > TimeUnit.MINUTES.toMillis(ServerConfig.BOOSTERS__MAXIMUM_MINUTES))
		{
			System.out.println("Would Exceed the server time limit for a single booster.");
			return false;
		}

		ServerEffect eff = new ServerEffect(u.getId(), M.ms() + timeAdd);
		eff.getContributors().add(playername);
		effects.add(eff);
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
				ServerEffects ss = UniversalParser.fromJSON(new JSONObject(VIO.readAll(f)), ServerEffects.class);
				ss.update();
				return ss;
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return new ServerEffects();
	}

	public void save()
	{
		if(CommonProperties.SIDE == RestlessSide.CLIENT)
		{
			return;
		}

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
		update();
		return effects;
	}

	public void setEffects(List<ServerEffect> effects)
	{
		this.effects = effects;
	}
}
