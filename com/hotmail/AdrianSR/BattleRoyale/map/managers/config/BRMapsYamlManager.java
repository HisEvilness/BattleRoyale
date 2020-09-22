package com.hotmail.AdrianSR.BattleRoyale.map.managers.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrinkingSuccession;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.MiniMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.setup.MiniMapGenerator;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.vector.ConfigurableVector3i;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents a Battle Royale Yaml config manager.
 * 
 * @author AdrianSR
 */
public final class BRMapsYamlManager {
	
	/**
	 * Battle Royale maps Yaml configuration files name.
	 */
	public static final String    BATTLE_MAPS_YML_CONFIG_FILE_NAME = "BattleMapConfig.yml";
	public static final String BATTLE_MAPS_MINIMAP_IMAGE_FILE_NAME = "MiniMapImage.png";
	
	/**
	 * Class values.
	 */
	private final BattleMap                                  map;
	private       String                                 mapName;
	private final File                                    folder;
	private final List < ConfigurableLocation >           spawns;
	private       Vector3i                              corner_a;
	private       Vector3i                              corner_b;
	private       BorderShrinkingSuccession    border_succession;
	private final List < ConfigurableLocation >    random_chests;
	private final List < Vehicle >                      vehicles;
	
	private Area             area;
	private MiniMap       minimap;
	private boolean dirty_minimap;
	
	/**
	 * Construct a new Battle Royale Yml config manager.
	 * <p>
	 * @param map the {@link BattleMap}.
	 */
	public BRMapsYamlManager ( BattleMap map ) {
		this.map           = map;
		this.folder        = map.getFolder ( );
		this.spawns        = new ArrayList < ConfigurableLocation > ( );
		this.random_chests = new ArrayList < ConfigurableLocation > ( );
		this.vehicles      = new ArrayList < Vehicle > ( );
		
		loadYamlConfiguration ( );
	}
	
	/**
	 * Load the map configuration.
	 */
	private void loadYamlConfiguration ( ) {
		File configuration_file = getFile ( );
		File minimap_image_file = getMiniMapImageFile ( );
		
		// here we're loading the map configuration from the corresponding file.
		if ( configuration_file.exists ( ) ) {
			YamlConfiguration yml = YamlConfiguration.loadConfiguration ( configuration_file );
			if ( yml == null ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
						"The map configuration could not be loaded: " + map.getName ( ) + "'" , BattleRoyale.getInstance ( ) );
				return;
			}
			
//			System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 2" );
			// load name.
			mapName = yml.getString("Name", map.getFolder().getName());
//			System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 3: " + mapName );
			// load spawns.
			if (yml.isConfigurationSection("Spawns")) {
//				System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 4" );
				// get and check spawns section.
				final ConfigurationSection spsc = yml.getConfigurationSection("Spawns");
				if (spsc != null) {
					// load locs in.
					for (String key : spsc.getKeys(false)) {
						if (spsc.isConfigurationSection(key)) {
							// add.
							spawns.add ( ConfigurableLocation.of ( spsc.getConfigurationSection ( key ) ) );
						}
					}
				}
//				System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 5: " + spawns.size ( ) );
			}
//			System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 6" );
			// load chests.
			if (yml.isConfigurationSection("Chests")) {
//				System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 7" );
				// get chest section.
				final ConfigurationSection spsc = yml.getConfigurationSection("Chests");
				
				// load locs in.
				for (String key : spsc.getKeys(false)) {
					if (spsc.isConfigurationSection(key)) {
						// add.
						this.random_chests.add ( ConfigurableLocation.of ( spsc.getConfigurationSection ( key ) ) );
					}
				}
//				System.out.println ( "BRMapsYamlManager.loadYamlConfiguration ( ) ----- 7: " + random_chests.size ( ) );
			}
			
			/* load game area */
			if ( yml.isConfigurationSection ( "corners" ) ) {
				final ConfigurationSection cosc = yml.getConfigurationSection("corners");
				
				/* load corners from section */
				if ( cosc.isConfigurationSection ( "corner-a" ) ) {
					corner_a = ConfigurableVector3i.of ( cosc.getConfigurationSection ( "corner-a" ) );
				}
				
				if ( cosc.isConfigurationSection ( "corner-b" ) ) {
					corner_b = ConfigurableVector3i.of ( cosc.getConfigurationSection ( "corner-b" ) );
				}
				
				if ( corner_a != null && corner_b != null ) {
					setArea ( new Area ( corner_a , corner_b ).getSquared ( ) );
				}
			}
			
			// load border succession.
			if (yml.isConfigurationSection("BorderSuccessionPoints")) {
				this.border_succession = new BorderShrinkingSuccession(yml.getConfigurationSection("BorderSuccessionPoints"));
			}
		}
		
