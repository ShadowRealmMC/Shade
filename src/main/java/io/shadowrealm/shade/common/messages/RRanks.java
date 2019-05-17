package io.shadowrealm.shade.common.messages;

import java.util.List;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowRank;
import mortar.api.sql.UniversalType;
import mortar.lang.collection.GList;

public class RRanks extends RestlessObject
{
	@UniversalType(ShadowRank.class)
	private List<ShadowRank> ranks;

	public RRanks()
	{
		super();
		ranks = new GList<>();
	}

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public List<ShadowRank> ranks()
	{
		return ranks;
	}

	public RRanks ranks(List<ShadowRank> ranks)
	{
		this.ranks = ranks;
		return this;
	}
}
