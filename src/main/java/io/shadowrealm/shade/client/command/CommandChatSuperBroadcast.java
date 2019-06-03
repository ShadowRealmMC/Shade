package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.messages.RBroadcast;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandChatSuperBroadcast extends MortarCommand
{
	public CommandChatSuperBroadcast()
	{
		super("broadcast");
		requiresPermission(ShadeClient.perm.chat.broadcast);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		String m = "";

		for(String i : args)
		{
			m += i + " ";
		}

		C bright = C.LIGHT_PURPLE;
		C dark = C.DARK_PURPLE;
		new RBroadcast().message(F.color(m)).colorBright(bright).colorDark(dark).completeBlind(Shade.connect());

		return true;
	}
}
