package io.shadowrealm.shade.object;

import java.util.UUID;

import mortar.lang.collection.GList;

public interface ShadowRealm
{
	public GList<ShadowServer> getServers();

	public ShadowPlayer getPlayer(UUID id);

	public GList<ShadowPlayer> getPlayers(String search);

	public GList<ShadowPlayer> getOnlinePlayers();

	public GList<ShadowPlayer> getOnlinePlayers(String search);

	public void login(ShadowPlayer player);

	public void targetServer(ShadowPlayer player, ShadowServer server);

	public void verifyServer(String name);

	public ShadowServer getServer(String name);
}
