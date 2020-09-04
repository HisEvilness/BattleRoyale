package com.hotmail.AdrianSR.BattleRoyale.util.configurable.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.adriansr.core.util.StringUtil;

/**
 * An convenience implementation of
 * {@link com.hotmail.adriansr.core.util.configurable.location.ConfigurableLocation}
 * that is compatible with older configurations.
 * <p>
 * @author AdrianSR / Saturday 15 August, 2020 / 10:03 PM
 */
public class ConfigurableLocation extends com.hotmail.adriansr.core.util.configurable.location.ConfigurableLocation {

	public static final String                      OLD_WORLD_KEY = "World";
	public static final String                          OLD_X_KEY = "X";
	public static final String                          OLD_Y_KEY = "Y";
	public static final String                          OLD_Z_KEY = "Z";
	public static final String                        OLD_YAW_KEY = "Yaw";
	public static final String                      OLD_PITCH_KEY = "Pitch";
	public static final String [ ] OLD_CONFIGURABLE_LOCATION_KEYS = { OLD_WORLD_KEY , OLD_X_KEY , OLD_Y_KEY , 
			OLD_Z_KEY , OLD_YAW_KEY , OLD_PITCH_KEY };
	
	/**
	 * Returns a {@link ConfigurableLocation} loaded from the given
	 * {@link ConfigurationSection}, or null if there is no any valid
	 * {@link ConfigurableLocation} stored on the given
	 * {@link ConfigurationSection}.
	 * <p>
	 * Note that this method checks the given configuration section calling
	 * {@link #isConfigurableLocation(ConfigurationSection)}.
	 * <p>
	 * @param section the section to parse.
	 * @return the parsed location.
	 */
	public static ConfigurableLocation of ( ConfigurationSection section ) {
		if ( isConfigurableLocation ( section ) ) {
			return new ConfigurableLocation ( ).load ( section );
		} else {
			return null;
		}
	}
	
	/**
	 * Return true if and only if there is a valid {@link ConfigurableLocation}
	 * stored on the given {@link ConfigurationSection}
	 * <p>
	 * @param section the {@link ConfigurationSection} where the supposed
	 *                {@link ConfigurableLocation} is stored.
	 * @return true if is.
	 */
	public static boolean isConfigurableLocation ( ConfigurationSection section ) {
		if ( com.hotmail.adriansr.core.util.configurable.location.ConfigurableLocation.isConfigurableLocation ( section ) ) {
			return true;
		} else {
			// compatibility for older configurations.
			for ( String key : OLD_CONFIGURABLE_LOCATION_KEYS ) {
				switch ( key ) {
					case OLD_WORLD_KEY:
						if ( !section.isString ( key ) ) {
							return false;
						}
						break;
						
					default:
						if ( !( section.get ( key ) instanceof Number ) ) {
							return false;
						}
						break;
				}
			}
			return true;
		}
	}
	
	/** name of the future world that is to be loaded */
	protected String world_name;

	public ConfigurableLocation ( ) {
		super ( );
	}

	public ConfigurableLocation ( Location copy ) {
		super ( copy );
	}

	public ConfigurableLocation ( World world, double x, double y, double z, float yaw, float pitch ) {
		super ( world , x , y , z , yaw , pitch );
	}
	
	public ConfigurableLocation ( String world , double x , double y , double z , float yaw , float pitch ) {
		super ( null , x , y , z , yaw , pitch );
		
		this.world_name = world;
		// as this flag is intended for checking this location is valid, and the
		// isValid() method returns false if the world is null, we can set this to
		// false, this makes the isValid() method to return true after loadWorld() is
		// called.
		this.initialized = true;
	}
	
	public ConfigurableLocation ( String world , double x , double y , double z ) {
		this ( world , x , y , z , 0.0F , 0.0F );
	}

	public ConfigurableLocation ( World world , double x , double y , double z ) {
		super ( world , x , y , z );
	}
	
	/**
	 * Loads and set the world from the specified {@link #world_name}.
	 * <p>
	 * @return this Object, for chaining.
	 */
	public ConfigurableLocation loadWorld ( ) {
		this.setWorld ( Bukkit.getWorld ( world_name ) );
		return this;
	}
	
	@Override
	public ConfigurableLocation load ( ConfigurationSection section ) {
//		System.out.println ( "ConfigurableLocation.load ( ) --------- 0" );
		if ( com.hotmail.adriansr.core.util.configurable.location.ConfigurableLocation.isConfigurableLocation ( section ) ) {
//			System.out.println ( "ConfigurableLocation.load ( ) --------- 1" );
			super.load ( section );
		} else if ( isConfigurableLocation ( section ) ) {
//			System.out.println ( "ConfigurableLocation.load ( ) --------- 2" );
			// compatibility for older versions.
//			System.out.println ( "ConfigurableLocation.load ( ) --------- 3: " + StringUtil.defaultIfBlank ( section.getString ( OLD_WORLD_KEY , null ) , "" ) );
			this.setWorld ( Bukkit.getWorld ( StringUtil.defaultIfBlank ( section.getString ( OLD_WORLD_KEY , null ) , "" ).replace ( '\\' , '/' ) ) );
//			System.out.println ( "ConfigurableLocation.load ( ) --------- 4: " + getWorld ( ) );
//			System.out.println ( "ConfigurableLocation.load ( ) --------- 5: " + test );
			this.setX ( section.getDouble ( OLD_X_KEY , 0 ) );
			this.setY ( section.getDouble ( OLD_Y_KEY , 0 ) );
			this.setZ ( section.getDouble ( OLD_Z_KEY , 0 ) );
			this.setYaw ( (float) section.getDouble ( OLD_YAW_KEY , 0 ) );
			this.setPitch ( (float) section.getDouble ( OLD_PITCH_KEY , 0 ) );
			this.initialized = true;
		}
		return this;
	}
	
	@Override
	public ConfigurableLocation withWorld ( World world ) {
		return (ConfigurableLocation) super.withWorld ( world );
	}
	
	@Override
	public ConfigurableLocation clone ( ) {
		return (ConfigurableLocation) super.clone ( );
	}
}