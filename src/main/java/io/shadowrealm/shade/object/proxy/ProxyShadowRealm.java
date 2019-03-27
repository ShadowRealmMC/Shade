package io.shadowrealm.shade.object.proxy;

import java.util.UUID;

import io.shadowrealm.shade.object.ShadowPlayer;
import io.shadowrealm.shade.object.ShadowRealm;
import io.shadowrealm.shade.object.ShadowServer;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;

public class ProxyShadowRealm implements ShadowRealm
{
	private GMap<String, ShadowServer> servers;
	private GMap<UUID, ShadowPlayer> players;

	public ProxyShadowRealm()
	{
		servers = new GMap<>();
		players = new GMap<>();
	}

	@Override
	public GList<ShadowServer> getServers()
	{
		return servers.v();
	}

	@Override
	public ShadowPlayer getPlayer(UUID id)
	{
		return players.get(id);
	}

	@Override
	public GList<ShadowPlayer> getOnlinePlayers()
	{
		return players.v();
	}

	@Override
	public GList<ShadowPlayer> getPlayers(String search)
	{
		// TODO Auto-generated method stub
		return new GList<>();
	}

	@Override
	public GList<ShadowPlayer> getOnlinePlayers(String search)
	{
		// TODO Auto-generated method stub
		return new GList<>();
	}

	@Override
	public void login(ShadowPlayer player)
	{
		players.put(player.getRealUUID(), player);
	}

	@Override
	public void targetServer(ShadowPlayer player, ShadowServer server)
	{
		verifyServer(server.getName());
		player.assignServer(getServer(server.getName()));
	}

	@Override
	public void verifyServer(String name)
	{
		if(!servers.containsKey(name))
		{
			servers.put(name, new ProxyShadowServer(name));
		}
	}

	@Override
	public ShadowServer getServer(String name)
	{
		return servers.get(name);
	}
}
