package io.shadowrealm.shade.module;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.shadowrealm.shade.common.CommandingPlayer;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.sched.J;
import mortar.bukkit.plugin.MortarAPIPlugin;
import mortar.lang.collection.GList;

public class SMCommands extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	//@builder
	@Key("cooldowns")
	public static GList<String> commandCooldowns = new GList<String>()
	.qadd("tp,tpa,tpo,tppos,j,jump,world,warp,mv tp=3000")
	.qadd("sky new,is new,sky create,is create,sky reboot,is reboot,sky delete,is delete=5000");
	//@done

	//@builder
	@Key("delays")
	public static GList<String> commandDelays = new GList<String>()
	.qadd("tp,tpa,tpo,tppos,j,jump,world,warp,mv tp=2000");
	//@done

	private GList<CommandingPlayer> commanders;

	public SMCommands()
	{
		super("Commands");
		commanders = new GList<>();
	}

	@Override
	public void start()
	{
		J.ar(() -> tickPlayers(), 3);
	}

	private void tickPlayers()
	{
		for(CommandingPlayer i : commanders)
		{
			if(i.needsTicking())
			{
				i.tick();
			}
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		commanders.add(new CommandingPlayer(e.getPlayer()));
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		for(CommandingPlayer i : commanders.copy())
		{
			if(i.p.equals(e.getPlayer()))
			{
				MortarAPIPlugin.p.unregisterListener(i);
				commanders.remove(i);
			}
		}
	}
}
