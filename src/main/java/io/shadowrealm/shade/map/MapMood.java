package io.shadowrealm.shade.map;

import java.io.Serializable;

import org.bukkit.Sound;

import com.volmit.phantom.lang.GList;

public enum MapMood implements Serializable
{
	FAR(Sound.ENTITY_FIREWORK_LARGE_BLAST_FAR, 0.25f, "cool", "nice", "smooth", "complex"),
	DARK(Sound.AMBIENT_CAVE, 0.76f, "spooky", "creepy", "scary", "bad", "sad", "evil", "hell"),
	LIGHT(Sound.ENTITY_ENDEREYE_DEATH, 0.8f, "bright", "sun", "town", "central", "main", "default");

	private GList<String> aliases;
	private Sound gameSound;
	private float pitch;

	private MapMood(Sound gameSound, float pitch, String... aliases)
	{
		this.gameSound = gameSound;
		this.pitch = pitch;
		this.aliases = new GList<String>(aliases);
	}

	public GList<String> getAliases()
	{
		return aliases;
	}

	public Sound getGameSound()
	{
		return gameSound;
	}

	public float getPitch()
	{
		return pitch;
	}

	public static MapMood parse(String mood)
	{
		for(MapMood i : values())
		{
			if(i.getAliases().contains(mood.toLowerCase()) || mood.equalsIgnoreCase(i.name()))
			{
				return i;
			}
		}

		throw new IllegalArgumentException("Invalid mood " + mood);
	}
}
