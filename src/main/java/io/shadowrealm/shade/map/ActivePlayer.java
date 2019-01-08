package io.shadowrealm.shade.map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.volmit.phantom.plugin.AR;
import com.volmit.phantom.plugin.PhantomPlugin;
import com.volmit.phantom.plugin.S;
import com.volmit.phantom.plugin.SR;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.plugin.Scaffold.Async;
import com.volmit.phantom.services.NMSSVC;
import com.volmit.phantom.services.ViaVersionSVC;
import com.volmit.phantom.text.C;
import com.volmit.phantom.time.M;
import com.volmit.phantom.util.ColoredParticleEffect;
import com.volmit.phantom.util.ParticleEffect;
import com.volmit.phantom.util.Protocol;
import com.volmit.phantom.util.RecordType;
import com.volmit.phantom.util.VectorMath;

public class ActivePlayer implements Listener
{
	private Player player;
	private ActiveMap map;
	private Sound currentMusic;
	private ChatColor currentColor;
	private String currentRegion;
	private MapMood currentMood;
	private AR ar;
	private SR sr;
	private boolean destroyed;

	public ActivePlayer(Player player, ActiveMap map)
	{
		this.player = player;
		this.map = map;
		Bukkit.getPluginManager().registerEvents(this, PhantomPlugin.plugin);
		ar = new AR(100)
		{
			@Override
			public void run()
			{
				if(destroyed)
				{
					return;
				}

				tick();
			}
		};

		sr = new SR(0)
		{
			@Override
			public void run()
			{
				if(destroyed)
				{
					return;
				}

				tickSync();
			}
		};

		new S()
		{
			@Override
			public void run()
			{
				player.setFlying(false);
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

	protected void tickSync()
	{
		if(player.isFlying())
		{
			player.setFlySpeed(0.1f);
			player.setFlying(false);
			player.setAllowFlight(false);
			org.bukkit.util.Vector direction = player.getLocation().getDirection().clone();
			direction.setY(0.25);
			direction.normalize();
			direction.multiply(3.5f);

			if(player.isSneaking())
			{
				direction.multiply(1.35f);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 10f, 1.57f);
			}

			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.57f);
			ParticleEffect.SMOKE_NORMAL.display(0.24f, 14, player.getLocation(), 64);
			player.setVelocity(direction);
		}

		if(player.isOnGround())
		{
			player.setAllowFlight(true);
		}

		else if(player.isGliding() && !player.isSneaking())
		{
			player.setGliding(false);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1f, 0.57f);
		}

		else if(!player.isGliding() && player.isSneaking())
		{
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1f, 0.57f);
			player.setGliding(true);
		}

		if(player.isGliding())
		{
			int speedr = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 6) / 6D) * 255);
			int speedg = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 1) / 1D) * 255);
			int speedb = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 3) / 3D) * 255);

			for(int i = 0; i < 8; i++)
			{
				new ColoredParticleEffect(Color.fromRGB(speedr, speedg, speedb)).play(player.getLocation().clone().add(player.getVelocity().clone().multiply(2.25f)).clone().add(Vector.getRandom().subtract(Vector.getRandom()).clone().multiply(VectorMath.getSpeed(player.getVelocity()))));
			}
		}
	}

	@Async
	private void tick()
	{
		Location currentLocation = player.getLocation().clone().add(player.getLocation().getDirection().clone().multiply(3));

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

	public void destroy()
	{
		destroyed = true;

		try
		{
			ar.cancel();
		}

		catch(Throwable e)
		{

		}

		try
		{
			sr.cancel();
		}

		catch(Throwable e)
		{

		}

		HandlerList.unregisterAll(this);
	}
}
