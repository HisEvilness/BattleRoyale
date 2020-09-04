package com.hotmail.AdrianSR.BattleRoyale.vehicles;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat.ACTCompatibility;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat.ACTCompatibles;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat.ACTHackType;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 18 August, 2020 / 11:02 AM
 */
public interface Vehicle extends Listener {
	
	/**
	 * All vehicle instances within a {@link Set}.
	 */
	public static final Set < Vehicle > VEHICLES = new HashSet < > ( );
	
	/**
	 * Gets the {@link Vehicle} which the provide {@code player} is the passenger.
	 * <p>
	 * @param player the player passenger.
	 * @return the vehicle player is passenger or null if player don't have a vehicle.
	 */
	public static Vehicle getVehicle ( final Player player ) {
		return VEHICLES.stream ( )
				.filter ( vehicle -> vehicle.hasPassenger ( ) && vehicle.getPassenger ( ).getUniqueId ( ).equals ( player.getUniqueId ( ) ) )
				.findAny ( ).orElse ( null );
	}
	
	/**
	 * Gets whether the provided {@code player} is the passenger of a vehicle.
	 * <p>
	 * @param player the player to check.
	 * @return true if passenger.
	 */
	public static boolean hasVehicle ( Player player ) {
		return getVehicle ( player ) != null;
	}
	
	/**
	 * Enable/disable compatible anti-hack plugin's fly anti-cheat.
	 * <p>
	 * @param check whether the fly anti-cheat is to be enabled or not.
	 */
	public static void setCheckFly ( boolean check ) {
		for ( ACTCompatibles act : ACTCompatibles.values ( ) ) {
			ACTCompatibility compatible = ACTCompatibility.of ( act );
			if ( compatible != null ) {
				if ( check ) {
					compatible.check ( ACTHackType.FLY );
				} else {
					compatible.ignore ( ACTHackType.FLY );
				}
			}
		}
	}
	
	public Player getPassenger ( );
	
	public boolean hasPassenger ( );
	
	public void join ( );
	
	public void close ( );
	
	public void destroy ( );
}