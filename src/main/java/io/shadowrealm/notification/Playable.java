package io.shadowrealm.notification;

import org.bukkit.entity.Player;

public interface Playable
{
	public void play(Player p);

	public int getTotalPlayTime();

	public int getMaximumSimultaneousMessages();
}
