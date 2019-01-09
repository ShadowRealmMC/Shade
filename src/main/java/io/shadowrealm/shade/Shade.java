package io.shadowrealm.shade;

import com.volmit.phantom.plugin.Module;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.plugin.Scaffold.Command;
import com.volmit.phantom.plugin.Scaffold.Config;
import com.volmit.phantom.plugin.Scaffold.Instance;
import com.volmit.phantom.plugin.Scaffold.ModuleInfo;
import com.volmit.phantom.plugin.Scaffold.Permission;
import com.volmit.phantom.plugin.Scaffold.Start;
import com.volmit.phantom.plugin.Scaffold.Stop;
import com.volmit.phantom.text.C;

import io.shadowrealm.shade.command.CommandMap;
import io.shadowrealm.shade.command.CommandShade;
import io.shadowrealm.shade.permission.PermissionShade;
import io.shadowrealm.shade.services.LobbySVC;

@ModuleInfo(name = "Shade", version = "1.0", author = "cyberpwn", color = C.DARK_PURPLE)
public class Shade extends Module
{
	@Config("config")
	public static ShadowConfig config;

	@Permission
	public static PermissionShade perm;

	@Command
	public CommandShade shade;

	@Command("Map")
	public CommandMap map;

	@Instance
	public static Shade instance;

	@Start
	public void start()
	{
		if(config.COMPONENT_LOBBY_ENABLED)
		{
			l("Enabling Lobby Service");
			SVC.start(LobbySVC.class);
		}
	}

	@Stop
	public void stop()
	{
		if(config.COMPONENT_LOBBY_ENABLED)
		{
			SVC.get(LobbySVC.class).close();
		}
	}
}
