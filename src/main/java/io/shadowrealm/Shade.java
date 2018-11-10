package io.shadowrealm;

import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.pawn.Stop;
import com.volmit.volume.bukkit.service.IService;

@CommandTag("&8[&5&lSRL&r&8]&7: ")
public class Shade extends VolumePlugin
{
	public static Shade instance;

	@Start
	public void start()
	{
		instance = this;
	}

	@Stop
	public void stop()
	{

	}

	public static <T extends IService> void startService(Class<? extends T> t)
	{
		VolumePlugin.vpi.getService(t);
	}
}
