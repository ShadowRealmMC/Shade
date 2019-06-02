package io.shadowrealm.shade.module;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.client.permission.PermissionShade;
import io.shadowrealm.shade.module.api.ShadeModule;
import mortar.api.config.Key;
import mortar.api.nms.Catalyst;
import mortar.api.nms.FrameType;
import mortar.bukkit.command.Permission;
import mortar.compute.math.M;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.util.text.C;

public class SMNotifier extends ShadeModule
{
	private GMap<Player, Long> lastDurabilityWarning;

	@Key("enable")
	public static boolean enabled = true;

	@Key("notify-durability.low")
	public static boolean durabilityLow = true;

	@Key("notify-durability.broken")
	public static boolean durabilityBroken = true;

	@Permission
	public static PermissionShade perm;

	public SMNotifier()
	{
		super("Notifier");
	}

	@Override
	public void start()
	{
		lastDurabilityWarning = new GMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(PlayerItemBreakEvent e)
	{
		if(!durabilityBroken)
		{
			return;
		}

		Styles.soundBroken(e.getPlayer());
		Catalyst.host.sendAdvancement(e.getPlayer(), FrameType.TASK, e.getBrokenItem(), C.RED + "\nYour " + F.capitalizeWords(e.getBrokenItem().getType().name().toLowerCase().replaceAll("_", " ")) + " has broken!\n");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(PlayerItemDamageEvent e)
	{
		if(!durabilityLow)
		{
			return;
		}

		Styles.soundBreaking(e.getPlayer());

		if(e.getItem().getType().getMaxDurability() - e.getItem().getDurability() < 15)
		{
			if(lastDurabilityWarning.containsKey(e.getPlayer()) && lastDurabilityWarning.get(e.getPlayer()) < M.ms() - 10000)
			{
				lastDurabilityWarning.remove(e.getPlayer());
			}

			if(!lastDurabilityWarning.containsKey(e.getPlayer()))
			{
				lastDurabilityWarning.put(e.getPlayer(), M.ms());
				Catalyst.host.sendAdvancement(e.getPlayer(), FrameType.TASK, e.getItem(), C.RED + "\nYour " + F.capitalizeWords(e.getItem().getType().name().toLowerCase().replaceAll("_", " ")) + " is about to break!\n");
			}
		}
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}
