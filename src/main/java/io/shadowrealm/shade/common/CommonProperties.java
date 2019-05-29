package io.shadowrealm.shade.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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
	public static boolean DOWNLOAD_UPDATES = true;

	public static void downloadUpdates()
	{
		if(!DOWNLOAD_UPDATES)
		{
			l("Not downloading updates");
			return;
		}

		l("Preparing Updates...");

		if(DOWNLOAD.isEmpty())
		{
			l("Nothing to update.");
			return;
		}

		l("Downloading " + DOWNLOAD.size() + " Update(s)");

		OkHttpClient cu = new OkHttpClient();
		cu.setConnectTimeout(10, TimeUnit.SECONDS);
		cu.setReadTimeout(10, TimeUnit.SECONDS);
		cu.setWriteTimeout(10, TimeUnit.SECONDS);

		OkHttpClient c = new OkHttpClient();
		c.setConnectTimeout(10, TimeUnit.SECONDS);
		c.setReadTimeout(10, TimeUnit.SECONDS);
		c.setWriteTimeout(10, TimeUnit.SECONDS);
		c.interceptors().add(new BasicAuthInterceptor("admin", "12311232"));

		for(String i : DOWNLOAD)
		{
			try
			{
				URL u = new URL(i.split("=")[1]);
				File f = new File("plugins" + (SIDE.equals(RestlessSide.CLIENT) ? "/update" : ""), i.split("=")[0] + ".jar");
				f.getParentFile().mkdirs();
				OkHttpClient x = i.split("=")[1].contains("shadowrealm") ? c : cu;
				Response r = x.newCall(new Request.Builder().url(u).build()).execute();
				if(!r.isSuccessful())
				{
					throw new IOException("Failed to download file: " + r);
				}
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(r.body().bytes());
				fos.close();
				l("Updated " + i.split("=")[0] + ".jar");
			}

			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		l("Updates Complete");
	}

	public static void l(String f)
	{
		System.out.println("[Shadow Updater]: " + f);
	}
}
