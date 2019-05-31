package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.util.text.C;
import mortar.util.text.ColoredString;
import mortar.util.text.RTEX;
import mortar.util.text.RTX;

public class CommandUnlockBrowse extends MortarCommand
{
	public CommandUnlockBrowse()
	{
		super("browse", "b");
		requiresPermission(ShadeClient.perm.unlocks);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		GMap<String, GList<ShadowUnlock>> c = new GMap<>();

		if(Shade.getUnlocks() == null)
		{
			sender.sendMessage("We don't have unlock data from the proxy yet... Be patient or panic. Take your pick.");
			return true;
		}

		for(ShadowUnlock i : Shade.getUnlocks())
		{
			if(!c.containsKey(i.getType()))
			{
				c.put(i.getType(), new GList<>());
			}

			c.get(i.getType()).add(i);
		}

		if(args.length == 0)
		{
			sender.sendMessage("There are " + Shade.getUnlocks().size() + " unlocks, across " + c.size() + " types.");

			for(String i : c.k())
			{
				sender.sendMessage(i + ": " + c.get(i).size());
			}

			return true;
		}

		GList<ShadowUnlock> u = c.get(args[0]);

		if(u == null)
		{
			sender.sendMessage("Cannot find category " + args[0] + ". Use /unlocks brose");
			return true;
		}

		if(u.isEmpty())
		{
			sender.sendMessage("The category " + args[0] + " has no unlocks within it.");
			return true;
		}

		sender.sendMessage("There are " + u.size() + " unlocks in the category " + args[0]);

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

		return true;
	}

}
