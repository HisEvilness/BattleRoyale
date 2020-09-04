package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * An enum for the supported
 * vehicle plugins by the 
 * {@link BattleRoyale}.
 * <p>
 * @author AdrianSR
 */
public enum VehiclesPlugin {
	
	/**
	 * Author: Zombie Striker.
	 * Name: QualityArmoryVehicles.
	 */
	QUALITY_ARMORY_VEHICLES
	(
		"QualityArmoryVehicles",
		VehicleType.BOAT, 
		VehicleType.CAR, 
		VehicleType.HELICOPTER, 
		VehicleType.PLANE,
		VehicleType.TRAIN
	),
	
	/**
	 * Author: Pollitoyeye.
	 * Name: Vehicles.
	 */
	VEHICLES
	(
		"Vehicles",
		VehicleType.BIKE,
		VehicleType.CAR,
		VehicleType.HELICOPTER,
		VehicleType.HOVER_BIKE,
		VehicleType.PLANE,
		VehicleType.RAFT,
		VehicleType.SUBMARINE,
		VehicleType.TANK,
		VehicleType.TRAIN
	);
	
	/**
	 * The name of the plugin;
	 */
	private final String plugin;
	
	/**
	 * All types supported
	 * by the vehicles plugin.
	 */
	private final VehicleType[] types;
	
	/**
	 * Construct enum value.
	 * <p>
	 * @param types
	 */
	VehiclesPlugin(String plugin, VehicleType... types) {
		this.plugin = plugin;
		this.types  = types;
	}
	
	/**
	 * Returns the name of the
	 * plugin.
	 * <p>
	 * @return
	 */
	public String getPluginName() {
		return plugin;
	}
	
	/**
	 * @return true if the plugin is enabled.
	 */
	public boolean isPluginEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
	}
	
	/**
	 * Returns all the types supported
	 * by the vehicles plugin.
	 */
	public VehicleType[] getTypes() {
		return types;
	}
	
	/**
	 * Returns all the types supported
	 * by the vehicles plugin in a list.
	 */
	public List<VehicleType> getTypesList() {
		return Arrays.asList(types);
	}
	
	public static VehiclesPlugin[] enabledValues() {
		List<VehiclesPlugin> enabled = Arrays.stream(values()).filter(item -> Bukkit.getPluginManager().isPluginEnabled(item.plugin)).collect(Collectors.toList());
		return enabled.toArray(new VehiclesPlugin[enabled.size()]);
	}
}