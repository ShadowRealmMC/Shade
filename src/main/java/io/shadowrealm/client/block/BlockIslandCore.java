package io.shadowrealm.client.block;

import mortar.api.fulcrum.object.CustomBlock;
import mortar.api.fulcrum.util.DefaultBlockModel;

public class BlockIslandCore extends CustomBlock
{
	public BlockIslandCore()
	{
		super("island_core");
		setModel(DefaultBlockModel.CUBE_FRAMED);
		getModel().rewrite("$id", getID());
		setTexture(getID() + "_inside", "assets/blocks/island_core_inside.png");
		setTexture(getID() + "_outside", "assets/blocks/island_core_outside.png");
	}
}
