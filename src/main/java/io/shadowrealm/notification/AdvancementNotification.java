package io.shadowrealm.notification;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.volmit.fulcrum.Fulcrum;

public class AdvancementNotification extends BaseNotification<String>
{
	private ItemStack is;
	private boolean significant;

	public AdvancementNotification(String t)
	{
		super(t);
		significant = false;
		is = new ItemStack(Material.BLAZE_POWDER);
	}

	public ItemStack getIs()
	{
		return is;
	}

	public void setIs(ItemStack is)
	{
		this.is = is;
	}

	public boolean isSignificant()
	{
		return significant;
	}

	public void setSignificant(boolean significant)
	{
		this.significant = significant;
	}

	@Override
	public void play(Player p)
	{
		if(significant)
		{
			Fulcrum.adapter.sendAdvancementIntense(p, is, getContent());
		}

		else
		{
			Fulcrum.adapter.sendAdvancementSubtle(p, is, getContent());
		}
	}

	@Override
	public int getTotalPlayTime()
	{
		return 100;
	}

	@Override
	public int getMaximumSimultaneousMessages()
	{
		return 5;
	}
}
