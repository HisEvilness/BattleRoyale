package com.hotmail.AdrianSR.BattleRoyale.util;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Friday 28 August, 2020 / 09:13 AM
 */
public class RenderConstants {
	
	public static final String LEVEL_HEIGHTMAP = ".Level.HeightMap";
	public static final String  LEVEL_SECTIONS = ".Level.Sections";

	/** chunk width */
	public static final int CHUNK_X_MAX = 16;
	/** chunk height */
	public static final int CHUNK_Y_MAX = 256;
	/** chunk depth */
	public static final int CHUNK_Z_MAX = 16;
	
	public static final int        CHUNK_SECTION_Y_MAX = 16;
	public static final int        CHUNK_SECTION_BYTES = CHUNK_X_MAX * CHUNK_SECTION_Y_MAX * CHUNK_Z_MAX;
	public static final int CHUNK_SECTION_HALF_NIBBLES = CHUNK_SECTION_BYTES / 2;
	public static final int                CHUNK_BYTES = CHUNK_X_MAX * CHUNK_Y_MAX * CHUNK_Z_MAX;
	
	/**
	 * Default sea level. Used to be 64.
	 */
	public static final int WORLD_SEA_LEVEL = 63;
}