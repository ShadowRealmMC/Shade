package io.shadowrealm.shade.map;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.volmit.phantom.api.math.M;
import com.volmit.phantom.api.protocol.Protocol;
import com.volmit.phantom.api.service.SVC;
import com.volmit.phantom.api.sheduler.AR;
import com.volmit.phantom.api.sheduler.S;
import com.volmit.phantom.api.sheduler.SR;
import com.volmit.phantom.lib.service.NMSSVC;
import com.volmit.phantom.lib.service.ViaVersionSVC;
import com.volmit.phantom.main.PhantomPlugin;
import com.volmit.phantom.util.sfx.RecordType;
import com.volmit.phantom.util.text.C;
import com.volmit.phantom.util.vfx.ColoredParticleEffect;
import com.volmit.phantom.util.vfx.ParticleEffect;
import com.volmit.phantom.util.world.BlastResistance;
import com.volmit.phantom.util.world.Cuboid;
import com.volmit.phantom.util.world.Cuboid.CuboidDirection;
import com.volmit.phantom.util.world.PE;
import com.volmit.phantom.util.world.VectorMath;

import io.shadowrealm.shade.Shade;
import io.shadowrealm.shade.services.LobbySVC;

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
	private double energy;
	private double maxEnergy;
	private int delayEnergy;
	private int introLevel;

	public ActivePlayer(Player player, ActiveMap map)
	{
		introLevel = 0;
		delayEnergy = Shade.config.COMPONENT_LOBBY_ENERGY_REGEN_DELAY;
		this.player = player;
		this.map = map;
		Bukkit.getPluginManager().registerEvents(this, PhantomPlugin.plugin);
		maxEnergy = Shade.config.COMPONENT_LOBBY_ENERGY_MAX;
		energy = Shade.config.COMPONENT_LOBBY_ENERGY_START;

		new S()
		{
			@Override
			public void run()
			{
				PE.BLINDNESS.a(10).d(10000).apply(player);
			}
		};

		ar = new AR(Shade.config.COMPONENT_LOBBY_SAMPLERATE)
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

	public boolean can(double energy)
	{
		if(this.energy > energy)
		{
			delayEnergy = Shade.config.COMPONENT_LOBBY_ENERGY_REGEN_DELAY;
			this.energy -= energy;
			return true;
		}

		return false;
	}

	@EventHandler
	public void on(PlayerToggleSprintEvent e)
	{
		if(!e.getPlayer().equals(player) || !player.getWorld().equals(map.getRift().getWorld()))
		{
			return;
		}
		new S()
		{
			@Override
			public void run()
			{
				if(e.getPlayer().isSprinting())
				{
					if(can(20))
					{
						e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().clone().multiply(1.5));

						for(int i = 0; i < 16; i++)
						{
							new ColoredParticleEffect(Color.fromRGB(33, 33, 33)).play(player.getLocation().clone().add(Vector.getRandom().subtract(Vector.getRandom()).multiply(6)));
						}

						player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENDERCHEST_OPEN, 1f, 0.37f);
					}

					else
					{
						player.setSprinting(false);
					}
				}
			}
		};
	}

	protected void tickSync()
	{
		if(!player.getWorld().equals(map.getRift().getWorld()))
		{
			return;
		}

		if(player.isFlying())
		{
			player.setFlySpeed(0.1f);
			player.setFlying(false);
			player.setAllowFlight(false);

			if(can(27))
			{
				org.bukkit.util.Vector direction = player.getLocation().getDirection().clone();
				direction.setY(0.25);
				direction.normalize();
				direction.multiply(1.73f);

				if(player.isSneaking() && can(95))
				{
					direction = player.getLocation().getDirection().clone().normalize();
					direction.multiply(3.25f);
					player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 10f, 1.57f);
				}

				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.57f);
				ParticleEffect.SMOKE_NORMAL.display(0.24f, 14, player.getLocation(), 64);
				player.setVelocity(direction);
			}
		}

		if(player.isOnGround())
		{
			if(energy > 66)
			{
				player.setAllowFlight(true);
			}

			else
			{
				player.setAllowFlight(false);
			}

			if(player.isSprinting())
			{
				if(can(0.86))
				{
					PE.SPEED.a(3).d(10).apply(player);
				}

				else
				{
					player.setSprinting(false);
				}
			}
		}

		else if(player.isGliding() && !player.isSneaking())
		{
			player.setGliding(false);

			for(int i = 0; i < 16; i++)
			{
				new ColoredParticleEffect(Color.fromRGB(33, 33, 33)).play(player.getLocation().clone().add(Vector.getRandom().subtract(Vector.getRandom()).multiply(6)));
			}

			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 0.53f, 0.57f);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 0.15f, 0.57f);
		}

		else if(!player.isGliding() && player.isSneaking() && !player.isOnGround())
		{
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 1.25f, 0.57f);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 0.45f, 0.57f);
			player.setGliding(true);

			for(int i = 0; i < 16; i++)
			{
				new ColoredParticleEffect(Color.fromRGB(33, 33, 33)).play(player.getLocation().clone().add(Vector.getRandom().subtract(Vector.getRandom()).multiply(6)));
			}
		}

		if(player.isGliding() && VectorMath.getSpeed(player.getVelocity().clone()) < 1.0 && can(3.28))
		{
			int speedr = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 6) / 6D) * 255);
			int speedg = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 1) / 1D) * 255);
			int speedb = (int) ((M.clip(VectorMath.getSpeed(player.getVelocity()), 0, 3) / 3D) * 255);

			for(int i = 0; i < 8; i++)
			{
				new ColoredParticleEffect(Color.fromRGB(speedr, speedg, speedb)).play(player.getLocation().clone().add(player.getVelocity().clone().multiply(2.25f)).clone().add(Vector.getRandom().subtract(Vector.getRandom()).clone().multiply(VectorMath.getSpeed(player.getVelocity()))));
			}

			player.setVelocity(player.getVelocity().clone().add(player.getLocation().getDirection().clone().multiply(0.074)));
		}

		if(Shade.config.COMPONENT_LOBBY_BLOCK_DESTRUCTION && player.isGliding())
		{
			Vector d = player.getVelocity();
			double speed = VectorMath.getSpeed(d);
			speed *= 1.25;
			Cuboid c = new Cuboid(player.getLocation(), player.getLocation().clone().add(d.clone().multiply(3.25)));
			Iterator<Block> b = c.iterator();
			c.outset(CuboidDirection.Both, 1);

			while(b.hasNext())
			{
				Block block = b.next();

				if(block.isEmpty())
				{
					continue;
				}

				double resistance = (double) BlastResistance.get(block.getType()) / 5D;

				if(resistance > 0 && speed > resistance)
				{
					player.setVelocity(player.getVelocity().clone().multiply(0.94));
					Material type = block.getType();
					@SuppressWarnings("deprecation")
					byte data = block.getData();
					block.setType(Material.AIR);
					@SuppressWarnings("deprecation")
					FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation().clone().add(0.5, 0, 0.5), type, data);
					fb.setDropItem(false);
					fb.setHurtEntities(true);
					fb.setGravity(true);
					fb.setVelocity(player.getVelocity().clone().multiply(1.25));
				}
			}
		}

		double current = player.getExp();
		double actual = energy / maxEnergy;
		if(actual > current)
		{
			current += ((actual - current) / 20);
		}

		else if(actual < current)
		{
			current -= ((current - actual) / 7);
		}

		actual = current;
		player.setExp((float) M.clip(actual, 0, 1));

		if(delayEnergy > 0)
		{
			delayEnergy--;
		}

		else
		{
			energy = M.clip(energy + Shade.config.COMPONENT_LOBBY_ENERGY_REGEN_AMOUNT, 0, maxEnergy);
		}
	}

	private void tick()
	{
		Location currentLocation = player.getLocation().clone().add(player.getLocation().getDirection().clone().multiply(3));

		if(!currentLocation.getWorld().equals(map.getRift().getWorld()))
		{
			return;
		}

		if(introLevel == 3)
		{
			introLevel++;
		}

		if(introLevel == 2)
		{
			introLevel++;
			player.sendTitle(C.GOLD + C.translateAlternateColorCodes('&', SVC.get(LobbySVC.class).getConfig().getVariationName()), C.DARK_GRAY + "" + C.BOLD + "By " + C.GOLD + C.BOLD + C.translateAlternateColorCodes('&', SVC.get(LobbySVC.class).getConfig().getMapAuthors()), 15, 65, 100);
		}

		if(introLevel == 1)
		{
			introLevel++;
		}

		if(introLevel == 0)
		{
			introLevel++;
			SVC.get(LobbySVC.class).ready = true;
			player.sendTitle(C.BOLD + "" + C.LIGHT_PURPLE + "" + C.BOLD + "Shadow" + C.DARK_GRAY + C.BOLD + "Realms", C.GOLD + "" + C.BOLD + C.translateAlternateColorCodes('&', SVC.get(LobbySVC.class).getConfig().getMapName()), 15, 5, 100);

			new S()
			{
				@Override
				public void run()
				{
					player.removePotionEffect(PotionEffectType.BLINDNESS);
					PE.BLINDNESS.a(100).d(20).apply(player);
					player.teleport(map.getRandomSpawn());
				}
			};
		}

		if(introLevel < 0)
		{
			introLevel++;
		}

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

				if(introLevel >= 4)
				{
					onRegionChanged();
				}
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
