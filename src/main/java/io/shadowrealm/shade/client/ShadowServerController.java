package io.shadowrealm.shade.client;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.RestlessObject;
import io.shadowrealm.shade.common.messages.RGetServers;
import io.shadowrealm.shade.common.messages.RSendServers;
import mortar.api.sched.J;
import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GList;

public class ShadowServerController extends Controller
{
	private RestlessConnector c;

	@Override
	public void start()
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> start(), 2);
			return;
		}

		c = ShadeClient.instance.getConnector();
		J.ar(() -> new RGetServers().complete(c, (r) -> ingestServers(r)), 20 * 60);
	}

	private void ingestServers(RestlessObject r)
	{
		if(r instanceof RSendServers)
		{
			GList<ConnectableServer> s = ((RSendServers) r).servers();

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
