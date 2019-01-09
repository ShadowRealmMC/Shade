package io.shadowrealm.shade.proxy;

import net.md_5.bungee.api.plugin.Plugin;

public class ShadeBungeecord extends Plugin
{
	public static final int PORT = 18475;
	private SomewhatShittyWebServer server;

	@Override
	public void onEnable()
	{
		server = new SomewhatShittyWebServer(PORT);
		server.start();
	}

	@Override
	public void onDisable()
	{
		server.stopServer();
		server.interrupt();
	}
}
