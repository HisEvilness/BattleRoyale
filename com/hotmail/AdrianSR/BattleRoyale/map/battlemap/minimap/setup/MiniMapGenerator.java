package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.lang.Validate;

import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.MiniMap;
import com.hotmail.AdrianSR.BattleRoyale.util.RenderConstants;
import com.hotmail.AdrianSR.BattleRoyale.util.math.ColorMatrix;
import com.hotmail.adriansr.core.util.server.Version;

import se.llbit.chunky.world.Heightmap;
import se.llbit.math.ColorUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Monday 31 August, 2020 / 05:54 PM
 */
public class MiniMapGenerator {
	
	/**
	 * Sector size in bytes.
	 */
	protected final static int SECTOR_SIZE = 4096;
	
	/**
	 * The package that holds the different versions of {@link ChunkSurfaceLoadTask}.
	 */
	protected static final String VERSIONS_PACKAGE = "com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup.";
	
	private final Stack < DataInputStream > inputs = new Stack < > ( );
	
	protected final File        world_folder;
	protected final ExecutorService executor;
	
	protected final Heightmap heightmap = new Heightmap ( );
	
	public MiniMapGenerator ( File world_folder ) {
		this.world_folder = world_folder;
		this.executor     = Executors.newWorkStealingPool ( );
	}
	
	/**
	 * Renders the  region represented by the provided {@code area}.
	 * <p>
	 * <strong>Note that as this is a heavy process, and will block until
	 * finished.</strong>
	 * <p>
	 * @param area the area to render.
	 * @return the rendered area.
	 */
	public MiniMap generate ( Area area ) {
		areaCheck ( area );
		
		// getXSize and getZSize are the same for us because the area is squared.
		ColorMatrix colors = new ColorMatrix ( area.getXSize ( ) );
		
		Vector3i min = area.getMin ( );
		Vector3i max = area.getMax ( );
		
		ChunkLocation chunk_min = new ChunkLocation ( min.getX ( ) >> 4 , min.getZ ( ) >> 4 );
		ChunkLocation chunk_max = new ChunkLocation ( max.getX ( ) >> 4 , max.getZ ( ) >> 4 );
		
		Set < ChunkLocation > request = new HashSet < > ( );
		for ( int x = chunk_min.getX ( ) ; x <= chunk_max.getX ( ) ; x ++ ) {
			for ( int z = chunk_min.getZ ( ) ; z <= chunk_max.getZ ( ) ; z ++ ) {
				request.add ( new ChunkLocation ( x , z ) );
			}
		}
		
//		System.out.println ( ">>>>>> request size: " + request.size ( ) );
		
		try {
			Map < ChunkLocation , ChunkSurface > surface_map = load ( request );
//			System.out.println ( ">>>>>> loaded surfaces: " + surface_map.size ( ) );
			
			synchronized  ( surface_map ) {
				// getXSize and getZSize are the same for us because the area is squared.
				int size = area.getXSize ( );
				
				for ( int x = 0 ; x < size ; x ++ ) {
					for ( int z = 0 ; z < size ; z ++ ) {
						int block_x = min.getX ( ) + x;
						int block_z = min.getZ ( ) + z;
						int chunk_x = block_x >> 4;
						int chunk_z = block_z >> 4;
				
						ChunkSurface surface = surface_map.get ( new ChunkLocation ( chunk_x , chunk_z ) );
						if ( surface != null ) {
							colors.set ( x , z , surface.getColor ( block_x & 15 , block_z & 15 ) );
						}
					}
				}
			}
		} catch ( InterruptedException ex ) {
			ex.printStackTrace ( );
		}
		
		dispose ( );
		return new MiniMap ( colors );
	}
	
	protected synchronized Map < ChunkLocation , ChunkSurface > load ( Collection < ChunkLocation > chunks ) throws InterruptedException {
		final List < ChunkSurfaceLoadTask > loaders = new ArrayList < > ( );
		chunks.forEach ( chunk -> {
			DataInputStream input = getChunkInputStream ( chunk );
			if ( input != null ) {
				loaders.add ( createLoadTask ( chunk.getX ( ) , chunk.getZ ( ) , inputs.push ( input ) , heightmap ) );
			}
		});
		
		Map < ChunkLocation , ChunkSurface > map = new HashMap < > ( );
		List < Future < ChunkSurface > > surfaces = executor.invokeAll ( loaders );
		for ( Future < ChunkSurface > future : surfaces ) {
			try {
				ChunkSurface   surface = future.get ( );
				ChunkLocation location = new ChunkLocation ( surface.x , surface.z );
				
				calculateGradient ( location , surface , heightmap );
				map.put ( location , surface );
			} catch ( ExecutionException ex ) {
				ex.printStackTrace ( );
			}
		}
		return map;
	}
	
