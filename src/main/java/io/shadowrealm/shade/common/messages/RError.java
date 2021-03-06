package io.shadowrealm.shade.common.messages;

import io.shadowrealm.shade.common.RestlessObject;

public class RError extends RestlessObject
{
	private String message;

	public RError()
	{

	}

	public RError(String message)
	{
		this.message = message;
	}

	@Override
	public RestlessObject handle()
	{
		System.out.println("Error: " + message);
		return null;
	}

	public String message()
	{
		return message;
	}

	public RError message(String message)
	{
		this.message = message;
		return this;
	}
}
