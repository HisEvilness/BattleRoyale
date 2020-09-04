package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup;

/**
 * Represents a chunk location.
 * <p>
 * @author AdrianSR / Saturday 29 August, 2020 / 02:14 PM
 */
public class ChunkLocation {

	protected final int x;
	protected final int z;
	
	protected final int hash;
	
	public ChunkLocation ( int x , int z ) {
		this.x = x;
		this.z = z;
		
		// we're hashing in construction and caching it to make process faster.
		this.hash = ( 1664525 * this.x + 1013904223 ) ^ ( 1664525 * ( this.z ^ -559038737 ) + 1013904223 );
	}
	
	public int getX ( ) {
		return x;
	}

	public int getZ ( ) {
		return z;
	}

	@Override
	public String toString ( ) {
		return "[" + x + ", " + z + "]";
	}

	@Override
	public int hashCode ( ) {
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		} else {
			if ( obj instanceof ChunkLocation ) {
				ChunkLocation other = (ChunkLocation) obj;
				return other.x == this.x && other.z == this.z;
			} else { 
				return false;
			}
		}
	}
}