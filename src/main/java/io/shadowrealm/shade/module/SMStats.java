package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandStats;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;

public class SMStats extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	public SMStats()
	{
		super("Stats");
	}

	@Override
	public void start()
	{
		l("Registering Stat Commands");
		registerCommand(new CommandStats());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
