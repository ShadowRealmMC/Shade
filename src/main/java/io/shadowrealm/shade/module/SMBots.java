package io.shadowrealm.shade.module;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.util.text.C;

public class SMBots extends ShadeModule
{
	private GMap<Player, Double> moveDistance;
	private GList<String> moveMoreMessages;

	@Key("enable")
	public static boolean enabled = true;

	@Key("on-join.movement-required-to-talk")
	public static double distanceRequired = 3.5;

	@Permission
	public static PermissionShade perm;

	public SMBots()
	{
		super("Bots");
	}

	@Override
	public void start()
	{
		moveDistance = new GMap<>();
		moveMoreMessages = new GList<>();
		moveMoreMessages.add("Move around a bit before chatting.");
		moveMoreMessages.add("Walk around a little before talking.");
		moveMoreMessages.add("You should move around before chatting.");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(AsyncPlayerChatEvent e)
	{
		if(moveDistance.containsKey(e.getPlayer()))
		{
			e.setCancelled(true);

			Styles.superBorder(e.getPlayer(), C.YELLOW + "" + C.BOLD + moveMoreMessages.pickRandom(), C.YELLOW, C.GOLD);
		}
	}

	@EventHandler
	public void on(PlayerMoveEvent e)
	{
		if(moveDistance.containsKey(e.getPlayer()))
		{
			Double at = moveDistance.get(e.getPlayer());
			Location from = e.getFrom().clone();
			Location to = e.getTo().clone();
			from.setY(0);
			to.setY(0);
			at -= to.distance(from);

			if(at <= 0)
			{
				moveDistance.remove(e.getPlayer());
			}

			else
			{
				moveDistance.put(e.getPlayer(), at);
			}
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		if(distanceRequired <= 0)
		{
			return;
		}

		moveDistance.put(e.getPlayer(), distanceRequired);
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		moveDistance.remove(e.getPlayer());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
