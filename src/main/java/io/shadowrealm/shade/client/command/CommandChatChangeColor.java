package io.shadowrealm.shade.client.command;

import org.bukkit.Material;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.client.TextFilter;
import io.shadowrealm.shade.common.messages.RAccount;
import io.shadowrealm.shade.common.messages.RGetAccount;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import io.shadowrealm.shade.module.SMChat;
import mortar.api.inventory.UIElement;
import mortar.api.inventory.UIWindow;
import mortar.api.inventory.Window;
import mortar.api.inventory.WindowResolution;
import mortar.api.world.MaterialBlock;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GSet;
import mortar.lang.json.JSONObject;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandChatChangeColor extends MortarCommand
{
	public CommandChatChangeColor()
	{
		super("color", "colors");
		requiresPermission(ShadeClient.perm.chat.clear);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		C dc = C.valueOf(SMChat.defaultChatColor);

		//@builder
		new RGetAccount().player(sender.player().getUniqueId()).complete(Shade.connect(), (r) -> {
			if(r instanceof RAccount)
			{
				Window w = new UIWindow(sender.player())
						.setResolution(WindowResolution.W9_H6)
						.setViewportHeight(2)
						.setTitle("Choose a Color");
				ShadowAccount a = ((RAccount)r).shadowAccount();
				GSet<String> colors = new GSet<>();
				colors.add(dc.name());

				for(String i : a.getUnlocks().keySet())
				{
					ShadowUnlock u = Shade.getUnlock(i);

					if(u == null)
					{
						continue;
					}

					if(u.getType().equals("chat"))
					{
						if(u.getId().startsWith("color_"))
						{
							colors.add(u.getId().replaceAll("color_", "").toUpperCase());
						}
					}
				}

				int m = 0;

				JSONObject settings = Shade.getSettings(sender.player());
				C mi = dc;

				try
				{
					mi = C.valueOf(settings.getString("chat-color"));
				}

				catch(Throwable ex)
				{

				}

				for(String i : colors)
				{
					boolean equipped = i.equals(mi.name());

					C c = C.BLACK;

					try
					{
						c = C.valueOf(i);
					}

					catch(Throwable e)
					{
						try
						{
							c = C.valueOf(TextFilter.valueOf(i).getPrefixes()[0]);
						}

						catch(Throwable ex)
						{

						}
					}

					w.setElement(w.getPosition(m), w.getRow(m++), new UIElement("color-" + i.toLowerCase())
							.setMaterial(new MaterialBlock(Material.STAINED_GLASS_PANE, (byte) c.dye().getWoolData()))
							.setEnchanted(equipped)
							.setName(i + F.capitalizeWords(i.toLowerCase().replaceAll("_", " ")) + " Chat Color")
							.addLore(C.GRAY + (equipped ? "Currently Equipped" : "Left click to use this color."))
							.onLeftClick((ele) -> Shade.changeChatColor(i, sender.player())));
				}

				w.open();
			}
		});
		//@done
		return true;
	}
}
