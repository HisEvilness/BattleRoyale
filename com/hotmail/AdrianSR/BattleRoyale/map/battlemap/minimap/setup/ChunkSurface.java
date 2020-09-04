package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.awt.image.ColorModel;

import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 28 August, 2020 / 09:03 AM
 */
class ChunkSurface {
	
	protected final int x;
	protected final int z;
	
	/** surface RGB colors within a {@link RenderConstants#CHUNK_X_MAX} x {@link RenderConstants#CHUNK_Z_MAX} array*/
	protected final int [ ] colors = new int [ RenderConstants.CHUNK_X_MAX * RenderConstants.CHUNK_Z_MAX ];

	/**
	 * 
	 * @param x
	 * @param z
	 * @param colors {@link RenderConstants#CHUNK_X_MAX} x {@link RenderConstants#CHUNK_Z_MAX} sRGB colors array.
	 */
	public ChunkSurface ( int x , int z , int [ ] colors ) {
		this.x = x;
		this.z = z;
		
		// copying colors from specified array.
		if ( colors.length == this.colors.length ) {
			System.arraycopy ( colors , 0 , this.colors , 0 , this.colors.length );
		} else {
			throw new IllegalArgumentException ( "colors array doesn't match required size of " + this.colors.length + " (" 
					+ RenderConstants.CHUNK_X_MAX + " x " + RenderConstants.CHUNK_Z_MAX + ")!");
		}
	}

	public int getX ( ) {
		return x;
	}

	public int getZ ( ) {
		return z;
	}

	public int [ ] value ( ) {
		return colors;
	}
	
	/**
	 * Gets the surface color at the specified relative coordinates <strong>x, z</strong>.
	 * <p>
	 * @param x the block x relative to chunk ( 0 - 15 )
	 * @param z the block z relative to chunk ( 0 - 15 )
	 * @return the color in default sRGB {@link ColorModel}.
	 */
	public int getColor ( int x , int z ) {
		return colors [ x + z * 16 ];
	}
}