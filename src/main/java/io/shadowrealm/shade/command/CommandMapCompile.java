package io.shadowrealm.shade.command;

import com.volmit.phantom.lang.Callback;
import com.volmit.phantom.lang.F;
import com.volmit.phantom.lang.GMap;
import com.volmit.phantom.lang.Profiler;
import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.text.C;
import com.volmit.phantom.util.Cuboid;
import com.volmit.phantom.world.WorldEditor;

import io.shadowrealm.shade.Shade;
import io.shadowrealm.shade.map.CompiledMap;
import io.shadowrealm.shade.map.MapPosition;
import io.shadowrealm.shade.services.MapBuilderSVC;

public class CommandMapCompile extends PhantomCommand
{
	public CommandMapCompile()
	{
		super("compile", "build");
		requiresPermission(Shade.perm.map.compile);
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!WorldEditor.hasSelection(sender.player()))
		{
			sender.sendMessage("Make a World Edit Selection of the map.");
			return true;
		}

		Profiler px = new Profiler();
		px.begin();
		Cuboid c = WorldEditor.getSelection(sender.player());
		sender.sendMessage("===== Compiling Map =====");
		SVC.get(MapBuilderSVC.class).compile(c, false, new Callback<CompiledMap>()
		{
			@Override
			public void run(CompiledMap t)
			{
				px.end();
				sender.sendMessage("Map compiled in " + F.time(px.getMilliseconds(), 1) + " with " + t.getWarnings().size() + " warning" + (t.getWarnings().size() == 1 ? "" : "s") + ".");
				sender.sendMessage("Spawns: " + t.getMap().getSpawns().size());
				sender.sendMessage("Regions: " + t.getMap().getRegions().size());
				sender.sendMessage("Musical Regions: " + t.getMap().getMusic().size());
				sender.sendMessage("Color Samples: " + t.getMap().getColors().size());

				if(t.getWarnings().size() > 0)
				{
					GMap<String, Integer> warnings = new GMap<>();
					sender.sendMessage("Map compiled with " + t.getWarnings().size() + " warning" + (t.getWarnings().size() == 1 ? "" : " "));

					for(String i : t.getWarnings().v())
					{
						if(!warnings.containsKey(i))
						{
							warnings.put(i, 0);
						}

						warnings.put(i, warnings.get(i) + 1);
					}

					for(String i : warnings.k())
					{
						sender.sendMessage(C.RED + "!! " + C.WHITE + i + C.YELLOW + " in " + warnings.get(i) + " position" + (warnings.get(i) == 1 ? "" : " "));
					}

					MapPosition mp = t.getWarnings().k().get(0);
					sender.player().teleport(mp.getLocation(sender.player().getWorld()));
				}
			}
		});

		return true;
	}
}
