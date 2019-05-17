package io.shadowrealm.shade.common;

import mortar.lang.collection.Callback;

public interface RestlessCompletable
{
	public RestlessObject handle();

	public RestlessObject complete(RestlessConnector c);

	public void complete(RestlessConnector co, Callback<RestlessObject> c);
}
