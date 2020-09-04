package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.IllegalPluginAccessException;

import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleSpawnException;
import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleTypeException;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.qav.QAVehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.vehicles.VVehicle;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.saveable.Saveable;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

public abstract class Vehicle implements Saveable {
	
	public static final String VEHICLE_PLUGIN_KEY = "plugin-name";
	public static final String   VEHICLE_TYPE_KEY = "type";
	
	public static Vehicle of(ConfigurationSection section) {
		VehiclesPlugin plugin = EnumReflection.getEnumConstant(VehiclesPlugin.class, 
				section.getString(VEHICLE_PLUGIN_KEY, "").toUpperCase());
		if (plugin != null) {
			switch(plugin) {
			case QUALITY_ARMORY_VEHICLES:
				return new QAVehicle(section);
			case VEHICLES:
				return new VVehicle(section);
			default:
				break;
			}
		}
		return null;
	}
	
	protected final VehicleType type;
	protected       ConfigurableLocation   spawn;
	protected final UUID        uuid; 
	
	public Vehicle(ConfigurationSection section) {
		if (section != null) { 
			this.type  = EnumReflection.getEnumConstant(VehicleType.class, section.getString(VEHICLE_TYPE_KEY));
			this.spawn = ( section.isConfigurationSection("spawn") ? ConfigurableLocation.of (section.getConfigurationSection("spawn")) : null );
		} else {
			this.type  = null;
			this.spawn = null;
		}
		this.uuid = UUID.randomUUID();
	}
	
	protected Vehicle(VehicleType type) {
		this.type = type;
		this.uuid = UUID.randomUUID();
	}
	
	/**
	 * @return the plugin
	 */
	public abstract VehiclesPlugin getVehiclesPlugin();

	/**
	 * @return the type
	 */
	public VehicleType getType() {
		return type;
	}

	/**
	 * @return the spawn
	 */
	public ConfigurableLocation getSpawn() {
		return spawn;
	}
	
	/**
	 * @param spawn the spawn to set
	 */
	public void setSpawn(ConfigurableLocation spawn) {
		this.spawn = spawn;
	}
	
	/**
	 * Returns auto generated
	 * vehicle unique {@link UUID}.
	 */
	public final UUID getUniqueUUID() {
		return uuid;
	}
	
	public abstract void spawn(Location location) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
	IllegalVehicleTypeException, IllegalArgumentException;
	
	public abstract void spawn(World world) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
			IllegalVehicleTypeException, IllegalArgumentException;
	
	public abstract boolean isValid();

	@Override
	public int save(ConfigurationSection section) {
		int save = YamlUtil.setNotEqual ( section , VEHICLE_PLUGIN_KEY, getVehiclesPlugin() != null ? getVehiclesPlugin().name() : "unknown") ? 1 : 0;
		save    += YamlUtil.setNotEqual ( section , VEHICLE_TYPE_KEY,   getType() != null ? getType().name() : "unknown") ? 1 : 0;
		
		/* save spawn position */
		if (spawn != null) {
			save += spawn.save(section.createSection("spawn"));
		}
		return save;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Vehicle)) {
			return false;
		}
		return uuid.equals(((Vehicle) obj).uuid) || super.equals(obj);
	}
}