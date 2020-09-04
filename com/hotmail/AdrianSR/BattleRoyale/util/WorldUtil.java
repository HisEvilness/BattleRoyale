package com.hotmail.AdrianSR.BattleRoyale.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldUtil {

	/**
	 * 
	 * @param world
	 * @param x
	 * @param z
	 * @return the highest or null.
	 */
	public static Block getHighestSolidBlockAt ( World world , int x , int z ) {
		for ( int y = world.getMaxHeight ( ) - 1 ; y >= 0 ; y -- ) {
			Block block = world.getBlockAt ( x , y , z );
			if ( block.getType ( ).isSolid ( ) ) {
				return block;
			}
		}
		return null;
	}
	
	public static Block getHighestSolidBlockAt ( Location location ) {
		return getHighestSolidBlockAt ( location.getWorld ( ) , 
				Location.locToBlock ( location.getX ( ) ) , 
				Location.locToBlock ( location.getZ ( ) ) );
	}
}
