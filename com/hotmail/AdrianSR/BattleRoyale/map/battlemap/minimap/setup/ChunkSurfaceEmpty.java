package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 28 August, 2020 / 10:16 AM
 */
final class ChunkSurfaceEmpty extends ChunkSurface {
	
	public ChunkSurfaceEmpty ( int x , int z ) {
		super ( x , z , new int [ RenderConstants.CHUNK_X_MAX * RenderConstants.CHUNK_Z_MAX ] );
	}
}