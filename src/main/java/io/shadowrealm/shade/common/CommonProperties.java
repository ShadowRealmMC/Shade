package io.shadowrealm.shade.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import mortar.lang.collection.GList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

		OkHttpClient cu = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).build();
		OkHttpClient c = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new BasicAuthInterceptor("admin", "12311232")).build();

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
