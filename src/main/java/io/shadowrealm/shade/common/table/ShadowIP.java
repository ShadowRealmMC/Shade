package io.shadowrealm.shade.common.table;

import java.util.UUID;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;

@Table("shadow_ip")
public class ShadowIP
{
	public static final TableCache<String, ShadowIP> CACHE = new TableCache<String, ShadowIP>(1024);

	@Column(name = "id", type = "VARCHAR(320)", placeholder = "<ERROR: UNDEFINED>", primary = true)
	private String id;

	@Column(name = "player", type = "VARCHAR(36)", placeholder = "unidentified")
	private UUID player;

	@Column(name = "ip", type = "VARCHAR(255)", placeholder = "unknown")
	private String ip;


	public ShadowIP()
	{
		this(UUID.randomUUID(), "error");
	}

	public ShadowIP(String id)
	{
		this.id = id;
	}

	public ShadowIP(UUID player, String ip)
	{
		this.id = player.toString() + ":" + ip;
		this.player = player;
		this.ip = ip;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
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

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}
}
