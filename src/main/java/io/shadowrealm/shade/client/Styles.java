package io.shadowrealm.shade.client;

import org.bukkit.entity.Player;

import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.logic.format.F;
import mortar.util.text.C;

public class Styles
{
	public static void cleared(Player i, String who)
	{
		clearChatRaw(i, F.color("            &d\u2720 &d&l" + who + " &8&lcleared the chat &d\u2720            "));
	}

	public static void clearSelf(Player i)
	{
		clearChatRaw(i, F.color("            &d\u2720 &d&lYou &8&lcleared your own chat &d\u2720            "));
	}

	public static void superBorder(Player i, String message, C bright, C dark)
	{
		superHR(i, F.color(bright + "            \u2720 " + bright + "&l" + message + bright + " \u2720"), bright, dark, 6, 3);
	}

	private static void superHR(Player i, String v, C bright, C dark, int height, int offset)
	{
		for(int j = 0; j < height; j++)
		{
			if(j != height - offset)
			{
				String k = "";
				GList<C> cx = new GList<C>().qadd(bright).qadd(dark).qadd(C.DARK_GRAY);

				for(int l = 0; l < 75; l++)
				{
					k = k + (Math.random() < 0.11 ? cx.pickRandom() + (M.r(0.45) ? "\u25CF" : "\u2022") : " ");
				}

				i.sendMessage(k);
			}

			if(j == height - offset)
			{
				i.sendMessage(v);
			}
		}
	}

	private static void clearChatRaw(Player i, String mainline)
	{
		superHR(i, mainline, C.LIGHT_PURPLE, C.DARK_PURPLE, 120, 5);
	}
}
