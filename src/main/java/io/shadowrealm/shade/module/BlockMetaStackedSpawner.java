package io.shadowrealm.shade.module;

import org.bukkit.block.CreatureSpawner;

import mortar.api.blockmeta.BlockMeta;

public class BlockMetaStackedSpawner extends BlockMeta
{
	private int level = 1;

	public BlockMetaStackedSpawner(CreatureSpawner s)
	{
		super(s.getBlock());
	}

	public BlockMetaStackedSpawner()
	{

	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
}
