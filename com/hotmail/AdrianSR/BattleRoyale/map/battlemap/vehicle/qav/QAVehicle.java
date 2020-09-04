package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.qav;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.IllegalPluginAccessException;

import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleSpawnException;
import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleTypeException;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehicleType;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehiclesPlugin;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

import me.zombie_striker.qg.exp.cars.api.QualityArmoryVehicles;
import me.zombie_striker.qg.exp.cars.baseclasses.AbstractVehicle;

public final class QAVehicle extends Vehicle {
	
	public static final String VEHICLES_CONFIG_TYPE_KEY = "vehicle-name";

	private final String vehicle_name;

	public QAVehicle(ConfigurationSection section) {
		super((VehicleType) null);
		this.vehicle_name = section.getString(VEHICLES_CONFIG_TYPE_KEY);
		this.spawn        = section != null ? ( section.isConfigurationSection("spawn") 
				? ConfigurableLocation.of ( section.getConfigurationSection("spawn")) : null ) : null;
	}
	
	public QAVehicle(String vehicle_name) {
		super((VehicleType) null);
		this.vehicle_name = vehicle_name;
	}
	
	@Override
	public VehiclesPlugin getVehiclesPlugin() {
		return VehiclesPlugin.QUALITY_ARMORY_VEHICLES;
	}
	
	@Override
	public void spawn(Location location) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
			IllegalVehicleTypeException, IllegalArgumentException {
		if (!BattleRoyale.isQualitArmoryVehiclesEnabled()) { /* check plugin */
			throw new IllegalPluginAccessException("The QualityArmoryVehicles is not enabled!");
		}

		if (getSpawn() == null) {
			throw new IllegalVehicleSpawnException("The type/spawn cannot be null!");
		}
		
		/* load vehicle */
		AbstractVehicle vehicle = QualityArmoryVehicles.getVehicle(vehicle_name);
		if (vehicle == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "The vehicle '" + vehicle_name
					+ "' could not be found in the vehicles inside the folder 'plugins/QualityArmoryVehicles/vehicles'!",
					BattleRoyale.getInstance());
			return;
		}
		
		vehicle.setCanJump(false);
		vehicle.setEnableFuel(false);
		
		/* spawn */
		QualityArmoryVehicles.spawnVehicle(vehicle, location, null);
	}

	@Override
	public void spawn(World world) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
	IllegalVehicleTypeException, IllegalArgumentException {
		spawn ( getSpawn ( ).withWorld ( world ) );
	}
	
	@Override
	public int save(ConfigurationSection section) {
		int save = YamlUtil.setNotSet ( section , VEHICLE_PLUGIN_KEY, getVehiclesPlugin().name()) ? 1 : 0;
		save += YamlUtil.setNotSet ( section , VEHICLES_CONFIG_TYPE_KEY, vehicle_name ) ? 1 : 0;
		
		/* save spawn position */
		if (spawn != null) {
			save += spawn.save(section.createSection("spawn"));
		}
		return save;
	}

	@Override
	public boolean isValid() {
		return !StringUtils.isBlank(vehicle_name) && getSpawn() != null;
	}
}