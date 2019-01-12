package io.shadowrealm.shade.command;

import com.volmit.phantom.api.command.PhantomCommand;
import com.volmit.phantom.api.command.PhantomSender;

import io.shadowrealm.shade.Shade;

public class CommandReload extends PhantomCommand
{
	public CommandReload()
	{
		super("reload", "rld");
		requiresPermission(Shade.perm.reload);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		sender.sendMessage("Reloaded... JK");
		return true;
	}
}
