package io.shadowrealm.shade.client.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import io.shadowrealm.shade.client.Shade;
import io.shadowrealm.shade.common.Statistics;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.logic.format.F;

public class CommandStats extends MortarCommand
{
	public CommandStats()
	{
		super("stats");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		Statistics s = Shade.getStatistics(sender.player());
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) is.getItemMeta();
		bm.setTitle(sender.player().getName() + "'s Statistics");
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
