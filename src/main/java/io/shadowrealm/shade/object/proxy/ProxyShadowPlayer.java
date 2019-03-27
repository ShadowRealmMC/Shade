package io.shadowrealm.shade.object.proxy;

import java.util.UUID;

import io.shadowrealm.shade.object.ShadowPlayer;
import io.shadowrealm.shade.object.ShadowPlayerLevel;
import io.shadowrealm.shade.object.ShadowServer;
import io.shadowrealm.shade.proxy.ShadeProxy;

public class ProxyShadowPlayer implements ShadowPlayer
{
	private ShadowPlayerLevel level;
	private String name;
	private UUID id;
	private ShadowServer server;

	public ProxyShadowPlayer(String name, UUID id)
	{
		this.name = name;
		this.id = id;
		level = ShadeProxy.getLevel(id);
	}

	@Override
	public ShadowPlayerLevel getLevel()
	{
		return level;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public UUID getRealUUID()
	{
		return id;
	}

	@Override
	public void assignServer(ShadowServer server)
	{
		this.server = server;
	}

	@Override
	public ShadowServer getServer()
	{
		return server;
	}
}
