package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.ShadeClient;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
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
		if(ShadeClient.perm.chat.clear.has(sender))
		{
			sender.sendMessage("/chat clear" + C.DARK_PURPLE + " - Clear your own chat");
		}

		if(ShadeClient.perm.chat.clearEveryone.has(sender))
		{
			sender.sendMessage("/chat clearall" + C.DARK_PURPLE + " - Clear everyone's chat");
		}

		if(ShadeClient.perm.chat.broadcast.has(sender))
		{
			sender.sendMessage("/chat broadcast [msg]" + C.DARK_PURPLE + " - Show a broadcast");
		}

		if(ShadeClient.perm.chat.changeColor.has(sender))
		{
			sender.sendMessage("/chat color" + C.DARK_PURPLE + " - Change your chat color");
		}

		return true;
	}
}
