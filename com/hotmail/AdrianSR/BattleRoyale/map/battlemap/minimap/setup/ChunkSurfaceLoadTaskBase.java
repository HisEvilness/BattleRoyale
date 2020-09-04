package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.awt.Color;
import java.io.DataInputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;

import se.llbit.chunky.world.Heightmap;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Monday 31 August, 2020 / 06:42 PM
 */
abstract class ChunkSurfaceLoadTaskBase extends ChunkSurfaceLoadTask {

	protected byte [ ] blocks;
	protected byte [ ]  datas;
	
	public ChunkSurfaceLoadTaskBase ( int x , int z , DataInputStream input , Heightmap heightmap ) {
		super ( x , z , input , heightmap );
		
		this.blocks = new byte [ RenderConstants.CHUNK_BYTES ];
		this.datas  = new byte [ RenderConstants.CHUNK_BYTES ];
	}

	@Override
	public ChunkSurface call ( ) throws Exception {
		if ( parseBlocksDatasHeightmap ( ) ) {
			// here we're loading the surface by finding the topmost non-empty block of each
			// row/column
			int [ ] surface_colors = new int [ RenderConstants.CHUNK_X_MAX * RenderConstants.CHUNK_Z_MAX ];
			for ( int x = 0 ; x < RenderConstants.CHUNK_X_MAX ; x ++ ) {
				for ( int z = 0 ; z < RenderConstants.CHUNK_Z_MAX ; z ++ ) {
					// finding topmost non-empty block.
					int y = RenderConstants.CHUNK_Y_MAX - 1;
					for ( ; y > 0 ; y -- ) {
						if ( getColor ( x , y , z ) != BlockColor.AIR ) {
							break;
						}
					}
					
					BlockColor block_color = getColor ( x , y , z );
					if ( block_color == BlockColor.AIR ) {
						// transparency
						surface_colors [ x + z * 16 ] = 0;
						continue;
					}
					
					if ( block_color == BlockColor.WATER ) {
						// here we're implementing a depth effect on water surfaces.
						int depth = 1;
						y        -= 1;
						for ( ; y >= 0 ; y -- ) {
							if ( getColor ( x , y , z ) != BlockColor.WATER ) {
								break;
							}
							
							depth += 1;
						}
						
						float      alpha = Math.max ( 0.0F , Math.min ( depth / 32.0F , 1.0F ) );
						float [ ] colors = block_color.getColor ( ).getRGBComponents ( null );
						
						Color background = new Color ( 100 / 255 , 0.8F , 1.0F );
						Color      color = new Color ( colors [ 0 ] , colors [ 1 ] , colors [ 2 ] , alpha );
						
						int r = color.getRed ( )   * color.getAlpha ( ) + background.getRed ( )   * ( 255 - color.getAlpha ( ) );
						int g = color.getGreen ( ) * color.getAlpha ( ) + background.getGreen ( ) * ( 255 - color.getAlpha ( ) );
						int b = color.getBlue ( )  * color.getAlpha ( ) + background.getBlue ( )  * ( 255 - color.getAlpha ( ) );
						
						float [ ] blend = blend ( 
								new float [ ] { r / 255 , g / 255 , b / 255 , 1.0F } , 
								new float [ ] { colors [ 0 ] , colors [ 1 ] , colors [ 2 ] , alpha } );
						
						surface_colors [ x + z * 16 ] = new Color ( blend [ 0 ] / 255 , blend [ 1 ] / 255 , blend [ 2 ] / 255 , blend [ 3 ] ).getRGB ( );
					} else {
						surface_colors [ x + z * 16 ] = block_color.getColor ( ).getRGB ( );
					}
				}
			}
			
			return new ChunkSurface ( x , z , surface_colors );
		} else {
			return new ChunkSurfaceEmpty ( x , z );
		}
	}
	
	/**
	 * Parses the blocks, datas, and the heightmap from {@link #input}.
	 * <p>
	 * @return true if parsed successfully.
	 */
	protected boolean parseBlocksDatasHeightmap ( ) {
		// this could seem unnecessary and redundant, but the NBT system we're using
		// clears the request set, the it necessary to create it when parsing.
		Set < String > request = new HashSet < > ( );
		request.add ( RenderConstants.LEVEL_HEIGHTMAP );
		request.add ( RenderConstants.LEVEL_SECTIONS );
		
		Map < String , Tag > result = parse ( request );
		
		Tag  sections = result.get ( RenderConstants.LEVEL_SECTIONS );
		Tag heightmap = result.get ( RenderConstants.LEVEL_HEIGHTMAP );
		
		if ( sections != null && sections.isList ( ) ) {
			// here we're loading all blocks within the chunk.
			for ( SpecificTag section : ( (ListTag) sections ) ) {
				int   y_offset = section.get ( "Y" ).byteValue ( ) & 0xFF;
				Tag blocks_tag = section.get ( "Blocks" );
				Tag   data_tag = section.get ( "Data" );
				
				// loading blocks.
				if ( blocks_tag.isByteArray ( RenderConstants.CHUNK_SECTION_BYTES ) ) {
					System.arraycopy ( blocks_tag.byteArray ( ) , 0 , blocks , 
							RenderConstants.CHUNK_SECTION_BYTES * y_offset , RenderConstants.CHUNK_SECTION_BYTES );
				}
				
				// loading datas.
				if ( data_tag.isByteArray ( RenderConstants.CHUNK_SECTION_HALF_NIBBLES ) ) {
					System.arraycopy ( data_tag.byteArray ( ) , 0 , datas , 
							RenderConstants.CHUNK_SECTION_HALF_NIBBLES * y_offset , RenderConstants.CHUNK_SECTION_HALF_NIBBLES );
				}
			}
			
			// here we're loading the chunk height map.
			if ( heightmap != null && heightmap.isIntArray ( RenderConstants.CHUNK_X_MAX * RenderConstants.CHUNK_Z_MAX ) ) {
				updateHeightMap ( blocks , heightmap.intArray ( ) );
			} else {
				updateHeightMap ( blocks , null );
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected int getId ( int x , int y , int z ) {
		return 0xFF & blocks [ index ( x , y , z ) ];
	}

	@Override
	protected int getData ( int x , int y , int z ) {
		int                  index = index ( x , y , z );
		byte unfiltered_block_data = datas [ index / 2 ];
		
		return index % 2 != 0 ? ( unfiltered_block_data >> 4 ) & 0xF : ( unfiltered_block_data ) & 0xF;
	}
	
	protected int index ( int x , int y , int z ) {
		return x + RenderConstants.CHUNK_X_MAX * ( z + RenderConstants.CHUNK_Z_MAX * y );
	}

	@Override
	protected boolean isAir ( int x , int y , int z ) {
		return getColor ( x , y , z ) == BlockColor.AIR;
	}

	@Override
	protected boolean isWater ( int x , int y , int z ) {
		return getColor ( x , y , z ) == BlockColor.WATER;
	}
}