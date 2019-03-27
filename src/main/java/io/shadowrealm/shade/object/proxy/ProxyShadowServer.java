package io.shadowrealm.shade.object.proxy;

import io.shadowrealm.shade.object.ShadowServer;

public class ProxyShadowServer implements ShadowServer
{
	private String name;

	public ProxyShadowServer(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}
}
