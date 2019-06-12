package io.shadowrealm.shade.module;

import java.io.File;

import org.bukkit.entity.EntityType;

import mortar.api.config.Configurator;
import mortar.api.config.Key;

public class SpawnerStackingConfig
{
	@Key("toggles.disable-stacking")
	private boolean disabledStacking = false;

	@Key("toggles.disable-spawner")
	private boolean disabledSpawner = false;

	@Key("max-level")
	private int maxLevel = 10;

	@Key("interval-ticks.min-level")
	private int minLevelInterval = 125;

	@Key("interval-ticks.max-level")
	private int maxLevelInterval = 65;

	@Key("activation-range.min-level")
	private int minLevelActivation = 8;

	@Key("activation-range.max-level")
	private int maxLevelActivation = 24;

	@Key("spawn-count.min-level")
	private int minLevelSpawnCount = 1;

	@Key("spawn-count.max-level")
	private int maxLevelSpawnCount = 8;

	@Key("upgrade-cost.base-cost")
	private double upgradeCost = 10000;

	@Key("upgrade-cost.level-multiplier")
	private double upgradeCostLevelMultiplier = 1.15;

	private EntityType type;

	public SpawnerStackingConfig(EntityType type)
	{
		this.type = type;
	}

	public void load(File folder) throws Throwable
	{
		Configurator.BUKKIT.load(this, new File(folder, type.toString().toLowerCase() + ".yml"));
	}

	public void write(File folder) throws Throwable
	{
		Configurator.BUKKIT.write(this, new File(folder, type.toString().toLowerCase() + ".yml"));
	}

	public boolean isDisabledStacking()
	{
		return disabledStacking;
	}

	public void setDisabledStacking(boolean disabledStacking)
	{
		this.disabledStacking = disabledStacking;
	}

	public boolean isDisabledSpawner()
	{
		return disabledSpawner;
	}

	public void setDisabledSpawner(boolean disabledSpawner)
	{
		this.disabledSpawner = disabledSpawner;
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
	}

	public int getMinLevelInterval()
	{
		return minLevelInterval;
	}

	public void setMinLevelInterval(int minLevelInterval)
	{
		this.minLevelInterval = minLevelInterval;
	}

	public int getMaxLevelInterval()
	{
		return maxLevelInterval;
	}

	public void setMaxLevelInterval(int maxLevelInterval)
	{
		this.maxLevelInterval = maxLevelInterval;
	}

	public int getMinLevelSpawnCount()
	{
		return minLevelSpawnCount;
	}

	public void setMinLevelSpawnCount(int minLevelSpawnCount)
	{
		this.minLevelSpawnCount = minLevelSpawnCount;
	}

	public int getMaxLevelSpawnCount()
	{
		return maxLevelSpawnCount;
	}

	public void setMaxLevelSpawnCount(int maxLevelSpawnCount)
	{
		this.maxLevelSpawnCount = maxLevelSpawnCount;
	}

	public double getUpgradeCost()
	{
		return upgradeCost;
	}

	public void setUpgradeCost(double upgradeCost)
	{
		this.upgradeCost = upgradeCost;
	}

	public double getUpgradeCostLevelMultiplier()
	{
		return upgradeCostLevelMultiplier;
	}

	public void setUpgradeCostLevelMultiplier(double upgradeCostLevelMultiplier)
	{
		this.upgradeCostLevelMultiplier = upgradeCostLevelMultiplier;
	}

	public EntityType getType()
	{
		return type;
	}

	public void setType(EntityType type)
	{
		this.type = type;
	}
}
