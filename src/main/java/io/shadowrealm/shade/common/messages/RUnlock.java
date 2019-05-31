package io.shadowrealm.shade.common.messages;

import java.util.Map;
import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import io.shadowrealm.shade.server.ShadeServer;

public class RUnlock extends RestlessObject
{
	private UUID player;
	private UnlockedItem unlock;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player);
		ShadowUnlock u = ShadeServer.instance.getSQL().getUnlock(unlock.getId());

		if(u.isSingleton() && unlock.getAmount() != 1)
		{
			return new RError().message("Cannot give more than 1 " + unlock.getId() + ". It's a singleton unlock.");
		}

		if(unlock.getAmount() < 1)
		{
			return new RError().message("Cannot give less than 1 " + unlock.getId() + ".");
		}

		Map<String, UnlockedItem> items = a.getUnlocks();

		if(items.containsKey(u.getId()) && u.isSingleton())
		{
			return new RError().message("Cannot give another " + unlock.getId() + ". Player already has it.");
		}

		if(items.containsKey(u.getId()))
		{
			items.put(u.getId(), new UnlockedItem(u.getId(), items.get(u.getId()).getAmount() + unlock.getAmount()));
			return new ROK();
		}

		if(!items.containsKey(u.getId()))
		{
			items.put(u.getId(), unlock);
			return new ROK();
		}

		return null;
	}
}
