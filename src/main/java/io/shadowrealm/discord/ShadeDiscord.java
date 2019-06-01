package io.shadowrealm.discord;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class ShadeDiscord
{
	public static JDA api;

	public static void main(String[] a) throws LoginException
	{
		l("Starting Shade (Discord Bot)...");
		@SuppressWarnings("unused")
		JDA api = new JDABuilder("[REDACTED]").build();
	}

	public static void l(String s)
	{
		System.out.println("[INFO]: " + s);
	}

	public static void w(String s)
	{
		System.out.println("[WARN]: " + s);
	}

	public static void f(String s)
	{
		System.out.println("[FATAL]: " + s);
	}
}
