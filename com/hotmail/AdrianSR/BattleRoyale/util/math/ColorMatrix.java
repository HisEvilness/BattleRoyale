package com.hotmail.AdrianSR.BattleRoyale.util.math;

import java.awt.Color;

/**
 * Represents a matrix of custom capacity for storing colors in sRGB color
 * model.
 * <p>
 * @author AdrianSR / Tuesday 01 September, 2020 / 12:14 PM
 */
public class ColorMatrix {
	
	public    final int   capacity;
	protected final int [ ] values;
	
	public ColorMatrix ( int capacity ) {
		this.capacity  = capacity;
		this.values    = new int [ capacity * capacity ];
	}
	
	public int [ ] getValues ( ) {
		return values;
	}
	
	public int get ( int column , int row ) {
		return values [ column + row * capacity ];
	}
	
	public Color getColor ( int column , int row ) {
		return new Color ( get ( column , row ) );
	}
	
	public void set ( int column , int row , int rgb ) {
		values [ column + row * capacity ] = rgb;
	}
	
	public void setColor ( int column , int row , Color color ) {
		set ( column , row , color.getRGB ( ) );
	}
	
	public void fill ( int rgb ) {
		for ( int x = 0 ; x < capacity ; x ++ ) {
			for ( int z = 0 ; z < capacity ; z ++ ) {
				set ( x , z , rgb );
			}
		}
	}
}