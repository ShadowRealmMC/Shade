package io.shadowrealm.shade.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.module.SMCommands;
import mortar.api.sched.J;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandingPlayer implements Listener
{
	public Player p;
	private boolean bypassing;
	private boolean delaying;
	private long delaySchedule;
	private boolean authorizing;
	private String delayedCommand;
	private GMap<String, Long> cooldowns;

	public CommandingPlayer(Player p)
	{
		authorizing = false;
		cooldowns = new GMap<>();
		delaying = false;
		delaySchedule = M.ms();
		delayedCommand = "";
		bypassing = ShadeClient.perm.chat.bypassCommand.has(p);
		bypassing = false;
		this.p = p;

		if(bypassing)
		{
			Shade.unlisten(this);
		}

		else
		{
			Shade.listen(this);
		}
	}

	public boolean needsTicking()
	{
		if(bypassing)
		{
			return false;
		}

		return true;
	}

	public void tick()
	{
		if(delaying && M.ms() >= delaySchedule)
		{
			J.s(() ->
			{
				delaying = false;
				delaySchedule = M.ms() - 1000;
				authorizing = true;
				PlayerCommandPreprocessEvent e = new PlayerCommandPreprocessEvent(p, delayedCommand);
				Bukkit.getPluginManager().callEvent(e);

				if(!e.isCancelled())
				{
					Bukkit.dispatchCommand(p, delayedCommand.substring(1));
					Styles.soundCommandSend(p);
					p.sendMessage(C.GRAY + "Ran " + C.WHITE + delayedCommand);
				}

				authorizing = false;
				delayedCommand = "";
			});
		}
	}

	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(bypassing)
		{
			return;
		}

		if(!e.getPlayer().equals(p))
		{
			return;
		}

		if(!e.getFrom().getBlock().equals(e.getTo().getBlock()))
		{
			if(delaying)
			{
				delaying = false;
				delaySchedule = M.ms() - 1000;
				delayedCommand = "";
				Styles.soundShout(p);
				p.sendMessage(C.GRAY + "Cancelled Previous /" + delayedCommand + " (Moved)");
			}
		}
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent e)
	{
		if(bypassing)
		{
			return;
		}

		if(!e.getPlayer().equals(p))
		{
			return;
		}

		if(delaying)
		{
			delaying = false;
			delaySchedule = M.ms() - 1000;
			delayedCommand = "";
			Styles.soundShout(p);
			p.sendMessage(C.GRAY + "Cancelled Previous /" + delayedCommand + " (World)");
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDelay(PlayerCommandPreprocessEvent e)
	{
		if(bypassing)
		{
			return;
		}

		if(!e.getPlayer().equals(p))
		{
			return;
		}

		if(authorizing)
		{
			return;
		}

		String filter = e.getMessage().trim();
		filter = filter.charAt(0) == '/' ? filter.substring(1) : filter;
		filter = filter.charAt(0) == '/' ? filter.substring(1) : filter;
		filter = filter.charAt(0) == '/' ? filter.substring(1) : filter;
		filter = filter.toLowerCase();

		looking: for(String i : SMCommands.commandCooldowns)
		{
			String cv = i.split("\\Q=\\E")[0];
			GList<String> strings = new GList<String>(cv.split("\\Q,\\E"));
			long cooldownTime = Long.valueOf(i.split("\\Q=\\E")[1]);

			for(String j : strings)
			{
				if(filter.startsWith(j.toLowerCase().trim()))
				{
					if(cooldowns.containsKey(i))
					{
						long left = cooldowns.get(i) - M.ms();

						if(left <= 0)
						{
							cooldowns.put(i, M.ms() + cooldownTime);
							break looking;
						}

						e.setCancelled(true);
						Styles.soundShout(p);
						e.getPlayer().sendMessage(C.GRAY + "Cancelled: Wait " + (left < 2000 ? "a second" : F.timeLong(left, 0)) + ".");
						return;
					}

					cooldowns.put(i, M.ms() + cooldownTime);
					break looking;
				}
			}
		}

		for(String i : SMCommands.commandDelays)
		{
			String cv = i.split("\\Q=\\E")[0];
			GList<String> strings = new GList<String>(cv.split("\\Q,\\E"));
			long delay = Long.valueOf(i.split("\\Q=\\E")[1]);

			for(String j : strings)
			{
				if(filter.startsWith(j.toLowerCase().trim()))
				{
					if(delaying)
					{
						delaying = false;
						p.sendMessage(C.GRAY + "Cancelled Previous /" + delayedCommand + " (Ran Command)");
						Styles.soundShout(p);
						delayedCommand = "";
						delaySchedule = M.ms() - 10000;
					}

					p.sendMessage(C.GRAY + "Running " + C.WHITE + delayedCommand + C.GRAY + "in " + (delay < 2000 ? "a second" : F.time(delay, 0)));
					e.setCancelled(true);
					delaying = true;
					delaySchedule = M.ms() + delay;
					delayedCommand = e.getMessage();
					return;
				}
			}
		}
	}
}