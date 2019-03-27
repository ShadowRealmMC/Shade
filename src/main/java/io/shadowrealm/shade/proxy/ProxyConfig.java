package io.shadowrealm.shade.proxy;

import mortar.api.config.Key;

public class ProxyConfig
{
	@Key("database.username")
	public static String dbUser = "roberto";

	@Key("database.prefix")
	public static String dbPrefix = "shade_";

	@Key("database.database")
	public static String db = "shadow";

	@Key("database.password")
	public static String dbPass = "booswift";

	@Key("database.address")
	public static String dbAddress = "localhost";

	@Key("database.table-cache-limit")
	public static int tableCacheSize = 1024;

	@Key("webserver.access-password")
	public static String accessPassword = "3zhrcS4YTPYJfY92sR7QX8SD7FGxrqCXS8bBSjj7hhDjGarg6QAsvPz";

	@Key("webserver.port")
	public static int wsPort = 23745;

	@Key("realm.rank.rank-average-radius")
	public static int rankRadius = 7;

	@Key("realm.rank.amplifier.rank-amp-ceil")
	public static int rankAmpCeiling = 10;

	@Key("realm.rank.amplifier.rank-amp-floor")
	public static int rankAmpFloor = 5;

	public static int SHADOW_PLAYER__RANK__AVERAGE_RADIUS = 7;
	public static double SHADOW_PLAYER__RANK__AMPLIFIER_CEILING = 10;
	public static double SHADOW_PLAYER__RANK__AMPLIFIER_FLOOR = 5;
	public static int SHADOW_PLAYER__RANK__MAXIMUM = 5000;
	public static int SHADOW_PLAYER__RANK__MINIMUM = -5000;
}
