package io.shadowrealm.shade.map;

import com.volmit.phantom.plugin.SVC;

import io.shadowrealm.shade.services.LobbySVC;

public class PayloadThread extends Thread
{
	private Runnable r;

	public PayloadThread(Runnable r)
	{
		this.r = r;
		setPriority(MAX_PRIORITY);
		setName("Player Payload");
	}

	@Override
	public void run()
	{
		while(!SVC.get(LobbySVC.class).ready)
		{
			try
			{
				r.run();
			}

			catch(Throwable e)
			{

			}

			try
			{
				Thread.sleep(31);
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
