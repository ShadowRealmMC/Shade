package io.shadowrealm;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.bukkit.TICK;
import com.volmit.fulcrum.custom.ContentManager;
import com.volmit.fulcrum.event.ContentRecipeRegistryEvent;
import com.volmit.fulcrum.event.ContentRegistryEvent;
import com.volmit.volume.bukkit.U;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.pawn.Tick;
import com.volmit.volume.bukkit.service.IService;

import io.shadowrealm.custom.item.ItemSackOfHolding;
import io.shadowrealm.notification.AdvancementNotification;
import io.shadowrealm.service.NotificationSVC;

@CommandTag("&8[&5&lSRL&r&8]&7: ")
public class Shade extends VolumePlugin
{
	public static Shade instance;

	@Start
	public void start()
	{
		instance = this;
		kickFulcrum();
	}

	@Tick
	public void tick()
	{
		TICK.tick++;
	}

	@Stop
	public void stop()
	{

	}

	@EventHandler
	public void on(ContentRegistryEvent e)
	{
		e.register(new ItemSackOfHolding());
	}

	@EventHandler
	public void on(ContentRecipeRegistryEvent e)
	{

	}

	@EventHandler
	public void on(PlayerSwapHandItemsEvent e)
	{
		AdvancementNotification an = new AdvancementNotification("Shadow Progression\n+17SXP");
		an.setIs(new ItemStack(Material.GOLDEN_APPLE));
		U.getService(NotificationSVC.class).queue(an);
	}

	public static <T extends IService> void startService(Class<? extends T> t)
	{
		VolumePlugin.vpi.getService(t);
	}

	private void kickFulcrum()
	{
		try
		{
			ContentManager.cacheResources(this);
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		ContentManager.reloadContentManager();
	}
}
