package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 01 September, 2020 / 06:18 PM
 */
public enum MiniMapZoom {
	
	NORMAL ( -1 ) ,
	
//	MINIMUM ( 128 * 2 ) ,
	
	MEDIUM ( 128 ),
	
	MAXIMUM ( 128 / 2 ),
	
	;
	
	// the smaller the display range, the greater the zoom.
	private final int range;

	MiniMapZoom ( int range ) {
		this.range = range;
	}

	public int getDisplayRange ( ) {
		return range;
	}
}