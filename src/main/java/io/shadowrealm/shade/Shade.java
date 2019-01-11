package io.shadowrealm.shade;

import com.volmit.phantom.api.module.Color;
import com.volmit.phantom.api.module.Command;
import com.volmit.phantom.api.module.Config;
import com.volmit.phantom.api.module.Instance;
import com.volmit.phantom.api.module.Module;
import com.volmit.phantom.api.module.Permission;
import com.volmit.phantom.api.module.Start;
import com.volmit.phantom.api.module.Stop;
import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.util.text.C;

import io.shadowrealm.shade.command.CommandMap;
import io.shadowrealm.shade.command.CommandShade;
import io.shadowrealm.shade.permission.PermissionShade;
import io.shadowrealm.shade.services.LobbySVC;

@Color(C.LIGHT_PURPLE)
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
		if(config.COMPONENT__LOBBY__ENABLED)
		{
			l("Enabling Lobby Service");
			SVC.start(LobbySVC.class);
		}
	}

	@Stop
	public void stop()
	{
		if(config.COMPONENT__LOBBY__ENABLED)
		{
			SVC.get(LobbySVC.class).close();
		}
	}
}
