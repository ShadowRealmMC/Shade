package io.shadowrealm.notification;

public interface FadingNotification
{
	public int getStayTime();

	public int getFadeOutTime();

	public int getFadeInTime();

	public void setStayTime(int time);

	public void setFadeOutTime(int time);

	public void setFadeInTime(int time);

	public int getTotalTime();
}
