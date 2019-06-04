package io.shadowrealm.shade.client;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.shadowrealm.shade.common.ConnectableServer;
import io.shadowrealm.shade.common.RestlessConnector;
import io.shadowrealm.shade.common.Statistics;
import io.shadowrealm.shade.common.messages.RAccount;
import io.shadowrealm.shade.common.messages.RCycleData;
import io.shadowrealm.shade.common.messages.RError;
import io.shadowrealm.shade.common.messages.RGetAccount;
import io.shadowrealm.shade.common.messages.RGetCycleData;
import io.shadowrealm.shade.common.messages.RGetRanks;
import io.shadowrealm.shade.common.messages.RGetUnlocks;
import io.shadowrealm.shade.common.messages.RGiveSXP;
import io.shadowrealm.shade.common.messages.RLoggedIn;
import io.shadowrealm.shade.common.messages.RRanks;
import io.shadowrealm.shade.common.messages.RSXPChanged;
import io.shadowrealm.shade.common.messages.RSetStatistics;
import io.shadowrealm.shade.common.messages.RStateChanged;
import io.shadowrealm.shade.common.messages.RUnlocks;
import io.shadowrealm.shade.common.table.ShadowAccount;
import io.shadowrealm.shade.common.table.ShadowRank;
import io.shadowrealm.shade.common.table.ShadowUnlock;
import mortar.api.sched.J;
import mortar.api.world.P;
import mortar.bukkit.plugin.Controller;
import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.logic.queue.ChronoLatch;
import mortar.util.text.D;

public class ShadowPlayerController extends Controller
{
	private GMap<Player, Statistics> stats;
	private GMap<Player, ShadowAccount> shadows;
	private GList<ShadowRank> ranks;
	private GList<ShadowUnlock> unlocks;
	private long cycleInterval;
	private ConnectableServer lastState;
	private String status;
	private String tagline;
	private long since;
	private int online;
	private ChronoLatch latch;

