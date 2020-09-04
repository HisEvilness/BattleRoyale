package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.io.DataInputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;

import se.llbit.chunky.world.Heightmap;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Monday 31 August, 2020 / 06:09 PM
 */
abstract class ChunkSurfaceLoadTask implements Callable < ChunkSurface > {
	
	protected final int                 x;
	protected final int                 z;
	protected final DataInputStream input;
	protected final Heightmap   heightmap;
	
	public ChunkSurfaceLoadTask ( int x , int z , DataInputStream input , Heightmap heightmap ) {
		this.x         = x;
		this.z         = z;
		this.input     = input;
		this.heightmap = heightmap;
	}
	
	/**
	 * Gets the id of the block at the specified location.
	 * <p>
	 * @param x the x location.
	 * @param y the y location.
	 * @param z the z location.
	 * @return the id of the block at the provided location.
	 */
	protected abstract int getId ( int x , int y , int z );
	
	/**
	 * Gets the data of the block at the specified location.
	 * <p>
	 * @param x the x location.
	 * @param y the y location.
	 * @param z the z location.
	 * @return the data of the block at the provided location.
	 */
	protected abstract int getData ( int x , int y , int z );
	
	/**
	 * Gets the color of the block at the specified location.
	 * <p>
	 * @param x the x location.
	 * @param y the y location.
	 * @param z the z location.
	 * @return the color of the block at the provided location.
	 */
	protected abstract BlockColor getColor ( int x , int y , int z );
	
	/**
	 * Gets whether the block at the specified location is air.
	 * <p>
	 * @param x the x location.
	 * @param y the y location.
	 * @param z the z location.
	 * @return whether the block at the specified location is air.
	 */
	protected abstract boolean isAir ( int x , int y , int z );
	
	/**
	 * Gets whether the block at the specified location is water.
	 * <p>
	 * @param x the x location.
	 * @param y the y location.
	 * @param z the z location.
	 * @return whether the block at the specified location is water.
	 */
	protected abstract boolean isWater ( int x , int y , int z );
	
	/**
	 * Updates the corresponding part of the heightmap for this chunk.
	 * <p>
	 * @param blocks the array of blocks within this chunk.
	 * @param heightmap_data the parsed heightmap or null to generate it.
	 */
	protected void updateHeightMap ( byte [ ] blocks , int [ ] heightmap_data ) {
		if ( heightmap_data == null ) {
			heightmap_data = new int [ RenderConstants.CHUNK_X_MAX * RenderConstants.CHUNK_Z_MAX ];
			for ( int i = 0 ; i < heightmap_data.length ; ++ i ) {
				heightmap_data [ i ] = RenderConstants.CHUNK_Y_MAX - 1;
			}
		}
		
		for ( int x = 0 ; x < 16 ; x ++ ) {
			for ( int z = 0 ; z < 16 ; z ++ ) {
				int y = Math.max ( 1 , heightmap_data [ z * 16 + x ] - 1 );
				
				for ( ; y > 1 ; -- y ) {
					if ( !isAir ( x , y , z ) || !isWater ( x , y , z ) ) {
						break;
					}
				}
				
				this.heightmap.set ( y , this.x * 16 + x , this.z * 16 + z );
			}
		}
	}
	
	/**
	 * Parse only the required from input.
	 */
	protected Map < String , Tag > parse ( Set < String > request ) {
		return NamedTag.quickParse ( input , request );
	}

	/**
	 * Blend the two argb colors a and b. Result is stored in the array a.
	 */
	protected float [ ] blend ( float [ ] src , float [ ] dst ) {
		float[] out = new float[4];
		out[3] = src[3] + dst[3] * (1 - src[3]);
		out[0] = (src[0] * src[3] + dst[0] * dst[3] * (1 - src[3])) / out[3];
		out[1] = (src[1] * src[3] + dst[1] * dst[3] * (1 - src[3])) / out[3];
		out[2] = (src[2] * src[3] + dst[2] * dst[3] * (1 - src[3])) / out[3];
		return out;
	}
}