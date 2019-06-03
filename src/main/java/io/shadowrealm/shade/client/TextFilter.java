package io.shadowrealm.shade.client;

import mortar.lang.collection.GList;
import mortar.util.text.C;

public enum TextFilter
{
	RGB(C.RED, C.GREEN, C.BLUE, C.AQUA, C.YELLOW, C.LIGHT_PURPLE, C.GOLD),
	THICC_RGB(2, C.RED, C.GREEN, C.BLUE, C.AQUA, C.YELLOW, C.LIGHT_PURPLE, C.GOLD),
	USA(C.RED, C.WHITE, C.BLUE),
	XMAS(C.DARK_RED, C.DARK_GREEN),
	HALLOWEEN(C.DARK_PURPLE, C.GOLD),
	CANDY_CANE(C.DARK_RED, C.WHITE);

	private String[] prefixes;

	private TextFilter(String... prefixes)
	{
		this.prefixes = prefixes;
	}

	private TextFilter(C... prefixes)
	{
		this.prefixes = new String[prefixes.length];

		for(int i = 0; i < prefixes.length; i++)
		{
			this.prefixes[i] = prefixes[i].toString();
		}
	}

	private TextFilter(int dupe, C... prefixes)
	{
		GList<String> g = new GList<String>();

		for(int i = 0; i < prefixes.length; i++)
		{
			for(int j = 0; j < dupe; j++)
			{
				g.add(prefixes[i].toString());
			}
		}

		this.prefixes = g.toArray(new String[g.size()]);
	}

	private TextFilter(int dupe, String... prefixes)
	{
		GList<String> g = new GList<String>();

		for(String i : prefixes)
		{
			for(int j = 0; j < dupe; j++)
			{
				g.add(i);
			}
		}

		this.prefixes = g.toArray(new String[g.size()]);
	}

	public String[] getPrefixes()
	{
		return prefixes;
	}
}
