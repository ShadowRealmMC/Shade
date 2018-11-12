package io.shadowrealm.notification;

public abstract class BaseNotification<T> implements Notification<T>
{
	private final T t;

	public BaseNotification(T t)
	{
		this.t = t;
	}

	@Override
	public T getContent()
	{
		return t;
	}
}
