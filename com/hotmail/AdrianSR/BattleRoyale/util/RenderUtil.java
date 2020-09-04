package com.hotmail.AdrianSR.BattleRoyale.util;

import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

/**
 * Represents a Minimap Renderer Utils class.
 * 
 * @author AdrianSR
 */
public class RenderUtil {

	/**
	 * Gets the capacity of the specified colors array.
	 * <p>
	 * @param colors the colors array to get.
	 * @return the capacity of the specified colors array.
	 */
	public static int capacity ( int [ ] colors ) {
		return (int) Math.floor ( Math.sqrt ( colors.length ) );
	}
	
	/**
	 * Get get size of from a Map View scale.
	 * 
	 * @param scale
	 *            the MapView.Scale
	 * @return the scale size.
	 */
	public static int getScaleSize(MapView.Scale scale) {
		if (scale.equals(MapView.Scale.CLOSEST)) {
			return 1;
		} else if (scale.equals(MapView.Scale.CLOSE)) {
			return 2;
		} else if (scale.equals(MapView.Scale.NORMAL)) {
			return 4;
		} else if (scale.equals(MapView.Scale.FAR)) {
			return 8;
		} else if (scale.equals(MapView.Scale.FARTHEST)) {
			return 16;
		}
		return 0;
	}

	/**
	 * Get a MapView scale value.
	 * 
	 * @param scale the MapView scale.
	 * @param fitTo0 if is true, and the values is 0, this methor will return 1.
	 * @return the scale value.
	 */
	@SuppressWarnings("deprecation")
	public static int getScaleValue(MapView.Scale scale, boolean fitTo0) {
		return fitTo0 ? Math.max((int) scale.getValue(), 1) : (int) scale.getValue();
	}
	
	public static int getScaleSize2(MapView.Scale scale) {
		if (scale.equals(MapView.Scale.CLOSEST)) {
			return 1;
		} else if (scale.equals(MapView.Scale.CLOSE)) {
			return 2;
		} else if (scale.equals(MapView.Scale.NORMAL)) {
			return 2;
		} else if (scale.equals(MapView.Scale.FAR)) {
			return 2;
		} else if (scale.equals(MapView.Scale.FARTHEST)) {
			return 2;
		}
		return 0;
	}
	
	/**
	 * Clear canvas.
	 * 
	 * @param mapCanvas
	 *            the canvas
	 */
	public static void clearCanvas(MapCanvas mapCanvas) {
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				// change pixel
				mapCanvas.setPixel(i, j, (byte) 0);
			}
		}
	}

	/**
	 * Convert location to map coords.
	 * 
	 * @return [x, y, direction]
	 */
	public static int[] locationToCoords(final int centerX, final int centerZ, final Location l1) {
		int distanceX = Math.min((l1.getBlockX() - centerX) / 5, 128);
		int distanceZ = Math.min((l1.getBlockZ() - centerZ) / 5, 128);
		int direction = (((int) l1.getYaw() + 360) % 360 * 16 / 360);
		return new int[] { distanceX, distanceZ, direction};
	}
	
	/**
	 * Convert location to map coords.
	 * 
	 * @return [x, y, direction]
	 */
//	public static int[] locationToCoords2(final int centerX, final int centerZ, final Location l1) {
//		int distanceX = (l1.getBlockX() - centerX);
//		int distanceZ = (l1.getBlockZ() - centerZ);
//		int direction = (((int) l1.getYaw() + 360) % 360 * 16 / 360);
//		return new int[] { distanceX, distanceZ, direction};
//	}
	
	/**
	 * @return true if is in range.
	 */
	public static boolean isInRange(Location p1, Location p2, MapView.Scale scale) {
		int scale_size = getScaleSize(scale) * 128;
		return p1.distance(p2) <= scale_size;
	}

}