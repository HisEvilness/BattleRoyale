package com.hotmail.AdrianSR.BattleRoyale.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.StandBlockFace;
import com.hotmail.adriansr.core.util.math.LocationUtil;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;

public class LocUtils extends LocationUtil {
	
	/**
	 * Check if a player is on the lobby map.
	 * 
	 * @param p the player to check.
	 * @return true if is on the lobby map.
	 */
	public static boolean isOnLobby(final Player p) {
		return p != null && isTheLobbyWorld(p.getWorld());
	}
	
	/**
	 * Check if a player is on the battle map.
	 * 
	 * @param p the player to check.
	 * @return true if is on the battle map.
	 */
	public static boolean isOnBattleMap(final Player p) {
		return p != null && isTheBattleWorld(p.getWorld());
	}
	
	/**
	 * Check if a world is the lobby world.
	 * 
	 * @param world the world to check.
	 * @return true if is the lobby world.
	 */
	public static boolean isTheLobbyWorld(final World world) {
		return world != null && MapsManager.LOBBY_MAP != null
				&& world.getName().equals(MapsManager.LOBBY_MAP.getWorld().getName());
	}
	
	/**
	 * Check if a world is the battle world.
	 * 
	 * @param world the world to check.
	 * @return true if is the battle world.
	 */
	public static boolean isTheBattleWorld(final World world) {
		return world != null && MapsManager.BATTLE_MAP != null
				&& world.getName().equals(MapsManager.BATTLE_MAP.getWorld().getName());
	}

	public static boolean isInsideOfImaginaryCuboid(Location check, final Location center, final double radius) {
		final int h = 29999984;

		double x = Math.floor(check.getX());
//		double y = Math.floor(check.getY());
		double z = Math.floor(check.getZ());

		// get double b.
		double b = center.getX() - radius / 2.0D;
		if (b < (double) (-h)) {
			b = (double) (-h);
		}

		// get double d.
		double d = center.getX() + radius / 2.0D;
		if (d > (double) h) {
			d = (double) h;
		}

		// get double c.
		double c = center.getZ() - radius / 2.0D;
		if (c < (double) (-h)) {
			c = (double) (-h);
		}

		// get double e.
		double e = center.getZ() + radius / 2.0D;
		if (e > (double) h) {
			e = (double) h;
		}
		return (double) (x + 1) > b && (double) x < d && (double) (z + 1) > c && (double) z < e;
	}
	
	public static boolean isInsideOfBorder(final Player p, final WorldBorder border) {
		return isInsideOfBorder(p.getLocation(), border);
	}
	
	public static boolean isInsideOfBorder(final Location location, final WorldBorder border) {
		try {
			/* get world of the world border */
			final World world = border.getCenter().getWorld();
			
			/* load reflection */
			final Class<?> craft_world_border_class         = border.getClass();
//			final Class<?> nms_world_border                 = ReflectionUtils.getCraftClass("WorldBorder");
			final Class<?> block_position_class             = ClassReflection.getNmsClass ( "BlockPosition" );
			final Constructor<?> block_position_constructor = block_position_class.getConstructor(double.class, double.class, double.class);
			
			/* get instances */
			final Object craft = craft_world_border_class.cast(border);
			final Object handle = FieldUtils.readField(craft, "handle", true);
			final Object block_position = block_position_constructor.newInstance(location.getX(), location.getY(), location.getZ());
			
			// check is inside conparing world and invoking method "a".
			final Method a = handle.getClass().getMethod("a", block_position_class);
			return (location.getWorld().equals(world)) && (boolean) (a.invoke(handle, block_position));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Add a {@link StandBlockFace} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link StandBlockFace}.
	 */
	public static void add(final Location location, final StandBlockFace face) {
		add(location, face, 1);
	}
	
	/**
	 * Add a {@link StandBlockFace} mod
	 * axis to {@link Location}.
	 * <p>
	 * @param location The {@link Location}.
	 * @param face The {@link StandBlockFace}.
	 * @param num The blocks amount.
	 */
	public static void add(final Location location, final StandBlockFace face, double num) {
		location.add(face.getModX() * num, face.getModY() * num, face.getModZ() * num);
	}
}
