package io.shadowrealm.notification;

import org.bukkit.entity.Player;

public class TitleNotification extends ChatNotification implements FadingNotification
{
	private int stayTime;
	private int fadeInTime;
	private int fadeOutTime;
	private String subtitle;

	public TitleNotification(String title, String subtitle)
	{
		super(title);
		setStayTime(12);
		setFadeInTime(7);
		setFadeOutTime(36);
		this.subtitle = subtitle;
	}

	@Override
	public void play(Player p)
	{
		p.sendTitle(getContent() == null ? "" : getContent(), subtitle == null ? "" : subtitle, getFadeInTime(), getStayTime(), getFadeOutTime());
	}

	@Override
	public int getTotalPlayTime()
	{
		return getTotalTime();
	}

	@Override
	public int getMaximumSimultaneousMessages()
	{
		return 1;
	}

	public String getSubtitle()
	{
		return subtitle;
	}

	public void setSubtitle(String subtitle)
	{
		this.subtitle = subtitle;
	}

	@Override
	public int getStayTime()
	{
		return stayTime;
	}

	@Override
	public void setStayTime(int stayTime)
	{
		this.stayTime = stayTime;
	}

	@Override
	public int getFadeInTime()
	{
		return fadeInTime;
	}

	@Override
	public void setFadeInTime(int fadeInTime)
	{
		this.fadeInTime = fadeInTime;
	}

	@Override
	public int getFadeOutTime()
	{
		return fadeOutTime;
	}

	@Override
	public void setFadeOutTime(int fadeOutTime)
	{
		this.fadeOutTime = fadeOutTime;
	}

	@Override
	public int getTotalTime()
	{
		return getStayTime() + getFadeInTime() + getFadeOutTime();
	}
}
