package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Sunday 30 August, 2020 / 03:07 PM
 */
enum BlockColor {

	AIR ( 0, 0),
	GRASS ( 1, 8368696 ),
	SAND ( 2, 16247203 ),
	CLOTH ( 3, 13092807 ),
	TNT ( 4, 16711680 ),
	ICE ( 5, 10526975 ),
	IRON ( 6, 10987431 ),
	FOLIAGE ( 7, 31744 ),
	SNOW ( 8, 16777215 ),
	CLAY ( 9, 10791096 ),
	DIRT ( 10, 9923917 ),
	STONE ( 11, 7368816 ),
	WATER ( 12, 4210943 ),
	WOOD ( 13, 9402184),
	QUARTZ ( 14, 16776437),
	ADOBE ( 15, 14188339),
	MAGENTA ( 16, 11685080),
	LIGHT_BLUE ( 17, 6724056),
	YELLOW ( 18, 15066419),
	LIME ( 19, 8375321),
	PINK ( 20, 15892389),
	GRAY ( 21, 5000268),
	SILVER ( 22, 10066329),
	CYAN ( 23, 5013401),
	PURPLE ( 24, 8339378),
	BLUE ( 25, 3361970),
	BROWN ( 26, 6704179),
	GREEN ( 27, 6717235),
	RED ( 28, 10040115),
	BLACK ( 29, 1644825),
	GOLD ( 30, 16445005),
	DIAMOND ( 31, 6085589),
	LAPIS ( 32, 4882687),
	EMERALD ( 33, 55610),
	OBSIDIAN ( 34, 8476209),
	NETHERRACK ( 35, 7340544),
	WHITE_STAINED_HARDENED_CLAY ( 36, 13742497),
	ORANGE_STAINED_HARDENED_CLAY ( 37, 10441252),
	MAGENTA_STAINED_HARDENED_CLAY ( 38, 9787244),
	LIGHT_BLUE_STAINED_HARDENED_CLAY ( 39, 7367818),
	YELLOW_STAINED_HARDENED_CLAY ( 40, 12223780),
	LIME_STAINED_HARDENED_CLAY ( 41, 6780213),
	PINK_STAINED_HARDENED_CLAY ( 42, 10505550),
	GRAY_STAINED_HARDENED_CLAY ( 43, 3746083),
	SILVER_STAINED_HARDENED_CLAY ( 44, 8874850),
	CYAN_STAINED_HARDENED_CLAY ( 45, 5725276),
	PURPLE_STAINED_HARDENED_CLAY ( 46, 8014168),
	BLUE_STAINED_HARDENED_CLAY ( 47, 4996700),
	BROWN_STAINED_HARDENED_CLAY ( 48, 4993571),
	GREEN_STAINED_HARDENED_CLAY ( 49, 5001770),
	RED_STAINED_HARDENED_CLAY ( 50, 9321518),
	BLACK_STAINED_HARDENED_CLAY ( 51, 2430480);

	private int      id;
	private Color color;

	BlockColor ( int id , int value ) {
		this.id = id;
		
		int r      = ( value & 16711680 ) >> 16;
        int g      = ( value & '\uff00' ) >> 8;
        int b      = ( value & 255 ) >> 0;
		this.color = new Color ( (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F );
	}

	public int getId ( ) {
		return id;
	}

	public Color getColor ( ) {
		return color;
	}
	
	public static Optional < BlockColor > getById ( int id ) {
		return Arrays.stream ( values ( ) ).filter ( target -> target.id == id ).findAny ( );
	}
}