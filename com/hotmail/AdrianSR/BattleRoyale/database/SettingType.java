package com.hotmail.AdrianSR.BattleRoyale.database;

import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleParticle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.ParachuteColor;

/**
 * Represents the various type 
 * of battle royale setting that 
 * {@link DTBPlayer}'s may have.
 */
public enum SettingType {
	
	/**
	 * The type of vehicle selected
	 * by the battle royale player.
	 */
	VEHICLE_TYPE(BRVehicleType.HORSE),

	/**
	 * The type of particles for
	 * the vehicle selected by the
	 * battle royale player.
	 */
	VEHICLE_PARTICLES(BRVehicleParticle.RAINBOW),

	/**
	 * The color for the parachute
	 * selected by the battle royale player.
	 */
	PARACHUTE_COLOR(ParachuteColor.BLACK);
	
	/**
	 * The setting default value set by the plugin.
	 */
	private Enum<?> default_value;
	
	/**
	 * Construct new setting.
	 * <p>
	 * @param default_value the setting default value.
	 */
	private SettingType(Enum<?> default_value) {
		this.default_value = default_value;
	}
	
	/**
	 * Get default setting value
	 * set by the plugin.
	 * <p>
	 * @return default setting value.
	 */
	public Enum<?> getDefaultValue() {
		return default_value;
	}
}
