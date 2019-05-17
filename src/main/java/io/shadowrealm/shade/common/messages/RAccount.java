package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.table.ShadowAccount;

public class RAccount extends RestlessObject
{
	private ShadowAccount shadowAccount;

	@Override
	public RestlessObject handle()
	{
		return null;
	}

	public ShadowAccount shadowAccount()
	{
		return shadowAccount;
	}

	public RAccount shadowAccount(ShadowAccount shadowAccount)
	{
		this.shadowAccount = shadowAccount;
		return this;
	}
}
