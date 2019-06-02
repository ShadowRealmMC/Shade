package io.shadowrealm.shade.client;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import mortar.api.sound.Audio;
import mortar.api.sound.MFADistortion;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.logic.format.F;
import mortar.util.text.C;

public class Styles
{
	private static final String[] RGB = new String[] {C.RED.toString(), C.GREEN.toString(), C.BLUE.toString(), C.AQUA.toString(), C.YELLOW.toString(), C.LIGHT_PURPLE.toString(), C.GOLD.toString()};

	public static String rgbify(String s)
	{
		s = C.stripColor(s);
		StringBuilder buffer = new StringBuilder(s.length());
		char[] charbuf = s.toCharArray();
		int offset = M.rand(0, RGB.length);

		for(int i = 0; i < charbuf.length; i++)
		{
			buffer.append(RGB[(i + offset) % RGB.length] + charbuf[i]);
		}

		return buffer.toString();
	}

	public static void soundBroken(Player player)
	{
		new Audio().setSound(Sound.ITEM_SHIELD_BREAK).vp(1f, 1.3f).play(player);
		new Audio().setSound(Sound.BLOCK_IRON_DOOR_CLOSE).vp(1f, 0.7f).play(player);
	}

	public static void soundBreaking(Player player)
	{
		new Audio().setSound(Sound.BLOCK_IRON_DOOR_CLOSE).vp(0.67f, 0.1f).play(player);
	}

	public static void soundAlert(Player i)
	{
		//@builder
		new Audio()
		.addChild(new Audio().s(Sound.ENTITY_SHULKER_BULLET_HIT).vp(0.3f, 1.31f))
		.addChild(new Audio().s(Sound.ITEM_TOTEM_USE).vp(0.1f, 0.01f))
		.addChild(new MFADistortion(2, 1.9f)
				.distort(new Audio().s(Sound.ENTITY_ITEMFRAME_ROTATE_ITEM).vp(1f, 0.85f)))
		.play(i);
		//@done
	}

	public static void soundShout(Player i)
	{
		//@builder
		new Audio()
		.addChild(new Audio().s(Sound.ENTITY_SHULKER_BULLET_HIT).vp(0.3f, 1.31f))
		.addChild(new MFADistortion(2, 1.9f)
				.distort(new Audio().s(Sound.ENTITY_ITEMFRAME_ROTATE_ITEM).vp(1f, 0.85f)))
		.play(i);
		//@done
	}

	public static void soundTabComplete(Player i)
	{
		new Audio().s(Sound.ENTITY_ITEMFRAME_ROTATE_ITEM).vp(1.25f, 1.2f).play(i);
	}

	public static void soundChatSend(Player i)
	{
		new Audio().s(Sound.ENTITY_ITEMFRAME_BREAK).vp(1.25f, 1.4f).play(i);
	}

	public static void soundCommandSend(Player i)
	{
		new Audio().s(Sound.ENTITY_ITEMFRAME_PLACE).vp(1.25f, 1.2f).play(i);
	}

	public static void soundChatReceive(Player i)
	{
		new Audio().s(Sound.ENTITY_CHICKEN_STEP).vp(0.5f, 0.2f).play(i);
	}

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
		superHR(i, F.color(bright + "            \u2720 " + bright + "&l" + message + bright + " \u2720"), bright, dark, 5, 3);
	}

	public static void superBorderShout(Player i, String message)
	{
		soundShout(i);
		superHRSilent(i, F.color(C.GREEN + "            \u2720 " + C.GREEN + "&l" + message + C.GREEN + " \u2720"), C.GREEN, C.DARK_GREEN, 3, 2);
	}

	private static void superHR(Player i, String v, int height, int offset)
	{
		soundAlert(i);
		for(int j = 0; j < height; j++)
		{
			if(j != height - offset)
			{
				String k = "";

				for(int l = 0; l < 75; l++)
				{
					k = k + (Math.random() < 0.11 ? C.WHITE + (M.r(0.45) ? "\u25CF" : "\u2022") : " ");
				}

				i.sendMessage(rgbify(k));
			}

			if(j == height - offset)
			{
				i.sendMessage(rgbify(v));
			}
		}
	}

	private static void superHR(Player i, String v, C bright, C dark, int height, int offset)
	{
		soundAlert(i);
		superHRSilent(i, v, bright, dark, height, offset);
	}

	private static void superHRSilent(Player i, String v, C bright, C dark, int height, int offset)
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
		superHR(i, mainline, C.YELLOW, C.GOLD, 120, 5);
	}

	public static void superBorderRGB(Player i, String message)
	{
		superHR(i, rgbify("            \u2720" + message + " \u2720"), 6, 3);
	}
}
