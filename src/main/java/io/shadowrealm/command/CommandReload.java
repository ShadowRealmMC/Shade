package io.shadowrealm.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;

import io.shadowrealm.Shade;

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
