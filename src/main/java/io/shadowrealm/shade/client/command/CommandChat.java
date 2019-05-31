package io.shadowrealm.shade.client.command;

import io.shadowrealm.shade.client.ShadeClient;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.util.text.C;

public class CommandChat extends MortarCommand
{
	@Command
	private CommandChatClear clear;

	@Command
	private CommandChatClearAll clearall;

	@Command
	private CommandChatSuperBroadcast broadcast;

	@Command
	private CommandChatChangeColor changeColor;

	public CommandChat()
	{
		super("chat");
		requiresPermission(ShadeClient.perm.chat);
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
			sender.sendMessage("/chat broadcast [msg]" + C.DARK_PURPLE + " - Show a supersized broadcast");
		}

		if(ShadeClient.perm.chat.changeColor.has(sender))
		{
			sender.sendMessage("/chat color" + C.DARK_PURPLE + " - Change your chat color");
		}

		return true;
	}
}
