package com.hotmail.AdrianSR.BattleRoyale.events;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * An enum to specify the 
 * cause of the Battle Royale 
 * Player deaths.
 * <p>
 * @author AdrianSR
 */
public enum DeathCause {

	/**
	 * Death Caused when the
	 * player is killed
	 * by other player.
	 */
	KILLED_BY_PLAYER(0),
	
	/**
	 * Death Caused when the
	 * player is killed by
	 * a projectile.
	 */
	KILLED_BY_PROJECTILE(1),
	
	/**
	 * Death Caused when the
	 * player is killed by
	 * a explosion.
	 */
	KILLED_BY_EXPLOSION(2),
	
	/**
	 * Death Caused when the
	 * player dies by a fall.
	 */
	FALL(3),
	
	/**
	 * Death Caused when the
	 * player is out of world
	 * border, and the radiation
	 * kill it.
	 */
	RADIATION(4),
	
	/**
	 * Death Caused when the
	 * player die because was
	 * bleeding out.
	 */
	BLEEDING_OUT(5),
	
	/**
	 * Death Caused when the
	 * player is killed by
	 * the void.
	 */
	VOID(6),
	
	/**
	 * Unknown Death Cause.
	 */
	UNKNOWN(7);
	
	/**
	 * Global class values.
	 */
	private final static Map<Integer, DeathCause> BY_ID = Maps.newHashMap();
	
	/**
	 * Class value.
	 */
	private final int value;
	
	/**
	 * Construct a new
	 * Death Cause.
	 * <p>
	 * @param value the value to get by ID.
	 */
	DeathCause(final int value) {
		this.value = value;
	}
	
	/**
	 * Gets the mode value associated 
	 * with this DeathCause.
	 * <p>
	 * @return An integer value of 
	 * this DeathCause.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Gets the DeathCause represented 
	 * by the specified value.
	 * <p>
	 * @param value Value to check
	 * @return Associative {@link DeathCause} 
	 * with the given value, or null if it
	 * doesn't exist.
	 */
	public static DeathCause getByValue(final int value) {
		return BY_ID.get(value);
	}
	
	/**
	 * Build "BY_ID" map.
	 */
	static {
		for (DeathCause cause : values()) {
			BY_ID.put(cause.getValue(), cause);
		}
	}
}
