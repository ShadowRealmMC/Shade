package io.shadowrealm.shade.server;

import mortar.lang.collection.GList;

public class ServerConfig
{
	public static boolean AUTO_RESTART__ENABLED = true;
	public static String AUTO_RESTART__TARGET_HOUR_EST = "3:30 AM";
	public static long SHOUTING_INTERVAL = 10000;
	public static long RANKING__CYCLE_TIME__VALUE = 1;
	public static String RANKING__CYCLE_TIME__UNIT = "DAYS";
	public static double RANKING__OVERBLEED = 0.25;
	public static double RANKING__RETAIN = 0.45;
	public static int WEBSERVER__SERVER_PORT = 15751;
	public static int WEBSERVER__CLIENT_PORT_POOL__MIN = 15752;
	public static int WEBSERVER__CLIENT_PORT_POOL__MAX = 15952;
	public static boolean WEBSERVER__CONNECTION_DEBUGGING = true;
	public static boolean DATABASE__LOG_SQL = true;
	public static String DATABASE__NAME = "shade";
	public static String DATABASE__USER = "root";
	public static String DATABASE__PASSWORD = ".";
	public static String DATABASE__ADDRESS = "localhost";
	public static String DATABASE__TABLE_PREFIX = "shade_";
	public static GList<String> PORT_OVERRIDES = new GList<String>().qadd("serverid=15752");
	public static boolean DOWNLOAD_UPDATES = true;
	public static GList<String> UPDATE = new GList<String>().qadd("Shade=http://nexus.volmit.com/content/repositories/shadowrealm/io/shadowrealm/Shade/production/Shade-production-shaded.jar");
}
