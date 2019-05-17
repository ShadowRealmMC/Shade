package io.shadowrealm.shade.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowRank;
import mortar.api.sql.SQLKit;
import mortar.compute.math.M;
import mortar.lang.collection.GList;

public class OSQL
{
	private SQLKit k;

	public OSQL(Connection connection)
	{
		k = new SQLKit(connection, ServerConfig.DATABASE__LOG_SQL);
		getRanks();
	}

	public void cycle(ShadowAccount a)
	{
		if(M.ms() - a.getLastCycle() > TimeUnit.valueOf(ServerConfig.RANKING__CYCLE_TIME__UNIT).toMillis(ServerConfig.RANKING__CYCLE_TIME__VALUE))
		{
			long earned = a.getShadowXPEarned();
			long lastEarned = a.getShadowXPLastEarned();

			lastEarned = (long) (((double) lastEarned * ServerConfig.RANKING__OVERBLEED) + (double) earned);
			earned = (long) (ServerConfig.RANKING__RETAIN * (double) lastEarned);

			a.setShadowXPLastEarned(lastEarned);
			a.setShadowXPEarned(earned);
			a.setLastCycle(M.ms());
			setAccount(a);
		}
	}

	public GList<ShadowRank> getRanks()
	{
		if(ShadowRank.CACHE.size() == 0)
		{
			try
			{
				GList<ShadowRank> r = k.getAllFor(ShadowRank.class, new GList<ShadowRank>(), () -> new ShadowRank("durr"));

				if(r.isEmpty())
				{
					r.addAll(generateDefaultShadowRanks());

					for(ShadowRank i : r)
					{
						setRank(i);
					}
				}

				for(ShadowRank i : r)
				{
					ShadowRank.CACHE.put(i.getId(), i);
				}
			}

			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

		return ShadowRank.CACHE.getValues();
	}

	private GList<ShadowRank> generateDefaultShadowRanks()
	{
		GList<ShadowRank> g = new GList<ShadowRank>();
		g.add(new ShadowRank("abomnination", "Abomination", Long.MIN_VALUE, -4001, 1));
		g.add(new ShadowRank("forsaken", "Forsaken", -4000, -3001, 1));
		g.add(new ShadowRank("condemned", "Condemned", -3000, -2001, 1));
		g.add(new ShadowRank("shunned", "Shunned", -2000, -1001, 1));
		g.add(new ShadowRank("undesired", "Undesired", -1000, -501, 1));
		g.add(new ShadowRank("unranked", "Unranked", -500, 499, 1));
		g.add(new ShadowRank("bronze", "Bronze", 500, 999, 1));
		g.add(new ShadowRank("silver", "Silver", 1000, 1999, 1));
		g.add(new ShadowRank("gold", "Gold", 2000, 2999, 1));
		g.add(new ShadowRank("platinum", "Platinum", 3000, 3999, 1));
		g.add(new ShadowRank("diamond", "Diamond", 4000, 4999, 1));
		g.add(new ShadowRank("master", "Master", 5000, 5999, 1));
		g.add(new ShadowRank("grandmaster", "Grandmaster", 6000, 6999, 1));
		g.add(new ShadowRank("legion", "Legion", 7000, Long.MAX_VALUE, 1));

		return g;
	}

	public boolean setAccount(ShadowAccount a)
	{
		try
		{
			if(k.set(a))
			{
				ShadowAccount.CACHE.put(a.getId(), a);
				return true;
			}
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean setRank(ShadowRank a)
	{
		try
		{
			if(k.set(a))
			{
				ShadowRank.CACHE.put(a.getId(), a);
				return true;
			}
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public ShadowAccount getAccount(UUID id)
	{
		if(ShadowAccount.CACHE.has(id))
		{
			return ShadowAccount.CACHE.get(id);
		}

		try
		{
			k.validate(new ShadowAccount());

			if(!k.has("shadow_accounts", "id", id.toString()))
			{
				ShadowAccount a = new ShadowAccount(id);
				setAccount(a);
				return getAccount(a.getId());
			}
		}

		catch(SQLException e1)
		{
			e1.printStackTrace();
			return null;
		}

		ShadowAccount sa = new ShadowAccount(id);

		try
		{
			if(k.get(sa))
			{
				ShadowAccount.CACHE.put(id, sa);
				return sa;
			}
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public ShadowRank getRank(String id)
	{
		if(ShadowRank.CACHE.has(id))
		{
			return ShadowRank.CACHE.get(id);
		}

		ShadowRank sa = new ShadowRank(id);

		try
		{
			if(k.get(sa))
			{
				ShadowRank.CACHE.put(id, sa);
				return sa;
			}
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
