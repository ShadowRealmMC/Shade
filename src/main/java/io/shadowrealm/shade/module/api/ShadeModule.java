package io.shadowrealm.shade.module.api;

import org.bukkit.event.Listener;

import io.shadowrealm.shade.client.ShadeClient;
import mortar.api.config.Configurator;
import mortar.bukkit.command.MortarCommand;
import mortar.util.text.D;
import mortar.util.text.Logged;

public abstract class ShadeModule implements IMod, Logged, Listener
{
	private final String name;

	public ShadeModule(String name)
	{
		this.name = name;
	}

	public void loadConfiguration()
	{
		Configurator.DEFAULT.load(getClass(), ShadeClient.instance.getDataFile("modules", getName() + ".yml"));
	}

	public void registerCommand(MortarCommand cmd)
	{
		ShadeClient.instance.registerCommand(cmd);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public abstract boolean isEnabled();

	@Override
	public abstract void start();

	@Override
	public void l(Object... l)
	{
		D.as(this).l(l);
	}

	@Override
	public void w(Object... l)
	{
		D.as(this).w(l);
	}

	@Override
	public void f(Object... l)
	{
		D.as(this).f(l);
	}

	@Override
	public void v(Object... l)
	{
		D.as(this).v(l);
	}
}
