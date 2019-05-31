package io.shadowrealm.shade.common.table;

import mortar.api.sql.Column;
import mortar.api.sql.Table;
import mortar.api.sql.TableCache;
import mortar.util.text.C;

@Table("shadow_unlocks")
public class ShadowUnlock
{
	public static final TableCache<String, ShadowUnlock> CACHE = new TableCache<String, ShadowUnlock>(16);

	@Column(name = "id", type = "VARCHAR(36)", placeholder = "id", primary = true)
	private String id;

	@Column(name = "type", type = "VARCHAR(36)", placeholder = "type_id")
	private String type;

	@Column(name = "name", type = "VARCHAR(36)", placeholder = "Unlock Name")
	private String name;

	@Column(name = "description", type = "TEXT", placeholder = "Describes what this unlock does.")
	private String description;

	@Column(name = "consumable", type = "TINYINT", placeholder = "0")
	private byte consumable;

	@Column(name = "singleton", type = "TINYINT", placeholder = "1")
	private byte singleton;

	public ShadowUnlock()
	{
		this("idk");
	}

	public ShadowUnlock(String id)
	{
		this.id = id;
		this.type = "type_id";
		this.name = "Unlock Name";
		this.description = "Unlock Description";
		this.consumable = 0;
		this.singleton = 1;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public String getFormattedName()
	{
		return C.translateAlternateColorCodes('&', getName());
	}

	public String getFormattedDescription()
	{
		return C.translateAlternateColorCodes('&', getDescription());
	}

	public boolean isSingleton()
	{
		return singleton == 1;
	}

	public boolean isConsumable()
	{
		return consumable == 1;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setConsumable(boolean consumable)
	{
		this.consumable = (byte) (consumable ? 1 : 0);
	}

	public void setSingleton(boolean singleton)
	{
		this.singleton = (byte) (singleton ? 1 : 0);
	}
}
