package io.shadowrealm.notification;

public interface Notification<T> extends Playable
{
	public T getContent();
}
