package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.compute.math.M;
import mortar.logic.format.F;

public class CommandReboot extends MortarCommand
{
	public CommandReboot()
	{
		super("reboot");
		requiresPermission(ShadeClient.perm.reboot);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(args.length == 0)
		{
			if(ShadeClient.rebootSchedule < 0)
			{
				sender.sendMessage("There is no scheduled reboot. Consult the config file Swift.");
			}

			else
			{
				sender.sendMessage("Reboot is scheduled for " + F.timeLong(Shade.getTimeUntilReboot(), 0) + " from now.");
			}

			sender.sendMessage("/reboot <seconds|now>");
		}

		if(args.length == 1)
		{
			if(args[0].toLowerCase().equals("now"))
			{
				Shade.scheduleNetworkReboot(0);
				ShadeClient.rebootSchedule = M.ms() + (10000);
				sender.sendMessage("Scheduled Reboot. Be Patient...");
			}

			else
			{
				try
				{
					long v = Long.valueOf(args[0]);
					Shade.scheduleNetworkReboot(v * 1000);
					ShadeClient.rebootSchedule = M.ms() + (v * 1000);
					sender.sendMessage("Reboot rescheduled for " + F.timeLong(ShadeClient.rebootSchedule - M.ms(), 0) + " from now.");
				}

				catch(Throwable e)
				{
					sender.sendMessage("Not a number.");
				}
			}
		}

		return true;
	}
}
