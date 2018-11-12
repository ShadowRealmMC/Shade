package io.shadowrealm.custom.item;

import org.bukkit.Sound;

import com.volmit.fulcrum.custom.CustomItem;
import com.volmit.fulcrum.sfx.Audio;

public class ItemSackOfHolding extends CustomItem
{
	public ItemSackOfHolding()
	{
		super("sack");
		setName("Sack of Holding");
		setStackSize(1);
		setPickupSound(new Audio().s(Sound.ITEM_ARMOR_EQUIP_LEATHER));
	}

}
