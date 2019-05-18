package io.shadowrealm.shade.common.table;

import java.util.UUID;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;

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
		id = UUID.randomUUID();
		this.cachedName = "unknown";
		this.shadowXP = 0;
		this.shadowXPEarned = 0;
		this.shadowXPLastEarned = 0;
		this.amethyst = 0;
		this.lastCycle = 0;
	}

	public ShadowAccount(UUID id)
	{
		this.id = id;
		this.cachedName = "unknown";
		this.shadowXP = 0;
		this.shadowXPEarned = 0;
		this.shadowXPLastEarned = 0;
		this.amethyst = 0;
		this.lastCycle = 0;
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
}
