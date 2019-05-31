package io.shadowrealm.shade.common.table;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;
import mortar.util.text.C;

@Table("shadow_ranks")
public class ShadowRank
{
	public static final TableCache<String, ShadowRank> CACHE = new TableCache<String, ShadowRank>(16);

	@Column(name = "id", type = "VARCHAR(36)", placeholder = "rank_id", primary = true)
	private String id;

	@Column(name = "name", type = "VARCHAR(36)", placeholder = "Rank Name")
	private String name;

	@Column(name = "chat_color", type = "VARCHAR(36)", placeholder = "&f")
	private String chatColor;

	@Column(name = "min_sr", type = "BIGINT", placeholder = "0")
	private long minSR;

	@Column(name = "max_sr", type = "BIGINT", placeholder = "1000")
	private long maxSR;

	@Column(name = "prioirity", type = "INT", placeholder = "1")
	private int prioirty;

	public ShadowRank()
	{
		this("idk");
	}

	public ShadowRank(String id)
	{
		this.id = id;
		this.name = "Rank Name";
		this.minSR = 0;
		this.maxSR = 1000;
		this.prioirty = 1;
	}

	public ShadowRank(String id, String name, long minSR, long maxSR, int priority)
	{
		this.id = id;
		this.name = name;
		this.minSR = minSR;
		this.maxSR = maxSR;
		this.prioirty = priority;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		return C.translateAlternateColorCodes('&', getChatColor() + getName());
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getMinSR()
	{
		return minSR;
	}

	public void setMinSR(long minSR)
	{
		this.minSR = minSR;
	}

	public long getMaxSR()
	{
		return maxSR;
	}

	public void setMaxSR(long maxSR)
	{
		this.maxSR = maxSR;
	}

	public static TableCache<String, ShadowRank> getCache()
	{
		return CACHE;
	}

	public int getPrioirty()
	{
		return prioirty;
	}

	public void setPrioirty(int prioirty)
	{
		this.prioirty = prioirty;
	}

	public String getChatColor()
	{
		return chatColor;
	}

	public void setChatColor(String chatColor)
	{
		this.chatColor = chatColor;
	}
}
