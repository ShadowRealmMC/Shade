package io.shadowrealm.test;

import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.pawn.IPawn;

public interface Test extends IPawn
{
	public void test(VolumeSender sender, String[] a);
}
