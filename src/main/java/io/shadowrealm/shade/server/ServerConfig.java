package io.shadowrealm.shade.server;

public class ServerConfig
{
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
}
