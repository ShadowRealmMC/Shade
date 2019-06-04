package io.shadowrealm.shade.common.table;

import java.util.Map;
import java.util.UUID;

import io.shadowrealm.shade.common.Statistics;
import io.shadowrealm.shade.common.UnlockedItem;
import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;
import mortar.api.sql.UniversalParser;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONException;
import mortar.lang.json.JSONObject;

@Table("shadow_accounts")
public class ShadowAccount
{
	public static final TableCache<UUID, ShadowAccount> CACHE = new TableCache<UUID, ShadowAccount>(1024);

	@Column(name = "id", type = "VARCHAR(36)", placeholder = "<ERROR: UNDEFINED>", primary = true)
	private UUID id;

	@Column(name = "cached_name", type = "VARCHAR(16)", placeholder = "unknown")
	private String cachedName;

	@Column(name = "cached_server", type = "VARCHAR(24)", placeholder = "unidentified")
	private String cachedServer;

	@Column(name = "unlocks", type = "TEXT", placeholder = "")
	private String unlocks;

	@Column(name = "settings", type = "TEXT", placeholder = "")
	private String settings;

	@Column(name = "statistics", type = "TEXT", placeholder = "")
	private String statistics;

	@Column(name = "last_cycle", type = "BIGINT", placeholder = "0")
	private long lastCycle;

	@Column(name = "shadow_xp", type = "BIGINT", placeholder = "0")
	private long shadowXP;

	@Column(name = "shadow_xp_earned", type = "BIGINT", placeholder = "0")
	private long shadowXPEarned;

	@Column(name = "shadow_xp_last_earned", type = "BIGINT", placeholder = "0")
	private long shadowXPLastEarned;

	@Column(name = "amethyst", type = "BIGINT", placeholder = "0")
	private long amethyst;

	public ShadowAccount()
	{
		this(UUID.randomUUID());
	}

	public ShadowAccount(UUID id)
	{
		this.id = id;
		this.cachedName = "unknown";
		this.cachedServer = "unknown";
		this.shadowXP = 0;
		this.shadowXPEarned = 0;
		this.shadowXPLastEarned = 0;
		this.amethyst = 0;
		this.lastCycle = 0;
		this.unlocks = "";
		this.statistics = "";
		this.settings = "";
	}

	public long getLastCycle()
	{
		return lastCycle;
	}

	public void setLastCycle(long lastCycle)
	{
		this.lastCycle = lastCycle;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getCachedName()
	{
		return cachedName;
	}

	public void setCachedName(String cachedName)
	{
		this.cachedName = cachedName;
	}

	public long getShadowXP()
	{
		return shadowXP;
	}

	public void setShadowXP(long shadowXP)
	{
		this.shadowXP = shadowXP;
	}

	public long getShadowXPEarned()
	{
		return shadowXPEarned;
	}

	public void setShadowXPEarned(long shadowXPEarned)
	{
		this.shadowXPEarned = shadowXPEarned;
	}

	public long getShadowXPLastEarned()
	{
		return shadowXPLastEarned;
	}

	public void setShadowXPLastEarned(long shadowXPLastEarned)
	{
		this.shadowXPLastEarned = shadowXPLastEarned;
	}

	public long getAmethyst()
	{
		return amethyst;
	}

	public void setAmethyst(long amethyst)
	{
		this.amethyst = amethyst;
	}

	public static TableCache<UUID, ShadowAccount> getCache()
	{
		return CACHE;
	}

	public String getCachedServer()
	{
		return cachedServer;
	}

	public void setCachedServer(String cachedServer)
	{
		this.cachedServer = cachedServer;
	}

	public JSONObject getSettings()
	{
		try
		{
			return new JSONObject(settings);
		}

		catch(JSONException e)
		{

		}

		return new JSONObject();
	}

	public void setSettings(JSONObject o)
	{
		try
		{
			settings = o.toString(0);
		}

		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	public Map<String, UnlockedItem> getUnlocks()
	{
		if(unlocks.isEmpty())
		{
			return new GMap<>();
		}

		try
		{
			GMap<String, UnlockedItem> items = new GMap<>();
			JSONObject object = new JSONObject(unlocks);

			for(String i : object.keySet())
			{
				try
				{
					items.put(i, UniversalParser.fromJSON(object.getJSONObject(i), UnlockedItem.class));
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			return items;
		}

		catch(Throwable e)
		{
			return new GMap<>();
		}
	}

	public void setUnlocks(Map<String, UnlockedItem> items)
	{
		JSONObject o = new JSONObject();

		for(String i : items.keySet())
		{
			try
			{
				o.put(i, UniversalParser.toJSON(items.get(i)));
			}

			catch(JSONException | IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			unlocks = o.toString(0);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void setSettings(String settings)
	{
		this.settings = settings;
	}

	public Statistics getStatistics()
	{
		return new Statistics().fill(statistics);
	}

	public void setStatistics(Statistics s)
	{
		statistics = s.toString();
	}

	public void setStatistics(String s)
	{
		statistics = s;
	}

	public UnlockedItem getUnlock(String id)
	{
		return getUnlocks().get(id);
	}

	public boolean hasUnlock(String id)
	{
		return getUnlocks().containsKey(id);
	}
}
