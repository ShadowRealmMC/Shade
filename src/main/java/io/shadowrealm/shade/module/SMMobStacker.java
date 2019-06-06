package io.shadowrealm.shade.module;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;

public class SMMobStacker extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	public SMMobStacker()
	{
		super("MobStacker");
	}

	@EventHandler
	public void on(SpawnerSpawnEvent e)
	{

	}

	@Override
	public void start()
	{

	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
