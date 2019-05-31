package io.shadowrealm.shade.common;

public class UnlockedItem
{
	private String id;
	private int amount;

	public UnlockedItem()
	{
		this.id = "";
		this.amount = 1;
	}

	public UnlockedItem(String id)
	{
		this();
		this.id = id;
		this.amount = 1;
	}

	public UnlockedItem(String id, int amount)
	{
		this(id);
		this.amount = amount;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}
}
