package io.shadowrealm.shade.client;

import mortar.lang.collection.GList;

public class ClientConfig
{
	public static String WEBSERVER__SERVER_ADDRESS = "localhost";
	public static String WEBSERVER__CLIENT_ROUTE = "localhost";
	public static int WEBSERVER__SERVER_PORT = 15751;
	public static String SERVER__ID = "hub-3";
	public static String SERVER__NAME = "Hub 3";
	public static boolean WEBSERVER__CONNECTION_DEBUGGING = true;
	public static boolean DOWNLOAD_UPDATES = true;
	public static GList<String> UPDATE = new GList<String>().qadd("Shade=http://nexus.volmit.com/content/repositories/shadowrealm/io/shadowrealm/Shade/production/Shade-production-shaded.jar");
}
