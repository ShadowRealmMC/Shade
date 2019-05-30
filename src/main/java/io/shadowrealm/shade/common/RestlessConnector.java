package io.shadowrealm.shade.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mortar.lang.json.JSONException;
import mortar.lang.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestlessConnector
{
	private ExecutorService service;
	public static RestlessConnector instance;
	private String address;
	private int port;
	private OkHttpClient c;
	public static int m = 1;
	private String who;

	public RestlessConnector(String address, int port, String who)
	{
		instance = this;
		this.address = address;
		this.port = port;

		c = new OkHttpClient().newBuilder().retryOnConnectionFailure(true).writeTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).connectTimeout(5, TimeUnit.SECONDS).build();
		service = Executors.newWorkStealingPool(4);
		this.who = who;
	}

	public void queue(Runnable r)
	{
		service.submit(r);
	}

	public static String encode(String data)
	{
		return Base64.getUrlEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
	}

	public static String decode(String u)
	{
		return new String(Base64.getUrlDecoder().decode(u), StandardCharsets.UTF_8);
	}

	public String who()
	{
		return who;
	}

	public JSONObject request(JSONObject request) throws JSONException, IOException
	{
		try
		{
			Response r = c.newCall(new Request.Builder().header("Accept-Encoding", "identity").header("Connection", "close").url("http://" + address + ":" + port + "/wire?j=" + encode(request.toString(0))).build()).execute();
			String x = r.body().string();
			r.close();

			if(x.isEmpty())
			{
				return null;
			}

			try
			{
				return new JSONObject(x);
			}

			catch(Throwable e)
			{
				return null;
			}
		}

		catch(Throwable e)
		{

		}

		return null;
	}
}
