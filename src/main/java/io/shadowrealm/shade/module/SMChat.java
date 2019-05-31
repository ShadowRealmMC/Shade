package io.shadowrealm.shade.module;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.client.command.CommandChat;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.bukkit.command.Permission;

public class SMChat extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Key("chat-commands")
	public static boolean chatCommands = true;

	@Key("chat-unlocks.color")
	public static boolean chatColorUnlocks = true;

	@Key("sounds.send-chat")
	public static boolean sendSounds = true;

	@Key("sounds.receive-chat")
	public static boolean receiveSounds = true;

	@Key("sounds.tab-complete")
	public static boolean tabSounds = true;

	@Key("sounds.execute-command")
	public static boolean commandSounds = true;

	@Permission
	public static PermissionShade perm;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerChatTabCompleteEvent e)
	{
		if(tabSounds)
		{
			Styles.soundTabComplete(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(TabCompleteEvent e)
	{
		if(tabSounds && e.getSender() instanceof Player)
		{
			Styles.soundTabComplete((Player) e.getSender());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerCommandPreprocessEvent e)
	{
		if(commandSounds)
		{
			Styles.soundCommandSend(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e)
	{
		if(sendSounds)
		{
			Styles.soundChatSend(e.getPlayer());
		}

		if(receiveSounds)
		{
			for(Player i : e.getRecipients())
			{
				if(e.getPlayer().equals(i))
				{
					continue;
				}

				Styles.soundChatReceive(i);
			}
		}
	}

	public SMChat()
	{
		super("Chat");
	}

	@Override
	public void start()
	{
		l("Registering Chat Commands");
		registerCommand(new CommandChat());
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
