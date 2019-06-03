package io.shadowrealm.shade.module;

import io.shadowrealm.shade.client.command.CommandReboot;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;

public class SMReboot extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	public SMReboot()
	{
		super("Reboot");
	}

	@Override
	public void start()
	{
		registerCommand(new CommandReboot());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
