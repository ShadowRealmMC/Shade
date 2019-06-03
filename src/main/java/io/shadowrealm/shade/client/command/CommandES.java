package io.shadowrealm.shade.client.command;

import org.bukkit.Sound;

import mortar.api.nms.Catalyst;
import mortar.api.sched.J;
import mortar.api.sound.Audio;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;

public class CommandES extends MortarCommand
{
	public CommandES()
	{
		super("elderscroll");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		sender.sendMessage("You look into yourself and try to find something new.");

		J.s(() -> sender.sendMessage("Nothing..."), 60);
		J.s(() -> sender.sendMessage("Still, Nothing...."), 90);
		J.s(() -> sender.sendMessage("You Find literally nothing..."), 160);
		J.s(() -> sender.sendMessage("Wait!"), 190);
		J.s(() -> sender.sendMessage("Wait a second I think I found an eld-"), 197);
		J.s(() -> new Audio().s(Sound.MUSIC_CREDITS).vp(1f, 1.5f).play(sender.player()), 198);
		J.s(() -> new Audio().s(Sound.MUSIC_CREDITS).vp(1f, 1.55f).play(sender.player()), 198);
		J.s(() -> Catalyst.host.sendPacket(sender.player(), Catalyst.host.packetGameState(4, 1)), 200);
		return true;
	}
}