	@Override
	public void start()
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> start(), 2);
			return;
		}

		stats = new GMap<>();
		latch = new ChronoLatch(10000);
		online = 0;
		tagline = "";
		status = "&aOnline";
		since = M.ms();
		lastState = new ConnectableServer(ClientConfig.SERVER__NAME, ClientConfig.SERVER__ID, status, tagline, since, P.onlinePlayers().size());
		shadows = new GMap<>();
		new RGetRanks().complete(ShadeClient.instance.getConnector(), (r) -> ranks = new GList<>(((RRanks) r).ranks()));
		new RGetUnlocks().complete(ShadeClient.instance.getConnector(), (r) ->
		{
			unlocks = new GList<>(((RUnlocks) r).unlocks());
			l("Unlocks" + unlocks.size());
		});
		new RGetCycleData().complete(ShadeClient.instance.getConnector(), (r) -> cycleInterval = ((RCycleData) r).cycle());
		J.ar(() -> updateState(), 20 * 30);
		J.ar(() -> syncronizeStatistics(), 6000);
		J.s(() -> status = "&aOnline", 100);
		J.s(() -> since = M.ms(), 100);
	}

	private void updateState()
	{
		online = P.onlinePlayers().size();
		Shade.updateServer(lastState);

		if(latch.flip() || online != lastState.getOnline() || !tagline.equals(lastState.getTagline()) || !status.equals(lastState.getStatus()) || since != lastState.getSince() || P.onlinePlayers().size() != lastState.getOnline())
		{
			lastState.setTagline(tagline);
			lastState.setStatus(status);
			lastState.setSince(since);
			lastState.setOnline(online);
			sendStateChange();
		}
	}

	public void sendStateChange()
	{
		new RStateChanged().server(lastState).complete(ShadeClient.instance.getConnector(), (r) ->
		{
			if(r == null || r instanceof RError)
			{
				D.as("Shade Anchor").f("Proxy doesnt know who the fuck we are. WE REALLY NEED TO REBOOT, but ill try to reconnect...");
				ShadeClient.instance.establishConnection();
			}
		});
	}

	@Override
	public void stop()
	{
		syncronizeStatistics();

		for(Player i : shadows.k())
		{
			quit(i);
		}

		lastState.setStatus("&cRestarting");
		lastState.setSince(M.ms());
		lastState.setOnline(0);
		sendStateChange();
	}

	@Override
	public void tick()
	{

	}

	@EventHandler
	public void on(AsyncPlayerChatEvent e)
	{
		if(e.getMessage().equals("r"))
		{
			ShadowAccount a = shadows.get(e.getPlayer());
			e.getPlayer().sendMessage("Current SXP: " + F.f(a.getShadowXP()));
			e.getPlayer().sendMessage("Earned: " + F.f(a.getShadowXPEarned()));
			e.getPlayer().sendMessage("Last Earned: " + F.f(a.getShadowXPLastEarned()));
			e.getPlayer().sendMessage("Rank: " + F.f((a.getShadowXPLastEarned())) + " (" + computeRank(a).getFullName() + ")");
		}

		if(e.getMessage().equals("x"))
		{
			ShadowAccount a = shadows.get(e.getPlayer());
			new RGiveSXP().player(a.getId()).amount(500).complete(ShadeClient.instance.getConnector(), (rx) ->
			{
				if(rx instanceof RSXPChanged)
				{
					RSXPChanged x = (RSXPChanged) rx;
					a.setShadowXP(x.current());
					a.setShadowXPEarned(x.earned());
					e.getPlayer().sendMessage("Current SXP: " + F.f(a.getShadowXP()));
					e.getPlayer().sendMessage("Earned: " + F.f(a.getShadowXPEarned()));
					e.getPlayer().sendMessage("Last Earned: " + F.f(a.getShadowXPLastEarned()));
					e.getPlayer().sendMessage("Rank: " + F.f((a.getShadowXPLastEarned())) + " (" + computeRank(a).getFullName() + ")");
				}
			});
		}
	}

	public ShadowRank computeRank(ShadowAccount a)
	{
		GList<ShadowRank> qualifies = new GList<ShadowRank>();
		long sr = a.getShadowXPLastEarned();
		for(ShadowRank i : getRanks())
		{
			if(sr >= i.getMinSR() && sr <= i.getMaxSR())
			{
				qualifies.add(i);
			}
		}

		if(qualifies.isEmpty())
		{
			throw new RuntimeException("Unable to determine rank for sr: " + sr);
		}

		if(qualifies.size() == 1)
		{
			return qualifies.get(0);
		}

		ShadowRank chosen = null;
		int p = Integer.MIN_VALUE;

		for(ShadowRank i : qualifies)
		{
			if(i.getPrioirty() > p)
			{
				p = i.getPrioirty();
				chosen = i;
			}
		}

		return chosen;
	}

	@EventHandler
	public void on(PlayerQuitEvent e)
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> on(e), 2);
			return;
		}

		J.a(() -> quit(e.getPlayer()));
	}

	public ShadowAccount getAccount(Player player)
	{
		return shadows.get(player);
	}

	public void syncronize(Player p)
	{
		new RGetAccount().player(p.getUniqueId()).complete(Shade.connect(), (r) ->
		{
			if(r instanceof RAccount)
			{
				RAccount a = (RAccount) r;
				ShadowAccount ac = a.shadowAccount();
				ac.setCachedName(p.getName());
				ac.setCachedServer(ClientConfig.SERVER__ID);
				shadows.put(p, ac);
			}
		});
	}

	@EventHandler
	public void on(PlayerJoinEvent e)
	{
		if(!ShadeClient.ready)
		{
			J.s(() -> on(e), 2);
			return;
		}

		//@builder
		new RLoggedIn()
		.player(e.getPlayer().getUniqueId())
		.name(e.getPlayer().getName())
		.server(ClientConfig.SERVER__ID)
		.completeBlind(ShadeClient.instance.getConnector());
		new RGetAccount().player(e.getPlayer().getUniqueId()).complete(ShadeClient.instance.getConnector(), (r) ->
		{
			if(r instanceof RAccount)
			{
				RAccount a = (RAccount) r;
				ShadowAccount ac = a.shadowAccount();
				ac.setCachedName(e.getPlayer().getName());
				ac.setCachedServer(ClientConfig.SERVER__ID);
				join(e.getPlayer(), ac);
			}
		});
		//@done
	}

	public void syncronizeStatistics()
	{
		for(Player i : stats.k())
		{
			syncronizeStatistics(i);
		}
	}

	public void syncronizeStatistics(Player i)
	{
		if(getAccount(i).getStatistics().equals(getStats(i)))
		{
			stats.remove(i);
			return;
		}

		getAccount(i).setStatistics(getStats(i));
		new RSetStatistics().player(i.getUniqueId()).statistics(getStats(i).toString()).complete(Shade.connect());
		stats.remove(i);
	}

	private void quit(Player player)
	{
		syncronizeStatistics(player);
		shadows.remove(player);
		stats.remove(player);
	}

	private void join(Player player, ShadowAccount shadowAccount)
	{
		shadows.put(player, shadowAccount);
		l("Logged " + player.getName() + " into shadow account");
	}

	public RestlessConnector getC()
	{
		return ShadeClient.instance.getConnector();
	}

	public GMap<Player, ShadowAccount> getShadows()
	{
		return shadows;
	}

	public GList<ShadowRank> getRanks()
	{
		return ranks;
	}

	public long getCycleInterval()
	{
		return cycleInterval;
	}

	public GList<ShadowUnlock> getUnlocks()
	{
		return new GList<>(unlocks);
	}

	public ConnectableServer getLastState()
	{
		return lastState;
	}

	public String getStatus()
	{
		return status;
	}

	public String getTagline()
	{
		return tagline;
	}

	public long getSince()
	{
		return since;
	}

	public int getOnline()
	{
		return online;
	}

	public ChronoLatch getLatch()
	{
		return latch;
	}

	public Statistics getStats(Player p)
	{
		if(!stats.containsKey(p))
		{
			stats.put(p, getAccount(p).getStatistics());
		}

		return stats.get(p);
	}
}
