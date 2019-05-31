package io.shadowrealm.shade.client.command;

import java.util.Map;
import java.util.UUID;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.messages.RAccount;
import io.shadowrealm.shade.common.messages.RGetAccount;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.MortarAPIPlugin;
import mortar.lang.collection.GList;
import mortar.lib.control.MojangProfileController;
import mortar.util.text.C;
import mortar.util.text.ColoredString;
import mortar.util.text.RTEX;
import mortar.util.text.RTX;

public class CommandUnlockList extends MortarCommand
{
	public CommandUnlockList()
	{
		super("list", "l");
		requiresPermission(ShadeClient.perm.unlocks);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(Shade.getUnlocks() == null)
		{
			sender.sendMessage("We don't have unlock data from the proxy yet... Be patient or panic. Take your pick.");
			return true;
		}

		if(args.length != 1)
		{
			sender.sendMessage("/unlock list <player>");

			return true;
		}

		UUID player = ((MojangProfileController) MortarAPIPlugin.p.getController(MojangProfileController.class)).getOnlineUUID(args[0]);

		if(player == null)
		{
			sender.sendMessage("Cannot find player " + args[0]);
			return true;
		}

		new RGetAccount().player(player).complete(Shade.connect(), (r) ->
		{
			try
			{
				if(r instanceof RAccount)
				{
					RAccount act = (RAccount) r;
					Map<String, UnlockedItem> m = act.shadowAccount().getUnlocks();
					GList<ShadowUnlock> u = new GList<>();
					int unidentified = 0;

					for(String i : m.keySet())
					{
						ShadowUnlock ux = Shade.getUnlock(i);

						if(ux == null)
						{
							unidentified++;
							continue;
						}

						u.add(ux);
					}

					sender.sendMessage(args[0] + " has " + u.size() + " unlocks.");
					for(ShadowUnlock i : u)
					{
						RTX rt = new RTX();
						RTEX rte = new RTEX();
						rte.getExtras().add(new ColoredString(C.getLastColors(i.getFormattedName()).isEmpty() ? C.GRAY : C.getByChar(C.getLastColors(i.getFormattedName()).substring(1).charAt(0)), C.stripColor(i.getFormattedName()) + "\n"));
						rte.getExtras().add(new ColoredString(C.GRAY, C.stripColor(i.getFormattedDescription()) + "\n\n"));

						if(i.isSingleton())
						{
							rte.getExtras().add(new ColoredString(C.WHITE, "Can only have one copy of this unlock.\n"));
						}

						else
						{
							rte.getExtras().add(new ColoredString(C.WHITE, "Can have multiples of this unlock.\n"));
							rte.getExtras().add(new ColoredString(C.WHITE, "Current Holdings: " + m.get(i.getId()).getAmount() + "\n"));
						}

						if(i.isConsumable())
						{
							rte.getExtras().add(new ColoredString(C.WHITE, "Consumable Unlock.\n\n"));
						}

						else
						{
							rte.getExtras().add(new ColoredString(C.WHITE, "Not Consumable.\n\n"));
						}

						rte.getExtras().add(new ColoredString(C.AQUA, "ID: "));
						rte.getExtras().add(new ColoredString(C.WHITE, i.getType() + ":" + i.getId()));
						rt.addText("- ", C.GRAY);
						rt.addTextHover(C.stripColor(i.getFormattedName()), rte, C.getLastColors(i.getFormattedName()).isEmpty() ? C.GRAY : C.getByChar(C.getLastColors(i.getFormattedName()).substring(1).charAt(0)));
						rt.tellRawTo(sender.player());
					}

					if(unidentified > 0)
					{
						sender.sendMessage("Couldnt identify " + unidentified + " unlocks on account.");
					}
				}

				else
				{
					sender.sendMessage("Cannot find shadow account");
				}
			}

			catch(Throwable e)
			{
				e.printStackTrace();
				sender.sendMessage("Failed to read data!");
			}
		});

		return true;
	}

}
