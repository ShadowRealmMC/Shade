package io.shadowrealm.shade.client.command;

import java.util.UUID;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.MortarAPIPlugin;
import mortar.lib.control.MojangProfileController;

public class CommandUnlock extends MortarCommand
{
	@Command
	private CommandUnlockBrowse browse;

	@Command
	private CommandUnlockList list;

	public CommandUnlock()
	{
		super("unlock", "unlocks");
		requiresPermission(ShadeClient.perm.unlocks);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(args.length == 0)
		{
			sender.sendMessage("/unlocks browse [category]");
			sender.sendMessage("/unlocks list <player>");
			sender.sendMessage("/unlock <category>:<id|*> <player>");
		}

		if(args.length < 2)
		{
			sender.sendMessage("/unlock <category>:<id|*> <player>");
			return true;
		}

		String cid = args[0].toLowerCase();
		ShadowUnlock u = Shade.getUnlock(cid);
		int a = 1;

		if(u == null)
		{
			sender.sendMessage("Cannot find " + cid);
			return true;
		}

		UUID player = ((MojangProfileController) MortarAPIPlugin.p.getController(MojangProfileController.class)).getOnlineUUID(args[1]);

		if(player == null)
		{
			sender.sendMessage("Cannot find player " + args[1]);
			return true;
		}

		if(args.length >= 3)
		{
			try
			{
				a = Integer.valueOf(args[2]);
			}

			catch(Throwable e)
			{
				sender.sendMessage("Not a number: " + args[2]);
				return true;
			}
		}

		Shade.unlock(sender, args[1], player, new UnlockedItem(u.getId(), a));

		return true;
	}

}
