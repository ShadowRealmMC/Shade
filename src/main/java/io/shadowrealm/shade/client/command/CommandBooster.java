package io.shadowrealm.shade.client.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.Styles;
import io.shadowrealm.shade.common.ServerEffect;
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
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.collection.GSet;
import mortar.logic.format.F;
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
		GSet<String> s = new GSet<String>();

		for(ServerEffect i : Shade.getActiveBoosters())
		{
			sender.sendMessage(C.WHITE + Shade.getUnlock(i.getId()).getName() + C.GRAY + " lasts for another " + F.timeLong(i.getEndsAt() - M.ms(), 0));
			s.addAll(i.getContributors());
		}

		if(!s.isEmpty())
		{
			sender.sendMessage(C.WHITE + "Contributors: ");
		}

		sender.sendMessage(new GList<String>(s).toString(", "));

		GList<UnlockedItem> t = Shade.getUnlocksForType(sender.player(), "booster");

		if(t.isEmpty())
		{
			sender.sendMessage("You do not have any boosters!");
		}

		else
		{
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

			w.open();
		}

		return true;
	}

	private void boost(Player player, String id)
	{
		player.closeInventory();

		Shade.syncronizeAccount(player);
		J.a(() ->
		{
			if(Shade.activateBooster(player, Shade.getUnlock(id)))
			{
				Styles.chatBroadcast(player, C.GRAY + "Activated Booster!");

				J.a(() -> Shade.syncronizeAccount(player), 10);
			}

			else
			{
				Shade.unlock(new MortarSender(Bukkit.getConsoleSender()), player.getName(), player.getUniqueId(), new UnlockedItem(id, 1));
				Styles.chatBroadcast(player, C.GRAY + "Failed to activate booster for some reason... Refunded booster.");
			}
		});
	}
}
