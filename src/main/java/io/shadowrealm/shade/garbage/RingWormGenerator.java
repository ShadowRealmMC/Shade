package io.shadowrealm.shade.garbage;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class RingWormGenerator
{
	private Location current;
	private int currentRing;
	private Random random;
	private double difficulty;

	public RingWormGenerator(Location start, long seed)
	{
		this.difficulty = 0;
		this.current = start;
		this.currentRing = 0;
		random = new Random(seed);
		current.setDirection(randomVector());
	}

	public Location nextRing(Vector entry)
	{
		currentRing++;
		Vector enter = entry.normalize();
		Vector exit = enter.clone().add(randomVector().multiply(difficulty));
		double distance = 32D - ((24D * difficulty) * random.nextDouble());
		difficulty += random.nextDouble() * 0.038;
		Location l = current.clone().add(exit.clone().normalize().multiply(distance));
		current = l;
		return current.clone();
	}

	public int getRingsPassed()
	{
		return currentRing;
	}

	public Location getCurrentRing()
	{
		return current.clone();
	}

	private Vector randomVector()
	{
		return new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble()).subtract(new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble()));
	}

	public Random getRandom()
	{
		return random;
	}

	public double getDifficulty()
	{
		return difficulty;
	}
}