	protected ChunkSurfaceLoadTask createLoadTask ( int x , int z , DataInputStream input , Heightmap heightmap ) {
		try {
			Class < ? extends ChunkSurfaceLoadTask > corresponding_class = Class
					.forName ( VERSIONS_PACKAGE + "ChunkSurfaceLoadTask" + "_" + Version.getServerVersion ( ).name ( ) )
					.asSubclass ( ChunkSurfaceLoadTask.class );
			
			return corresponding_class.getConstructor ( int.class , int.class , DataInputStream.class , Heightmap.class )
					.newInstance ( x , z , input , heightmap );
		} catch ( ClassNotFoundException ex ) {
			throw new UnsupportedOperationException ( "unsupported server version!" );
		} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex_b ) {
			ex_b.printStackTrace();
		}
		return null;
	}
	
	protected synchronized void dispose ( ) {
		executor.shutdown ( );
		
		while ( !inputs.isEmpty ( ) ) {
			try {
				DataInputStream input = inputs.pop ( );
				if ( input != null ) {
					input.close ( );
				}
			} catch ( IOException ex ) {
				ex.printStackTrace ( );
			}
		}
	}
	
	protected void areaCheck ( Area area ) {
		Validate.notNull ( area , "area cannot be null!" );
		Validate.isTrue ( area.isSquared ( ) , "area must be squared!" );
		Validate.isTrue ( Math.max ( area.getXSize ( ) , area.getZSize ( ) ) >= 2 , "area is too small!" );
	}
	
	protected synchronized DataInputStream getChunkInputStream ( ChunkLocation location ) {
		return getChunkInputStream ( getRegionFile ( location ) , location );
	}
	
	protected synchronized File getRegionFile ( ChunkLocation location ) {
		File     region_folder = new File ( world_folder , "region" );
		String region_filename = "r." + ( location.getX ( ) >> 5 ) + "." + ( location.getZ ( ) >> 5 ) + ".mca";
		
		return new File ( region_folder , region_filename );
	}
	
	private static synchronized void calculateGradient ( ChunkLocation location , ChunkSurface surface , Heightmap heightmap ) {
		int cx = location.getX ( ) * RenderConstants.CHUNK_X_MAX;
		int cz = location.getZ ( ) * RenderConstants.CHUNK_Z_MAX;

		float [ ] rgb = new float [ 3 ];
		for ( int x = 0 ; x < 16 ; x ++ ) {
			for ( int z = 0 ; z < 16 ; z ++ ) {
				if ( surface.value ( ) [ x + z * 16 ] == 0 ) {
					continue;
				}

				Color color = new Color ( surface.getColor ( x , z ) );
				ColorUtil.getRGBComponents ( ColorUtil.getArgb ( (float) color.getRed ( ) / 255.F , 
						(float) color.getGreen ( ) / 255.F , (float) color.getBlue ( ) / 255.F , 1.0F ) , rgb );

				float gradient = ( heightmap.get ( cx + x, cz + z ) + heightmap.get ( cx + x + 1, cz + z )
						+ heightmap.get ( cx + x , cz + z + 1 ) - heightmap.get ( cx + x - 1, cz + z )
						- heightmap.get ( cx + x , cz + z - 1 ) - heightmap.get ( cx + x - 1, cz + z - 1 ) );
				gradient = (float) ( ( Math.atan ( gradient / 15 ) / ( Math.PI / 1.7 ) ) + 1 );

				rgb [ 0 ] *= gradient;
				rgb [ 1 ] *= gradient;
				rgb [ 2 ] *= gradient;

				// clip the result
				rgb [ 0 ] = Math.max ( 0.0F , rgb [ 0 ] );
				rgb [ 0 ] = Math.min ( 1.0F , rgb [ 0 ] );
				rgb [ 1 ] = Math.max ( 0.0F , rgb [ 1 ] );
				rgb [ 1 ] = Math.min ( 1.0F , rgb [ 1 ] );
				rgb [ 2 ] = Math.max ( 0.0F , rgb [ 2 ] );
				rgb [ 2 ] = Math.min ( 1.0F , rgb [ 2 ] );

				surface.value ( ) [ x + z * 16 ] = ColorUtil.getRGB ( rgb [ 0 ] , rgb [ 1 ] , rgb [ 2 ] );
			}
		}
	}
	
	/**
	 * Read chunk data from region file.
	 *
	 * @return {@code null} if the chunk could not be loaded
	 */
	private static synchronized DataInputStream getChunkInputStream ( File regionFile , ChunkLocation chunkPos ) {
		int x = chunkPos.x & 31;
		int z = chunkPos.z & 31;
		int index = x + z * 32;
		try ( RandomAccessFile file = new RandomAccessFile ( regionFile , "r" ) ) {
			long length = file.length();
			if (length < 2 * SECTOR_SIZE) {
//				System.out.println ( "Missing header in region file!" );
				return null;
			}
			
			file.seek(4 * index);
			
			int          loc = file.readInt ( );
			int   numSectors = loc & 0xFF;
			int sectorOffset = loc >> 8;
			
			file.seek ( SECTOR_SIZE + 4 * index );
			
			// skipping the time-stamp
			file.readInt ( );
			if ( length <  ( sectorOffset + numSectors ) * SECTOR_SIZE ) {
//				System.err.println("Chunk is outside region file!");
				return null;
			}
			
			file.seek ( sectorOffset * SECTOR_SIZE );

			int chunkSize = file.readInt();

			if (chunkSize > numSectors * SECTOR_SIZE) {
//				System.err.println("Error: chunk length does not fit in allocated sectors!");
				return null;
			}

			byte type = file.readByte();
			if (type != 1 && type != 2) {
//				System.err.println("Error: unknown chunk data compression method: " + type + "!");
				return null;
			}
			
			byte[] buf = new byte[chunkSize - 1];
			file.read(buf);
			
			ByteArrayInputStream in = new ByteArrayInputStream(buf);
			if (type == 1) {
				return new DataInputStream ( new GZIPInputStream ( in ) );
			} else {
				return new DataInputStream ( new InflaterInputStream ( in ) );
			}
		} catch ( IOException ex ) {
//			System.err.println("Failed to read chunk: " + e.getMessage());
			return null;
		}
	}
}