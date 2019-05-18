package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.ShadowPlayerController;
import io.shadowrealm.shade.common.table.ShadowAccount;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;

public class CommandSR extends MortarCommand
{
	@Command
	private CommandSRGet get;

	@Command
	private CommandSRGive give;

	@Command
	private CommandSRDamage damage;

	public CommandSR()
	{
		super("shadowrank", "sr", "sxp");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(sender.isPlayer())
		{
			ShadowPlayerController c = (ShadowPlayerController) ShadeClient.instance.getController(ShadowPlayerController.class);
			ShadowAccount a = c.getAccount(sender.player());
			sender.sendMessage("Rank: " + c.computeRank(a).getFullName() + " (" + F.f(a.getShadowXPLastEarned()) + ")");
			sender.sendMessage("Total SXP: " + F.f(a.getShadowXP()));
			sender.sendMessage("  Today: " + F.f(a.getShadowXPEarned()));
			sender.sendMessage("  Yesterday: " + F.f(a.getShadowXPLastEarned()));
			sender.sendMessage("----------------------------");
			sender.sendMessage("/sxp <player> <amount>");
			sender.sendMessage("/sxp give <player> <amount>");
			sender.sendMessage("/sxp damage <player> <amount>");
		}

		else
		{
			sender.sendMessage("/sxp get <player>");
			sender.sendMessage("/sxp give <player> <amount>");
			sender.sendMessage("/sxp damage <player> <amount>");
		}

		return true;
	}

}
