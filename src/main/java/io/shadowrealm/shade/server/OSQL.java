package io.shadowrealm.shade.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowRank;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.sql.SQLKit;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.logic.format.F;
import mortar.util.text.C;

public class OSQL
{
	private SQLKit k;
	private GList<ShadowUnlock> unlocks;

	public OSQL(Connection connection)
	{
		unlocks = new GList<>();
		k = new SQLKit(connection, ServerConfig.DATABASE__LOG_SQL);
		getRanks();

		try
		{
			generateUnlocks();
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		unlocks = new GList<>(getUnlocks());
	}

	private void generateUnlocks() throws SQLException
	{
		// Chat Colors & Formats
		String g = "&" + C.GRAY.getChar();

		for(C i : C.values())
		{
			String c = "&" + i.getChar();
			String n = F.capitalizeWords(i.name().toLowerCase().replaceAll("\\Q_\\E", " "));

			if(i.isColor())
			{
				generateUnlock("chatcolor:" + i.name().toLowerCase(), false, true, c + n + " Chat Color", g + "Grants the ability to use the color " + n + " when chatting.");
			}

			else
			{
				generateUnlock("chatformat:" + i.name().toLowerCase(), false, true, g + c + n + "&r" + g + " Chat Format", g + "Grants the ability to use the format " + n + " when chatting.");
			}
		}

		generateUnlock("loot:box", true, false, "Loot Box", "There's stuff inside. Open it.");
		generateUnlock("loot:shard_multitude", true, false, "Shard of Multitude", "Modify a loot box to give more stuff.");
		generateUnlock("loot:shard_valor", true, false, "Shard of Valor", "Modify a loot box to give more combat oriented stuff.");
		generateUnlock("loot:shard_divergence", true, false, "Shard of Divergence", "Modify a loot box to give things not unlocked by anyone recently. (Shard of Hipster)");
		generateUnlock("loot:shard_magnitude", true, false, "Shard of Magnitude", "Modify a loot box to give rarer stuff.");
		generateUnlock("loot:shard_impact", true, false, "Shard of Impact", "Modify a loot box to give stuff which adds effects to gameplay.");
		generateUnlock("loot:shard_ally", true, false, "Shard of Ally", "Modify a loot box to give stuff related to pets & companions.");

		for(C i : C.values())
		{
			if(i.isColor())
			{
				String n = F.capitalizeWords(i.name().toLowerCase().replaceAll("\\Q_\\E", " "));
				generateUnlock("loot:shard_" + i.name().toLowerCase(), true, false, "Shard of " + n, "Modify a loot box to give stuff colored " + n);
			}
		}
	}

	public void generateUnlock(String cid, boolean consumable, boolean singleton, String name, String description) throws SQLException
	{
		String category = cid.split("\\Q:\\E")[0];
		String id = cid.split("\\Q:\\E")[1];
		ShadowUnlock l = new ShadowUnlock(id);
		l.setType(category);
		l.setSingleton(singleton);
		l.setConsumable(consumable);
		l.setName(name);
		l.setDescription(description);

		k.validate(l);
		if(!k.has("shadow_unlocks", "id", l.getId()))
		{
			k.set(l);
		}
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

	public SQLKit getKit()
	{
		return k;
	}

	public List<ShadowUnlock> getUnlocks()
	{
		if(!unlocks.isEmpty())
		{
			return unlocks;
		}

		try
		{
			return k.getAllFor(ShadowUnlock.class, new GList<ShadowUnlock>(), () -> new ShadowUnlock("durr"));
		}

		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return new GList<>();
	}

	public ShadowUnlock getUnlock(String id)
	{
		for(ShadowUnlock i : getUnlocks())
		{
			if(i.getId().equals(id.toLowerCase()))
			{
				return i;
			}

			if(id.toLowerCase().equals(i.getType() + ":" + i.getId()))
			{
				return i;
			}
		}

		return null;
	}
}
