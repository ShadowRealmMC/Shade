package io.shadowrealm.shade.client.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.inventory.UIElement;
import mortar.api.inventory.UIPaneDecorator;
import mortar.api.inventory.UIWindow;
import mortar.api.inventory.Window;
import mortar.api.inventory.WindowResolution;
import mortar.api.sched.J;
import mortar.api.world.MaterialBlock;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.util.text.C;

public class CommandBooster extends MortarCommand
{
	public CommandBooster()
	{
		super("boost", "booster", "boosters");
		requiresPermission(ShadeClient.perm.booster);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		GList<UnlockedItem> t = new GList<>();

		for(ShadowUnlock i : Shade.getUnlocksForType("booster"))
		{
			if(Shade.hasUnlock(sender.player(), i.getId()))
			{
				t.add(Shade.getUnlock(sender.player(), i.getId()));
			}
		}

		//@builder
		Window w = new UIWindow(sender.player())
				.setTitle("Boosters")
				.setResolution(WindowResolution.W3_H3)
				.setDecorator(new UIPaneDecorator(C.DARK_GRAY));
		//@done
		int m = 0;
		for(UnlockedItem i : t)
		{
			ShadowUnlock r = Shade.getUnlock(i.getId());
			//@builder
			w.setElement(w.getPosition(m), w.getRow(m), new UIElement("ulb-" + i.getId())
					.setCount(i.getAmount())
					.setMaterial(new MaterialBlock(Material.NETHER_STAR))
					.setName(C.LIGHT_PURPLE + r.getFormattedName())
					.addLore(C.GRAY + r.getFormattedDescription())
					.addLore(C.GREEN + "Left Click to boost ShadowRealms!")
					.onLeftClick((e) -> boost(sender.player(), i.getId())));
			//@done
			m++;
		}

		if(t.isEmpty())
		{
			w.close();
			sender.sendMessage("You do not have any boosters!");
		}

		else
		{
			w.open();
		}

		return true;
	}

	private void boost(Player player, String id)
	{
		player.closeInventory();

		J.a(() ->
		{
			if(Shade.activateBooster(player, Shade.getUnlock(id)))
			{
				Styles.chatBroadcast(player, C.GRAY + "Activated Booster!");
			}

			else
			{
				Styles.chatBroadcast(player, C.GRAY + "Failed to activate booster for some reason...");
			}
		});
	}
}
