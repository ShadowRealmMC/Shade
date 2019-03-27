package io.shadowrealm.shade;

import mortar.bukkit.plugin.Instance;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.util.text.C;
import mortar.util.text.TXT;

public class Shade extends MortarPlugin
{
	@Instance
	public static Shade instance;

	@Override
	public void start()
	{

	}

	@Override
	public void stop()
	{

	}

	@Override
	public String getTag(String subTag)
	{
		return TXT.makeTag(C.DARK_GRAY, C.LIGHT_PURPLE, C.GRAY, "Shade");
	}
}
