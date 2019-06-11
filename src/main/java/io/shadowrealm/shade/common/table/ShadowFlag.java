package io.shadowrealm.shade.common.table;

import java.util.UUID;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;

@Table("shadow_flags")
public class ShadowFlag
{
	public enum FlagType
	{
		BAN,
		WARN,
		SUPERVISED,
		GEZUUN,
		MUTED,
		SILENCED;
	}

	public static final TableCache<UUID, ShadowFlag> CACHE = new TableCache<UUID, ShadowFlag>(1024);

	@Column(name = "id", type = "VARCHAR(36)", placeholder = "<ERROR: UNDEFINED>", primary = true)
	private UUID id;

	@Column(name = "player", type = "VARCHAR(36)", placeholder = "ERROR")
	private UUID player;

	@Column(name = "reasoning", type = "BIGINT", placeholder = "0")
	private long bannedUntil;

	@Column(name = "type", type = "VARCHAR(16)", placeholder = "WARN")
	private long type;

	public ShadowFlag()
	{
		this(UUID.randomUUID());
	}

	public ShadowFlag(UUID id)
	{
		this.id = id;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getPlayer()
	{
		return player;
	}

	public void setPlayer(UUID player)
	{
		this.player = player;
	}

	public long getBannedUntil()
	{
		return bannedUntil;
	}

	public void setBannedUntil(long bannedUntil)
	{
		this.bannedUntil = bannedUntil;
	}

	public long getType()
	{
		return type;
	}

	public void setType(long type)
	{
		this.type = type;
	}
}
