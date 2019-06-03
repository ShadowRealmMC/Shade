package io.shadowrealm.shade.module;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.client.TextFilter;
import io.shadowrealm.shade.client.command.CommandChat;
import io.shadowrealm.shade.client.command.CommandShout;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.sched.J;
import mortar.api.world.P;
import mortar.compute.math.RollingAverage;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONObject;
import mortar.util.text.C;

public class SMChat extends ShadeModule
{
	@Key("enable")
	public static boolean enabled = true;

	@Key("chat-commands")
	public static boolean chatCommands = true;

	@Key("chat-unlocks.color")
	public static boolean chatColorUnlocks = true;

	@Key("chat-unlocks.default-color")
	public static String defaultChatColor = "GRAY";

	@Key("sounds.send-chat")
	public static boolean sendSounds = true;

	@Key("sounds.receive-chat")
	public static boolean receiveSounds = true;

	@Key("sounds.tab-complete")
	public static boolean tabSounds = true;

	@Key("sounds.execute-command")
	public static boolean commandSounds = true;

	@Key("delays.use-chat-delays")
	public static boolean useChatDelays = true;

	@Key("delays.max-messages-interval")
	public static int delayInterval = 20;

	@Key("delays.max-messages-interval")
	public static int delayMemory = 5;

	@Key("delays.max-messages-interval")
	public static double delayLimit = 1.25;

	private GMap<Player, RollingAverage> averageChatFrequency;
	private GMap<Player, Double> chatFrequency;

	public SMChat()
	{
		super("Chat");
	}

	@Override
	public void start()
	{
		l("Registering Chat Commands");
		registerCommand(new CommandChat());
		registerCommand(new CommandShout());
		averageChatFrequency = new GMap<>();
		chatFrequency = new GMap<>();
		J.ar(() -> updateChatDelays(), delayInterval);
	}

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

	public void chatted(Player p)
	{
		if(!chatFrequency.containsKey(p))
		{
			chatFrequency.put(p, 0D);
		}

		chatFrequency.put(p, chatFrequency.get(p) + 1D);
	}

	public double getFrequency(Player p)
	{
		if(chatFrequency.containsKey(p))
		{
			return chatFrequency.get(p);
		}

		return 0;
	}

	public double getAverage(Player p)
	{
		if(averageChatFrequency.containsKey(p))
		{
			return averageChatFrequency.get(p).get();
		}

		return 0;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e)
	{
		if(useChatDelays && !ShadeClient.perm.chat.bypass.has(e.getPlayer()))
		{
			if(getAverage(e.getPlayer()) > delayLimit || getFrequency(e.getPlayer()) > delayLimit + (delayLimit * 0.5))
			{
				Styles.superBorder(e.getPlayer(), "Slow Down Please.", C.RED, C.DARK_RED);
				e.setCancelled(true);
				return;
			}

			chatted(e.getPlayer());
		}

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

		if(chatColorUnlocks)
		{
			JSONObject settings = Shade.getSettings(e.getPlayer());
			C i = C.valueOf(defaultChatColor);
			TextFilter f = null;

			try
			{
				i = C.valueOf(settings.getString("chat-color"));
			}

			catch(Throwable ex)
			{
				try
				{
					f = TextFilter.valueOf(settings.getString("chat-color"));
				}

				catch(Throwable exx)
				{

				}
			}

			if(f != null)
			{
				e.setMessage(Styles.filter(e.getMessage(), f));
			}

			else
			{
				e.setMessage(i + e.getMessage());
			}
		}
	}

	private void updateChatDelays()
	{
		for(Player i : P.onlinePlayers())
		{
			if(chatFrequency.containsKey(i))
			{
				if(!averageChatFrequency.containsKey(i))
				{
					averageChatFrequency.put(i, new RollingAverage(delayMemory));
					for(int j = 0; j < delayMemory; j++)
					{
						averageChatFrequency.get(i).put(1);
					}
				}

				averageChatFrequency.get(i).put(getFrequency(i) * 2.5);
				chatFrequency.remove(i);
			}

			else if(averageChatFrequency.containsKey(i))
			{
				averageChatFrequency.get(i).put(0);
			}
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
