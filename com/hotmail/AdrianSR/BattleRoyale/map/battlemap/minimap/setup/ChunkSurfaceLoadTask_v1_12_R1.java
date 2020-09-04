package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.io.DataInputStream;
import java.lang.reflect.Field;

import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;

import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EnumDirection;
import net.minecraft.server.v1_12_R1.IBlockAccess;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.Material;
import net.minecraft.server.v1_12_R1.MaterialMapColor;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import net.minecraft.server.v1_12_R1.TileEntity;
import se.llbit.chunky.world.Heightmap;

/**
 * Chunk surface loader implementation for version v1_12_R1.
 * <p>
 * @author AdrianSR / Monday 31 August, 2020 / 07:14 PM
 */
class ChunkSurfaceLoadTask_v1_12_R1 extends ChunkSurfaceLoadTaskBase {

	/**
	 * Our implementation of {@link IBlockAccess} for version v1_12_R1.
	 * <p>
	 * @author AdrianSR / Monday 31 August, 2020 / 12:30 AM
	 */
	private static class CustomBlockAccess implements IBlockAccess {
		
		private final byte [ ] blocks;

		private CustomBlockAccess ( byte [ ] blocks ) {
			this.blocks = blocks;
		}
		
		@Override public int getBlockPower ( BlockPosition arg0 , EnumDirection arg1 ) {
			return 0;
		}

		@Override @SuppressWarnings ( "unchecked" )
		public TileEntity getTileEntity ( BlockPosition location ) {
			Block      block = blockAt ( location );
			MinecraftKey key = Block.REGISTRY.b ( block );
			
			RegistryMaterials < MinecraftKey , Class < ? extends TileEntity > > registry = null;
			try {
				Field registry_field = FieldReflection.getAccessible ( TileEntity.class , "f" );
				registry = ( RegistryMaterials < MinecraftKey , Class < ? extends TileEntity > > ) registry_field.get ( TileEntity.class );
			} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex ) {
				ex.printStackTrace ( );
			}
			
			if ( registry != null ) {
				Class < ? extends TileEntity > clazz = registry.get ( key );
				if ( clazz != null ) {
					try {
						return clazz.newInstance ( );
					} catch ( Throwable ex ) {
						ex.printStackTrace ( );
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		@Override
		public IBlockData getType ( BlockPosition location ) {
			return blockAt ( location ).getBlockData ( );
		}

		@Override
		public boolean isEmpty ( BlockPosition location ) {
			return blockAt ( location ).getBlockData ( ).getMaterial ( ) == Material.AIR;
		}
		
		private Block blockAt ( BlockPosition location ) {
			int    index = index ( location.getX ( ) , location.getY ( ) , location.getZ ( ) );
			int block_id = 0xFF & blocks [ index ];
			
			return Block.getById ( block_id );
		}
		
		private int index ( int x , int y , int z ) {
			return x + RenderConstants.CHUNK_X_MAX * ( z + RenderConstants.CHUNK_Z_MAX * y );
		}
	}

	public ChunkSurfaceLoadTask_v1_12_R1 ( int x , int z , DataInputStream input , Heightmap heightmap ) {
		super ( x , z, input , heightmap );
	}

	@Override @SuppressWarnings ( "deprecation" )
	protected BlockColor getColor ( int x , int y , int z ) {
		Block                block = Block.getById ( getId ( x , y , z ) );
		MaterialMapColor nms_color = block.fromLegacyData ( getData ( x , y , z ) )
				.a ( new CustomBlockAccess ( blocks ) , new BlockPosition ( x , y , z ) );
		
		return BlockColor.getById ( nms_color.ad ).orElse ( BlockColor.AIR );
	}
}