package io.shadowrealm.shade.map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.volmit.phantom.plugin.AR;
import com.volmit.phantom.plugin.PhantomPlugin;
import com.volmit.phantom.plugin.S;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.plugin.Scaffold.Async;
import com.volmit.phantom.services.NMSSVC;
import com.volmit.phantom.services.ViaVersionSVC;
import com.volmit.phantom.text.C;
import com.volmit.phantom.time.M;
import com.volmit.phantom.util.ParticleEffect;
import com.volmit.phantom.util.Protocol;
import com.volmit.phantom.util.RecordType;

public class ActivePlayer implements Listener
{
	private Player player;
	private ActiveMap map;
	private Sound currentMusic;
	private ChatColor currentColor;
	private String currentRegion;
	private long lms;
	private MapMood currentMood;
	private AR ar;

	public ActivePlayer(Player player, ActiveMap map)
	{
		lms = M.ms();
		this.player = player;
		this.map = map;
		Bukkit.getPluginManager().registerEvents(this, PhantomPlugin.plugin);
		ar = new AR(100)
		{
			@Override
			public void run()
			{
				tick();
			}
		};

		new S()
		{
			@Override
			public void run()
			{
				player.setFlying(false);
				player.setAllowFlight(true);
				player.getInventory().clear();
				ItemStack is = new ItemStack(Material.ELYTRA);
				is.addEnchantment(Enchantment.DURABILITY, 2);
				ItemMeta im = is.getItemMeta();
				im.setUnbreakable(true);
				im.setDisplayName(ChatColor.LIGHT_PURPLE + "Shadow Wings");
				is.setItemMeta(im);
				player.getInventory().setArmorContents(new ItemStack[] {null, null, is, null});
				player.updateInventory();
			}
		};
	}

	@Async
	private void tick()
	{
		Location currentLocation = player.getLocation().clone().add(player.getLocation().getDirection().clone().multiply(3));

		new S()
		{
			@Override
			public void run()
			{
				player.setAllowFlight(true);
				player.setFlying(false);
			}
		};

		if(map.isDebug())
		{
			ParticleEffect.CRIT.display(0.25f, 14, currentLocation, 32);
		}

		MapColor color = map.sampleColor(currentLocation);
		MapRegion region = map.sampleRegion(currentLocation);
		MapMusic music = map.sampleMusic(currentLocation);

		if(color != null)
		{
			ChatColor cx = ChatColor.valueOf(color.getColor().toUpperCase());
			if(currentColor == null || !currentColor.equals(cx))
			{
				currentColor = cx;
				onColorChanged();
			}
		}

		if(region != null)
		{
			String name = region.getName();
			MapMood mood = region.getMood();

			if(currentRegion == null || currentMood == null || (!(currentRegion.equals(name) && currentMood.equals(mood))))
			{
				currentRegion = name;
				currentMood = mood;
				onRegionChanged();
			}
		}

		if(music != null)
		{
			Sound song = Sound.valueOf(music.getMusic());
			float v = music.getSpeed();

			if(currentMusic == null || !song.equals(currentMusic))
			{
				Sound old = currentMusic;
				currentMusic = song;
				onMusicChanged(old, v, music.getPosition().getLocation(map.getRift().getWorld()));
			}
		}

		if(currentColor == null)
		{
			currentColor = ChatColor.GRAY;
		}
	}

	private void onMusicChanged(Sound old, float v, Location at)
	{
		if(currentMusic != null)
		{
			if(canUseMusic())
			{
				if(old != null)
				{
					player.stopSound(old);
				}

				player.stopSound(currentMusic);
				player.playSound(at, currentMusic, 250f, v);
			}

			else
			{
				onMusicChangedLegacy(old, at, v);
			}
		}
	}

	private boolean canUseMusic()
	{
		if(Bukkit.getPluginManager().getPlugin("ViaVersion") == null)
		{
			return true;
		}

		return Protocol.R1_9.to(Protocol.R1_12_2).contains(SVC.get(ViaVersionSVC.class).getVersion(player));
	}

	private void onMusicChangedLegacy(Sound old, Location l, float pitch)
	{
		if(currentMusic != null)
		{
			if(old != null && currentMusic.equals(old))
			{
				return;
			}

			SVC.get(NMSSVC.class).playRecord(player, map.getMapData().getCenter().getLocation(map.getRift().getWorld()), RecordType.from(currentMusic));
		}
	}

	private void onRegionChanged()
	{
		player.playSound(player.getLocation(), currentMood.getGameSound(), 4f, currentMood.getPitch());
		player.sendTitle("", C.GRAY + "" + C.BOLD + "" + currentRegion, currentMood.equals(MapMood.FAR) ? 25 : currentMood.equals(MapMood.DARK) ? 45 : 7, 14, currentMood.equals(MapMood.FAR) ? 50 : currentMood.equals(MapMood.DARK) ? 60 : 25);
	}

	private void onColorChanged()
	{

	}

	@EventHandler
	public void on(PlayerToggleFlightEvent e)
	{
		if(e.getPlayer().equals(player) && e.isFlying() && e.getPlayer().isOnGround() && M.ms() - lms > 1265)
		{
			lms = M.ms();
			e.getPlayer().setFlying(false);
			org.bukkit.util.Vector direction = e.getPlayer().getLocation().getDirection().clone().normalize();
			direction.setY(0.25);
			e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2f, 1.27f);
			ParticleEffect.SMOKE_NORMAL.display(0.24f, 14, e.getPlayer().getLocation(), 64);
			e.getPlayer().setVelocity(e.getPlayer().getVelocity().clone().add(direction));
		}

		else
		{
			e.setCancelled(true);
			e.getPlayer().setFlying(false);
		}
	}

	public void destroy()
	{
		try
		{
			ar.cancel();
		}

		catch(Throwable e)
		{

		}

		HandlerList.unregisterAll(this);
	}
}
