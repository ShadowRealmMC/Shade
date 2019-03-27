package io.shadowrealm.shade.object;

import java.util.UUID;

public interface ShadowPlayer
{
	public ShadowPlayerLevel getLevel();

	public String getName();

	public UUID getRealUUID();

	public ShadowServer getServer();

	public void assignServer(ShadowServer server);
}
