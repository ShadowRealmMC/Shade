package io.shadowrealm.shade.client.command;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.client.ShadeClient;
import io.shadowrealm.shade.common.Statistics;
import mortar.api.world.P;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.logic.format.F;

public class CommandStats extends MortarCommand
{
	public CommandStats()
	{
		super("stats");
		requiresPermission(ShadeClient.perm.stats);
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(args.length == 0)
		{
			sender.sendMessage("/stats <player>");
			return true;
		}

		String name = args[0];
		UUID id = null;

		for(Player i : P.onlinePlayers())
		{
			if(i.getName().equals(name))
			{
				id = i.getUniqueId();
				break;
			}
		}

		id = id == null ? Shade.getUUID(name) : id;

		if(id == null)
		{
			sender.sendMessage("Cant find " + name);
			return true;
		}

		Statistics s = Shade.getStatistics(id);
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) is.getItemMeta();
		bm.setTitle(name + "'s Statistics");
		GList<String> pages = new GList<String>();
		bm.setAuthor("ShadowRealm");
		int m = 0;
		StringBuilder buffer = new StringBuilder();

		for(String i : s.k())
		{
			buffer.append(ChatColor.BLACK + "" + ChatColor.BOLD + i + ": \n" + ChatColor.DARK_GRAY + F.f(s.get(i).longValue()));

			if(m < 3)
			{
				buffer.append("\n\n");
				m++;
			}

			else
			{
				pages.add(buffer.toString());
				buffer = new StringBuilder();
				m = 0;
			}
		}

		if(m > 0)
		{
			pages.add(buffer.toString());
		}

		bm.setGeneration(Generation.TATTERED);
		bm.setPages(pages);
		is.setItemMeta(bm);
		sender.player().getInventory().addItem(is);

		return true;
	}
}
