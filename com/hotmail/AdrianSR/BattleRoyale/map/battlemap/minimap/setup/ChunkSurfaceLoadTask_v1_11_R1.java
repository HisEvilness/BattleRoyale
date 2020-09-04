package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.io.DataInputStream;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.MaterialMapColor;
import se.llbit.chunky.world.Heightmap;

/**
 * Chunk surface loader implementation for version v1_11_R1.
 * <p>
 * @author AdrianSR / Friday 04 September, 2020 / 08:57 AM
 */
class ChunkSurfaceLoadTask_v1_11_R1 extends ChunkSurfaceLoadTaskBase {
	
	public ChunkSurfaceLoadTask_v1_11_R1 ( int x , int z , DataInputStream input , Heightmap heightmap ) {
		super ( x , z, input , heightmap );
	}

	@Override @SuppressWarnings ( "deprecation" )
	protected BlockColor getColor ( int x , int y , int z ) {
		Block                block = Block.getById ( getId ( x , y , z ) );
		MaterialMapColor nms_color = block.fromLegacyData ( getData ( x , y , z ) ).g ( );
		
		return BlockColor.getById ( nms_color.M ).orElse ( BlockColor.AIR );
	}	
}