package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.vehicles;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;

import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleSpawnException;
import com.hotmail.AdrianSR.BattleRoyale.exceptions.IllegalVehicleTypeException;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehicleType;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehiclesPlugin;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.interfaces.VehicleManager;
import es.pollitoyeye.vehicles.interfaces.VehicleSubType;
import es.pollitoyeye.vehicles.vehiclemanagers.BikeManager;
import es.pollitoyeye.vehicles.vehiclemanagers.CarManager;
import es.pollitoyeye.vehicles.vehiclemanagers.HelicopterManager;
import es.pollitoyeye.vehicles.vehiclemanagers.HoverBikeManager;
import es.pollitoyeye.vehicles.vehiclemanagers.PlaneManager;
import es.pollitoyeye.vehicles.vehiclemanagers.RaftManager;
import es.pollitoyeye.vehicles.vehiclemanagers.SubmarineManager;
import es.pollitoyeye.vehicles.vehiclemanagers.TankManager;
import es.pollitoyeye.vehicles.vehiclemanagers.TrainManager;
import es.pollitoyeye.vehicles.vehicletypes.BikeType;
import es.pollitoyeye.vehicles.vehicletypes.CarType;
import es.pollitoyeye.vehicles.vehicletypes.HelicopterType;
import es.pollitoyeye.vehicles.vehicletypes.HoverBikeType;
import es.pollitoyeye.vehicles.vehicletypes.PlaneType;
import es.pollitoyeye.vehicles.vehicletypes.RaftType;
import es.pollitoyeye.vehicles.vehicletypes.SubmarineType;
import es.pollitoyeye.vehicles.vehicletypes.TankType;
import es.pollitoyeye.vehicles.vehicletypes.TrainType;

public final class VVehicle extends Vehicle {
	
	public static final String VEHICLES_CONFIG_TYPE_KEY = "config-type";
	
	private final String config_type;

	public VVehicle(ConfigurationSection section) {
		super(section);
		this.config_type = section.getString(VEHICLES_CONFIG_TYPE_KEY);
	}
	
	public VVehicle(VehicleType type, String config_type) {
		super(type);
		this.config_type = config_type;
	}

	@Override
	public VehiclesPlugin getVehiclesPlugin() {
		return VehiclesPlugin.VEHICLES;
	}
	
	@Override
	public void spawn(Location location) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
			IllegalVehicleTypeException, IllegalArgumentException {
		if (!BattleRoyale.isVehiclesEnabled()) { /* check plugin */
			throw new IllegalPluginAccessException("The Vehicles plugin is not enabled!");
		}

		if (getType() == null) {
			throw new IllegalVehicleTypeException("The type cannot be null!");
		}
		
		if (getSpawn() == null) {
			throw new IllegalVehicleSpawnException("The spawn cannot be null!");
		}

		if (!getVehiclesPlugin().getTypesList().contains(getType())) {
			throw new IllegalArgumentException("The plugin '" + getVehiclesPlugin().name()
					+ "' donnot supports the type '" + getType().name() + "'!");
		}
		
		VehicleSubType sub_type = null;
		switch(getType()) {
		case BIKE:
			sub_type = BikeType.valueOf(config_type);
			break;
		case CAR:
			sub_type = CarType.valueOf(config_type);
			break;
		case HELICOPTER:
			sub_type = HelicopterType.valueOf(config_type);
			break;
		case HOVER_BIKE:
			sub_type = HoverBikeType.valueOf(config_type);
			break;
		case PLANE:
			sub_type = PlaneType.valueOf(config_type);
			break;
		case RAFT:
			sub_type = RaftType.valueOf(config_type);
			break;
		case SUBMARINE:
			sub_type = SubmarineType.valueOf(config_type);
			break;
		case TANK:
			sub_type = TankType.valueOf(config_type);
			break;
		case TRAIN:
			sub_type = TrainType.valueOf(config_type);
			break;
		default:
			return;
		}
		
		/* check type loaded from the vehicles plugin config */
		if (sub_type == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED,
					"The " + getType().name().toLowerCase()
							+ "Type '" + config_type + "'  could not be found in the file plugins/Vehicles/config.yml",
					BattleRoyale.getInstance());
			return;
		}
		
		/* spawn vehicle */
		VehicleManager spawner = null;
		switch(getType()) {
		case BIKE:
			spawner = new BikeManager();
			break;
		case CAR:
			spawner = new CarManager();
			break;
		case HELICOPTER:
			spawner = new HelicopterManager();
			break;
		case HOVER_BIKE:
			spawner = new HoverBikeManager();
			break;
		case PLANE:
			spawner = new PlaneManager();
			break;
		case RAFT:
			spawner = new RaftManager();
			break;
		case SUBMARINE:
			spawner = new SubmarineManager();
			break;
		case TANK:
			spawner = new TankManager();
			break;
		case TRAIN:
			spawner = new TrainManager();
			break;
		default:
			break;
		}
		
		/* override owners id before spawn */
		overrideOwnersID();
		
		/* spawn vehicle */
		ArmorStand handle = spawner.spawn(location, UUID.randomUUID().toString(), config_type);
		handle.setGravity(true);
	}

	@Override
	public void spawn(World world) throws IllegalPluginAccessException, IllegalVehicleSpawnException,
	IllegalVehicleTypeException, IllegalArgumentException {
		spawn(getSpawn().withWorld(world));
	}
	
	public static void overrideOwnersID() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (es.pollitoyeye.vehicles.enums.VehicleType type : es.pollitoyeye.vehicles.enums.VehicleType.values()) {
				String permission_a = ( type.getConfigName() + "s.overrideOwner" );
				String permission_b = ( type.getConfigName() + "s.overrideOwner.ride" );
				if (!player.hasPermission(permission_a)) {
					player.addAttachment(VehiclesMain.getPlugin(), permission_a, true);
				}
				
				if (!player.hasPermission(permission_b)) {
					player.addAttachment(VehiclesMain.getPlugin(), permission_b, true);
				}
				player.recalculatePermissions();
			}
		}
	}
	
	@Override
	public boolean isValid() {
		return !StringUtils.isBlank(config_type) && getSpawn() != null;
	}
	
	@Override
	public int save(ConfigurationSection section) {
		int save = YamlUtil.setNotSet ( section ,VEHICLE_PLUGIN_KEY, getVehiclesPlugin().name()) ? 1 : 0;
		
		save += YamlUtil.setNotSet ( section ,VEHICLE_TYPE_KEY,   getType() != null ? getType().name() : "unknown") ? 1 : 0;
		save += YamlUtil.setNotSet ( section ,VEHICLES_CONFIG_TYPE_KEY, config_type) ? 1 : 0;
		
		/* save spawn position */
		if (spawn != null) {
			save += spawn.save(section.createSection("spawn"));
		}
		return save;
	}
}