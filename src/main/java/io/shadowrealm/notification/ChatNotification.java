package io.shadowrealm.notification;

import org.bukkit.entity.Player;

public class ChatNotification extends BaseNotification<String>
{
	public ChatNotification(String t)
	{
		super(t);
	}

	@Override
	public void play(Player p)
	{
		p.sendMessage(getContent());
	}

	@Override
	public int getTotalPlayTime()
	{
		return (int) (20D * ((double) getContent().length() / 8D));
	}

	@Override
	public int getMaximumSimultaneousMessages()
	{
		return 4;
	}
}
