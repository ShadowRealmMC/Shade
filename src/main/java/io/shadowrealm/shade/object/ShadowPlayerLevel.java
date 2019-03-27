package io.shadowrealm.shade.object;

import java.util.UUID;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.lang.json.JSONArray;

@Table("shade_player_level")
public class ShadowPlayerLevel
{
	@Column(name = "id", type = "VARCHAR(36)", placeholder = "<ERROR: UNDEFINED>", primary = true)
	private UUID id;

	@Column(name = "shadow_xp", type = "BIGINT", placeholder = "0")
	private long xp;

	@Column(name = "shadow_xp_log", type = "TEXT", placeholder = "[]")
	private String ledger;

	@Column(name = "shadow_rank", type = "BIGINT", placeholder = "0")
	private long rank;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public long getXp()
	{
		return xp;
	}

	public void setXp(long xp)
	{
		this.xp = xp;
	}

	public String getLedgerString()
	{
		return ledger;
	}

	public XPLedger getLedger()
	{
		try
		{
			return new XPLedger(new JSONArray(getLedgerString()));
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return new XPLedger();
	}

	public void setLedger(XPLedger l)
	{
		setLedgerString(l.toJSON().toString(0));
	}

	public void setLedgerString(String ledger)
	{
		this.ledger = ledger;
	}

	public long getRank()
	{
		return rank;
	}

	public void setRank(long rank)
	{
		this.rank = rank;
	}
}
