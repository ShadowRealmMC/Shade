package io.shadowrealm.shade.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import mortar.lang.collection.GList;

public class CommonProperties
{
	public static boolean DEBUG_CONNECTION = false;
	public static RestlessSide SIDE;
	public static GList<String> DOWNLOAD = new GList<String>();

	public static void downloadUpdates()
	{
		l("Preparing to download");
		ExecutorService svc = Executors.newCachedThreadPool();

		if(DOWNLOAD.isEmpty())
		{
			l("Nothing to download.");
			return;
		}

		OkHttpClient c = new OkHttpClient();
		c.setConnectTimeout(10, TimeUnit.SECONDS);
		c.setReadTimeout(10, TimeUnit.SECONDS);
		c.setWriteTimeout(10, TimeUnit.SECONDS);
		c.interceptors().add(new BasicAuthInterceptor("admin", "12311232"));

		for(String i : DOWNLOAD)
		{
			svc.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						URL u = new URL(i.split("=")[1]);
						File f = new File("plugins/update", i.split("=")[0] + ".jar");
						f.getParentFile().mkdirs();
						Response r = c.newCall(new Request.Builder().url(u).build()).execute();
						if(!r.isSuccessful())
						{
							throw new IOException("Failed to download file: " + r);
						}
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(r.body().bytes());
						fos.close();
					}

					catch(Throwable e)
					{

					}
				}
			});
		}

		svc.shutdown();
		try
		{
			svc.awaitTermination(5, TimeUnit.MINUTES);
		}

		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static void l(String f)
	{
		System.out.println("[Shadow Updater]: " + f);
	}
}
