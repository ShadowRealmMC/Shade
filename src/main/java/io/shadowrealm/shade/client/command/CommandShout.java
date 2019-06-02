package io.shadowrealm.shade.client.command;

import java.util.Map;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.UnlockedItem;
import io.shadowrealm.shade.common.messages.RError;
import io.shadowrealm.shade.common.messages.ROK;
import io.shadowrealm.shade.common.messages.RShout;
import io.shadowrealm.shade.common.messages.RSpendUnlock;
import io.shadowrealm.shade.common.table.ShadowAccount;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandShout extends MortarCommand
{
	public CommandShout()
	{
		super("shout");
		requiresPermission(ShadeClient.perm.chat.shout);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		String m = "";

		for(String i : args)
		{
			m += i + " ";
		}

		ShadowAccount a = Shade.getAccount(sender.player());
		Map<String, UnlockedItem> items = a.getUnlocks();

		if(items.containsKey("shout"))
		{
			if(items.get("shout").getAmount() > 0)
			{
				if(args.length == 0)
				{
					sender.sendMessage("You have " + items.get("shout").getAmount() + " shout(s).");
					sender.sendMessage("Use /shout <text> to broadcast to every realm.");
				}

				else
				{
					new RShout().message(C.stripColor(F.color(m))).complete(Shade.connect(), (r) ->
					{
						if(r instanceof RError)
						{
							sender.sendMessage("Cannot shout at this time. Try again later.");
							sender.sendMessage("None of your shouts were spent.");
						}

						else
						{
							sender.sendMessage("Shout Scheduled");

							new RSpendUnlock().player(sender.player().getUniqueId()).unlock("shout").complete(Shade.connect(), (res) ->
							{
								if(res != null && res instanceof ROK)
								{
									UnlockedItem t = items.get("shout");
									t.setAmount(t.getAmount() - 1);

									if(t.getAmount() > 0)
									{
										items.put("shout", t);
									}

									else
									{
										items.remove("shout");
									}

									a.setUnlocks(items);
									sender.sendMessage("You have " + t.getAmount() + " shout(s) left.");
								}
							});
						}
					});
				}
			}

			else
			{
				sender.sendMessage("You do not have any shouts.");
			}
		}

		else
		{
			sender.sendMessage("You do not have any shouts.");
		}

		return true;
	}
}
