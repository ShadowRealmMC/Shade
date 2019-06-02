package io.shadowrealm.shade.common.messages;

import java.util.Map;
import java.util.UUID;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import io.shadowrealm.shade.server.ShadeServer;

public class RSpendUnlock extends RestlessObject
{
	private UUID player;
	private String unlock;

	@Override
	public RestlessObject handle()
	{
		ShadowAccount a = ShadeServer.instance.getSQL().getAccount(player);
		ShadowUnlock u = ShadeServer.instance.getSQL().getUnlock(unlock);

		if(!u.isConsumable())
		{
			return new RError().message("Cannot consume this unlock type: " + unlock);
		}

		Map<String, UnlockedItem> items = a.getUnlocks();

		if(items.containsKey(u.getId()))
		{
			UnlockedItem uu = items.get(u.getId());
			uu.setAmount(uu.getAmount() - 1);

			if(uu.getAmount() > 0)
			{
				items.put(u.getId(), new UnlockedItem(u.getId(), uu.getAmount()));
			}

			else
			{
				items.remove(u.getId());
			}

			a.setUnlocks(items);
			ShadeServer.instance.getSQL().setAccount(a);
			return new ROK();
		}

		return new RError("Cannot find unlock in player");
	}

	public UUID player()
	{
		return player;
	}

	public String unlock()
	{
		return unlock;
	}

	public RSpendUnlock player(UUID player)
	{
		this.player = player;
		return this;
	}

	public RSpendUnlock unlock(String unlock)
	{
		this.unlock = unlock;
		return this;
	}
}
