package io.shadowrealm.shade.common.messages;

import java.util.List;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.sql.UniversalType;
import mortar.lang.collection.GList;

public class RUnlocks extends RestlessObject
{
	@UniversalType(ShadowUnlock.class)
	private List<ShadowUnlock> unlocks;

	public RUnlocks()
	{
		super();
		unlocks = new GList<>();
	}

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public List<ShadowUnlock> unlocks()
	{
		return unlocks;
	}

	public RUnlocks unlocks(List<ShadowUnlock> unlocks)
	{
		this.unlocks = unlocks;
		return this;
	}
}
