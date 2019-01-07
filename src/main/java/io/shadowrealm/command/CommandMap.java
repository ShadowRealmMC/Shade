package io.shadowrealm.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.plugin.Scaffold.Command;

import io.shadowrealm.Shade;

public class CommandMap extends PhantomCommand
{
	@Command
	public CommandMapCompile compile;

	@Command
	public CommandMapTest test;

	@Command
	public CommandMapExport build;

	public CommandMap()
	{
		super("map");
		requiresPermission(Shade.perm.map);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		sender.sendMessage("/map compile - Build Data & Check Errors");
		sender.sendMessage("/map test - Test the map");
		sender.sendMessage("/map export - Export map to a shaded schematic.");
		return true;
	}
}
