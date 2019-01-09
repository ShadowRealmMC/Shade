package io.shadowrealm.shade.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.plugin.Scaffold.Command;

import io.shadowrealm.shade.Shade;

public class CommandShade extends PhantomCommand
{
	@Command
	public CommandReload reload;

	public CommandShade()
	{
		super("shade", "sh");
		requiresPermission(Shade.perm);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		sender.sendMessage("Hello");
		return true;
	}
}