		// here we're loading the minimap image from the corresponding file, this might
		// be a heavy proccess due to minimap image can be very high, then must be
		// loaded asynchronously.
		if ( minimap_image_file.exists ( ) ) {
			final ExecutorService executor = Executors.newSingleThreadExecutor ( );
			executor.execute ( new Runnable ( ) {
				@Override public void run ( ) {
					try {
						loadMiniMap ( minimap_image_file );
					} catch ( IOException ex ) {
						ex.printStackTrace ( );
					}
					
					executor.shutdownNow ( );
				}
			} );
		}
	}
	
	public File getMapFolder ( ) {
		return folder;
	}
	
	public File getFile ( ) {
		return new File ( getMapFolder ( ) , BATTLE_MAPS_YML_CONFIG_FILE_NAME );
	}
	
	public File getMiniMapImageFile ( ) {
		return new File ( getMapFolder ( ) , BATTLE_MAPS_MINIMAP_IMAGE_FILE_NAME );
	}
	
	/**
	 * Gets the map game-play area.
	 * <p>
	 * @return map game-play area.
	 */
	@Nullable
	public Area getArea ( ) {
		return area;
	}
	
	/**
	 * Sets the map game-play area.
	 * <p>
	 * @param area the map game-play area, that must squared.
	 * @throws IllegalArgumentException if the area is not squared.
	 */
	@Nonnull
	public void setArea ( Area area ) {
		Validate.notNull ( area );
		Validate.isTrue ( area.isSquared ( ) , "area must be squared!" );
		
		dirty_minimap = !Objects.equals ( area , this.area );
		this.area     = area;
	}
	
	/**
	 * Gets the minimap for the map.
	 * <p>
	 * @return the minimap.
	 */
	public MiniMap getMiniMap ( ) {
		return minimap;
	}
	
	/**
	 * Sets the minimap for the map.
	 * <p>
	 * @param minimap the minimap.
	 */
	public void setMiniMap ( @Nullable MiniMap minimap ) {
		this.minimap  = minimap;
		dirty_minimap = false;
	}
	
	/**
	 * Loads the minimap from default minimap image file: {@link #getMiniMapImageFile()}.
	 * <p>
	 * @throws IOException if an error occurs during loading.
	 */
	public void loadMiniMap ( ) throws IOException {
		loadMiniMap ( getMiniMapImageFile ( ) );
	}
	
	/**
	 * Loads the minimap from the specified {@link File}.
	 * <p>
	 * @param file the file to load from.
	 * @throws IOException if an error occurs during loading.
	 */
	@Nonnull
	public void loadMiniMap ( File file ) throws IOException {
		if ( minimap != null ) {
			minimap.load ( file );
		} else {
			setMiniMap ( new MiniMap ( file ) );
		}
		
		dirty_minimap = false;
	}
	
	/**
	 * Add a spawn.
	 * 
	 * @param ConfigurableLocation the spawn location.
	 */
	public void addSpawn ( ConfigurableLocation loc ) {
		if ( loc != null && !spawns.contains ( loc ) ) {
			spawns.add ( loc );
		}
	}
	
	/**
	 * Add a spawn.
	 * 
	 * @param loc the spawn location.
	 */
	public void addSpawn ( Location loc ) {
		addSpawn ( new ConfigurableLocation ( loc ) );
	}
	
	/**
	 * Add a random chest {@link Loc}.
	 * 
	 * @param loc the random Loc.
	 */
	public void addRandomChest ( ConfigurableLocation loc ) {
		if ( loc != null && !random_chests.contains ( loc ) ) {
			random_chests.add ( loc );
		}
	} 
	
	/**
	 * Add a random chest from {@link Location}.
	 * 
	 * @param loc the random Location.
	 */
	public void addRandomChest ( Location loc ) {
		addRandomChest ( new ConfigurableLocation ( loc ) );
	}
	
	/**
	 * Remove a random chest from {@link Loc}.
	 * 
	 * @param loc the random Loc.
	 */
	public void removeRandomChest ( ConfigurableLocation loc ) {
		random_chests.remove ( loc );
	}
	
	/**
	 * Remove a random chest from {@link Location}.
	 * 
	 * @param loc the random Location.
	 */
	public void removeRandomChest ( Location loc ) {
		removeRandomChest ( new ConfigurableLocation ( loc ) );
	}
	
	public void addVehicle(Vehicle vehicle) {
		if (!vehicles.contains(vehicle) && vehicle != null && vehicle.isValid()) {
			this.vehicles.add(vehicle); 
		}
	}
	
	public void removeVehicle(Vehicle vehicle) {
		if (vehicle != null) {
			this.vehicles.remove(vehicle);
		}
	}
	
	/**
	 * Returns the spawn points for
	 * the vehicles.
	 * <p>
	 * @return vehicles spawn points.
	 */
	public List<Vehicle> getVehicles() {
		return Collections.unmodifiableList(this.vehicles);
	}
	
	/**
	 * Get the map name.
	 * 
	 * @return the map name.
	 */
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * Get spawns on the Battle map.
	 * 
	 * @return a spawn list of the battle map.
	 */
	public List<ConfigurableLocation> getSpawns() {
		return Collections.unmodifiableList(spawns);
	}

	/**
	 * Get chests Locs.
	 * 
	 * @return a chest {@link ConfigurableLocation} list.
	 */
	public List<ConfigurableLocation> getChests() {
		return Collections.unmodifiableList(random_chests);
	}
	
	/**
	 * Check if a {@link Block} is a random chest.
	 * 
	 * @param loc the Location to check.
	 * @return true if is.
	 */
	public boolean isRandomChest(final Block block) {
		return block != null && isRandomChest(block.getLocation());
	}
	
	/**
	 * Check if a {@link Location} is a random chest.
	 * <p>
	 * @param loc the Location to check.
	 * @return true if is.
	 */
	public boolean isRandomChest(final Location loc) {
		if (loc == null) {
			return false;
		}
		
		for (ConfigurableLocation lok : getChests()) {
			if (lok != null && lok.equals(loc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get random spawn.
	 * 
	 * @return a random spawn {@link Location}.
	 */
	public Location getRandomSpawn ( ) {
		if ( spawns.isEmpty ( ) ) {
			return null;
		} else {
			List < ConfigurableLocation > valid_spawns = spawns.stream ( )
					.filter ( location -> location.isValid ( ) )
					.collect ( Collectors.toList ( ) );
			if ( valid_spawns.isEmpty ( )  ) {
				return null;
			} else {
				return valid_spawns.get ( RandomUtils.nextInt ( valid_spawns.size ( ) ) );
			}
		}
	}
	
	/**
	 * Get random chests locations.
	 * 
	 * @return a random location list.
	 */
	public List<Location> getRandomChests() {
		final List<Location> chests = new ArrayList<Location>();
		final List<Location> locs   = new ArrayList<Location>();
		for (int x = 0; x < random_chests.size(); x ++) {
			final ConfigurableLocation loc = random_chests.get(x);
			if ( loc.isValid ( ) ) {
				locs.add ( loc );
			}
		}
		
		// get random locs. // (random_chests.size() / 2)
		for (int x = 0; x < locs.size(); x ++) {
			Location rand = locs.get(RandomUtils.nextInt(locs.size()));
			if (chests.contains(rand)) {
				continue;
			}
			chests.add(rand);
		}
		return chests;
	}
	
	/**
	 * Get loot chests locations.
	 * 
	 * @return a loot chest location list.
	 */
	public List<Location> getValidChests() {
		final List<Location> chests = new ArrayList<Location>();
		for (ConfigurableLocation loc : random_chests) {
			if ( loc.isValid ( ) ) {
				chests.add ( loc );
			}
		}
		return chests;
	}
	
	/**
	 * Returns the corner 'A'
	 * of the Battle Map Area.
	 * <p>
	 * @return corner 'A'.
	 */
	public Vector3i getCornerA() {
		return corner_a;
	}
	
	/**
	 * Returns the corner 'B'
	 * of the Battle Map Area.
	 * <p>
	 * @return corner 'B'.
	 */
	public Vector3i getCornerB() {
		return corner_b;
	}
	
	/**
	 * Get the map center point.
	 * <p>
	 * @return the center point {@link ConfigurableLocation}
	 */
	public ConfigurableLocation getMapCenter ( ) {
		final Vector3d vector = getMapCenterVector ( ); 
		if ( vector != null ) {
			return new ConfigurableLocation ( map.getWorld ( ) , vector.getX ( ) , vector.getY ( ) , vector.getZ ( ) );
		} else {
			return null;
		}
	}
	
	public Vector3d getMapCenterVector ( ) {
		if ( map.getArea ( ) != null ) {
			return map.getArea ( ).getCenter ( );
		} else {
			return null;
		}
	}
	
	/**
	 * Set the map boder shrinking succession.
	 * 
	 * @param newSuccession the new succession.
	 */
	public void setBorderShrinkSuccession(final BorderShrinkingSuccession newSuccession) {
		this.border_succession = newSuccession;
	}
	
	/**
	 * Get map boder shrink succession.
	 * 
	 * @return the succession.
	 */
	public BorderShrinkingSuccession getBorderSuccession ( ) {
		return border_succession;
	}
	
	/**
	 * Saves current map configuration.
	 * <p>
	 * @param minimap save minimap image? <strong>Note that this will render the
	 *                game-play area if required, and then, block until rendered so
	 *                this must be called asynchronously.</strong>
	 */
	public void saveConfig ( boolean minimap ) {
		// if this map is a temp copy, the configuration must be saved to the real map.
		File map_folder = map.isBuildLoaded ( ) ? getMapFolder ( ) 
				: new File ( MapsManager.checkBattleMapsFolder ( ) , 
						getMapFolder ( ).getName ( ).replace ( BRMapsWorldManager.MAPS_TEMP_COPY_FOLDER_SUFIX , "" ) );
		
		File configuration_file = new File ( map_folder , BATTLE_MAPS_YML_CONFIG_FILE_NAME );
		File minimap_image_file = new File ( map_folder , BATTLE_MAPS_MINIMAP_IMAGE_FILE_NAME );
				
		// here we're saving the configuration of the map.
		if ( !configuration_file.exists ( ) ) {
			try {
				configuration_file.createNewFile ( );
			} catch ( IOException ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
						"Couldn't save configuration of map '" + getMapName ( ) + "':", BattleRoyale.getInstance ( ) );
				ex.printStackTrace ( );
				return;
			}
		}
		
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration ( configuration_file );
		if ( saveToConfig ( configuration ) > 0 ) {
			try {
				configuration.save ( configuration_file );
			} catch ( IOException ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
						"Couldn't save configuration of map '" + getMapName ( ) + "':", BattleRoyale.getInstance ( ) );
				ex.printStackTrace ( );
				return;
			}
		}
		
		// here we're saving the minimap (rendering it if required).
		if ( ( this.minimap == null || this.minimap.getColors ( ) == null ) || dirty_minimap ) {
			// this will block until rendered, so this method must be called asynchronously.
			this.minimap  = new MiniMapGenerator ( map_folder ).generate ( getArea ( ) );
			dirty_minimap = false;
		}
		
		try {
			this.minimap.save ( minimap_image_file );
		} catch ( IOException ex ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
					"Couldn't save minimap for map '" + getMapName ( ) + "':", BattleRoyale.getInstance ( ) );
			ex.printStackTrace ( );
		}
	}
	
	/**
	 * Save map configuration to specified {@link ConfigurationSection}.
	 * <p>
	 * @param section the config section.
	 * @return a number > 0 if there are changes.
	 */
	public int saveToConfig(ConfigurationSection section) {
		/* save area */
		int saved_corners = 0;
		if ( getArea ( ) != null ) {
			ConfigurationSection cosc = section.createSection ( "corners" );
			
			saved_corners += new ConfigurableVector3i ( getArea ( ).getMax ( ) ).save ( cosc.createSection ( "corner-a" ) );
			saved_corners += new ConfigurableVector3i ( getArea ( ).getMin ( ) ).save ( cosc.createSection ( "corner-b" ) );
			
//			/* get corners as array */
//			final ConfigurableLocation [ ] corners = new ConfigurableLocation [ ] 
//					{
//						new ConfigurableLocation(mapName, getArea().getMax().getX(), getArea().getMax().getY(), getArea().getMax().getZ()),
//						new ConfigurableLocation(mapName, getArea().getMin().getX(), getArea().getMin().getY(), getArea().getMin().getZ())
//					};
//			
//			/* save corners to their configuration sections */
//			for ( int x = 0 ; x < corners.length ; x ++ ) {
//				corners[x].saveToConfig(cosc.createSection("corner-" + (x == 0 ? "a" : "b")), false, false, false);
//				saved_corners ++;
//			}
		}
		
		// save spawns.
		int savedSpawns = 0;
		if ( spawns.size ( ) > 0 ) {
			int count = 0;
//			savedSpawns += section.isConfigurationSection("Spawns") ? 0 : YamlUtil.createNotExisting ( section , "Spawns" ) != null ? 1 : 0;
			
			ConfigurationSection spawns_section = section.createSection ( "Spawns" );
			for (ConfigurableLocation loc : spawns) {
				if ( loc.isValid ( ) ) {
					savedSpawns += loc.save ( spawns_section.createSection ( "spawn-" + count ) );
					count ++;
				}
			}
		}
		
		// save random chests.
		int savedChests = 0;
		if (random_chests.size() > 0) {
			int count = 0;
			savedChests += section.isConfigurationSection("Chests") ? 0 : YamlUtil.createNotExisting ( section , "Chests" ) != null ? 1 : 0;
			for (ConfigurableLocation loc : random_chests) {
				if ( loc.isValid ( ) ) {
					savedChests += loc.save ( section.getConfigurationSection ( "Chests" ).createSection ( "chest-" + count ) );
					count++;
				}
			}
		}
		
		// save border succession.
		int saveBorder = border_succession != null
				? border_succession.saveToConfig(section.createSection("BorderSuccessionPoints"))
				: 0;
				
		/* save vehicles */
		int saved_vehicles = section.isConfigurationSection("VehiclesAndSpawns") 
				? 0 
				: YamlUtil.createNotExisting ( section , "VehiclesAndSpawns" ) != null ? 1 : 0;
		for (Vehicle vehicle : vehicles) {
			if (vehicle == null) {
				continue;
			}
			
			vehicle.save(section.getConfigurationSection("VehiclesAndSpawns").createSection("vehicle-" + saved_vehicles));
			saved_vehicles ++;
		}
		
		return ( YamlUtil.setNotEqual ( section , "Name" , getMapName ( ) ) ? 1 : 0 )
			  + savedSpawns // saved spawns.
			  + saveBorder  // save border succession
			  + savedChests // saved random chests.
			  + saved_corners
//			  + savedOres   // saved ores.
			  + saved_vehicles
			  ;
	}
}