package io.shadowrealm.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.volume.bukkit.pawn.Async;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Tick;
import com.volmit.volume.bukkit.service.IService;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;

import io.shadowrealm.notification.Playable;

public class NotificationSVC implements IService
{
	private GMap<Player, GList<Playable>> queue;
	private GMap<Player, Long> nextAllowedAction;

	@Start
	public void start()
	{
		queue = new GMap<Player, GList<Playable>>();
		nextAllowedAction = new GMap<Player, Long>();

		for(Player i : Bukkit.getOnlinePlayers())
		{
			join(i);
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		join(e.getPlayer());
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		quit(e.getPlayer());
	}

	@Async
	@Tick(7)
	public void tick()
	{
		System.out.println(TICK.tick);

		for(Player i : queue.k())
		{
			if(nextAllowedAction.get(i) < TICK.tick && !queue.get(i).isEmpty())
			{
				Playable v = queue.get(i).pop();
				nextAllowedAction.put(i, TICK.tick + v.getTotalPlayTime() + 5);

				new S()
				{
					@Override
					public void run()
					{
						v.play(i);
					}
				};
			}
		}
	}

	public void queue(Player player, Playable p)
	{
		queue.get(player).add(p);
	}

	public void queueNow(Player player, Playable p)
	{
		queue.get(player).addFirst(p);
	}

	public void queue(Playable p)
	{
		for(Player i : queue.k())
		{
			queue(i, p);
		}
	}

	public void queueNow(Playable p)
	{
		for(Player i : queue.k())
		{
			queueNow(i, p);
		}
	}

	private void join(Player player)
	{
		queue.put(player, new GList<Playable>());
		nextAllowedAction.put(player, TICK.tick);
	}

	private void quit(Player player)
	{
		queue.remove(player);
		nextAllowedAction.remove(player);
	}
}
