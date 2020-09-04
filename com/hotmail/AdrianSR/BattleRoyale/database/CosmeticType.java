package com.hotmail.AdrianSR.BattleRoyale.database;

import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleParticle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.ParachuteColor;

/**
 * Represents the various type 
 * of battle royale setting that 
 * {@link DTBPlayer}'s may have.
 * <p>
 * @author AdrianSR
 */
public enum CosmeticType {
	
	/**
	 * Cosmetic type for vehicle types.
	 */
	VEHICLE_TYPE(BRVehicleType.class),

	/**
	 * Cosmetic type for vehicle particles.
	 */
	VEHICLE_PARTICLES(BRVehicleParticle.class),

	/**
	 * Cosmetic type for parachute colors.
	 */
	PARACHUTE_COLOR(ParachuteColor.class);
	
	private final Class<? extends Enum> cosmetic_class;
	
	CosmeticType(Class<? extends Enum> cosmetic_class) {
		this.cosmetic_class = cosmetic_class;
	}
	
	/**
	 * Returns the enum class of
	 * the type of cosmetic.
	 * <p>
	 * @return enum class.
	 */
	public Class<? extends Enum> getEnumClass() {
		return cosmetic_class;
	}
}