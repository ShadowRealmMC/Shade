package io.shadowrealm.shade.map;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;

public class MapPosition implements Serializable
{
	private static final long serialVersionUID = 8735395866945606325L;
	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;

	public static MapPosition getPosition(Location location)
	{
		MapPosition p = new MapPosition();
		p.x = location.getX();
		p.y = location.getY();
		p.z = location.getZ();
		p.yaw = location.getYaw();
		p.pitch = location.getPitch();

		return p;
	}

	public Location getLocation(World w)
	{
		return new Location(w, x, y, z, (float) yaw, (float) pitch);
	}

	public void setLocation(Location location)
	{
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		yaw = location.getYaw();
		pitch = location.getPitch();
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}

	public double getYaw()
	{
		return yaw;
	}

	public void setYaw(double yaw)
	{
		this.yaw = yaw;
	}

	public double getPitch()
	{
		return pitch;
	}

	public void setPitch(double pitch)
	{
		this.pitch = pitch;
	}
}
