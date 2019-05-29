package io.shadowrealm.shade.client;

import java.util.List;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.messages.RGetServers;
import io.shadowrealm.shade.common.messages.RSendServers;
import mortar.api.sched.J;
import mortar.bukkit.plugin.Controller;
import mortar.compute.math.M;
import mortar.util.text.D;

public class ShadowServerController extends Controller
{
	@Override
	public void start()
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> start(), 2);
			return;
		}

		J.ar(() ->
		{
			boolean failed = false;

			try
			{
				RSendServers r = (RSendServers) new RGetServers().complete(ShadeClient.instance.getConnector());

				if(r == null)
				{
					failed = true;
					return;
				}

				ingestServers(r);
			}

			catch(Throwable e)
			{
				failed = true;
			}

			if(failed)
			{
				D.as("ShadowServerController").f("Failed to connect to proxy. Attempting to reconnect...");
				J.s(() -> ShadeClient.instance.establishConnection());
			}
		}, 20 * M.rand(20, 60));
	}

	private void ingestServers(RestlessObject r)
	{
		if(r instanceof RSendServers)
		{
			List<ConnectableServer> s = ((RSendServers) r).servers();

			for(ConnectableServer i : s)
			{
				Shade.updateServer(i);
			}
		}
	}

	@Override
	public void stop()
	{

	}

	@Override
	public void tick()
	{

	}
}
