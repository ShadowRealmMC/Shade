package io.shadowrealm.shade.common;

public class StatisticTracker
{
	private double value;

	public StatisticTracker()
	{
		value = 0;
	}

	public void add(double v)
	{
		value += v;
	}

	public void sub(double v)
	{
		add(-v);
	}

	public void sub(int v)
	{
		add(-v);
	}

	public void sub(long v)
	{
		add(-v);
	}

	public void add(int v)
	{
		add((double) v);
	}

	public void add(long v)
	{
		add((double) v);
	}

	public void set(double v)
	{
		value = v;
	}

	public void set(int v)
	{
		set((double) v);
	}

	public void set(long v)
	{
		set((double) v);
	}

	public double doubleValue()
	{
		return value;
	}

	public int intValue()
	{
		return (int) doubleValue();
	}

	public long longValue()
	{
		return (long) doubleValue();
	}
}
