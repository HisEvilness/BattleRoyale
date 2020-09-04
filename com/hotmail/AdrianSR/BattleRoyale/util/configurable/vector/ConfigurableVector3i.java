package com.hotmail.AdrianSR.BattleRoyale.util.configurable.vector;

import org.bukkit.configuration.ConfigurationSection;

import com.flowpowered.math.vector.Vector3i;
import com.hotmail.adriansr.core.util.configurable.Configurable;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * A convenience implementation of {@link Vector3i} that can be saved to or
 * loaded from a {@link ConfigurationSection}.
 * <p>
 * @author AdrianSR / Sunday 16 August, 2020 / 11:14 AM
 */
public class ConfigurableVector3i extends Vector3i implements Configurable {
	
	public static final String                          X_KEY = "x";
	public static final String                          Y_KEY = "y";
	public static final String                          Z_KEY = "z";
	public static final String [ ] CONFIGURABLE_VECTOR3I_KEYS = { X_KEY , Y_KEY , Z_KEY };
	
	public static final String                          OLD_X_KEY = "X";
	public static final String                          OLD_Y_KEY = "Y";
	public static final String                          OLD_Z_KEY = "Z";
	public static final String [ ] OLD_CONFIGURABLE_VECTOR3I_KEYS = { OLD_X_KEY , OLD_Y_KEY , OLD_Z_KEY };

	private static final long serialVersionUID = 6650770869438256839L;
	
	/**
	 * Returns a {@link ConfigurableVector3i} loaded from the given
	 * {@link ConfigurationSection}, or null if there is no any valid
	 * {@link ConfigurableVector3i} stored on the given
	 * {@link ConfigurationSection}.
	 * <p>
	 * Note that this method checks the given configuration section calling
	 * {@link #isConfigurableVector3i(ConfigurationSection)}.
	 * <p>
	 * @param section the section to parse.
	 * @return the parsed location.
	 */
	public static ConfigurableVector3i of ( ConfigurationSection section ) {
		return isConfigurableVector3i ( section ) ? new ConfigurableVector3i ( ).load ( section ) : null;
	}
	
	/**
	 * Return true if and only if there is a valid {@link ConfigurableVector3i}
	 * stored in the given {@link ConfigurationSection}.
	 * <p>
	 * @param section the {@link ConfigurationSection} where the supposed
	 *                {@link ConfigurableVector3i} is stored.
	 * @return true if is.
	 */
	public static boolean isConfigurableVector3i ( ConfigurationSection section ) {
		boolean latest_configuration = true;
		for ( String key : CONFIGURABLE_VECTOR3I_KEYS ) {
			if ( !( section.get ( key ) instanceof Number ) ) {
				latest_configuration = false;
				break;
			}
		}
		
		// compatibility for old configurations.
		boolean older_configuration = true;
		for ( String key : OLD_CONFIGURABLE_VECTOR3I_KEYS ) {
			if ( !( section.get ( key ) instanceof Number ) ) {
				older_configuration = false;
				break;
			}
		}
		return latest_configuration || older_configuration;
	}
	
	public ConfigurableVector3i ( ) {
		super ( ); // zero
	}

	public ConfigurableVector3i ( double x , double y , double z ) {
		super ( x , y , z );
	}

	public ConfigurableVector3i ( int x , int y , int z ) {
		super ( x , y , z );
	}
	
	public ConfigurableVector3i ( Vector3i vector ) {
		super ( vector );
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * @return a new {@link ConfigurableVector3i} loaded from the given {@link ConfigurationSection}.
	 */
	@Override
	public ConfigurableVector3i load ( ConfigurationSection section ) {
		int x = section.isSet ( X_KEY ) ? section.getInt ( X_KEY ) : section.getInt ( OLD_X_KEY );
		int y = section.isSet ( Y_KEY ) ? section.getInt ( Y_KEY ) : section.getInt ( OLD_Y_KEY );
		int z = section.isSet ( Z_KEY ) ? section.getInt ( Z_KEY ) : section.getInt ( OLD_Z_KEY );
		return new ConfigurableVector3i ( x , y , z );
	}

	@Override
	public boolean isValid ( ) {
		return true;
	}

	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}

	@Override
	public int save ( ConfigurationSection section ) {
		return 	  ( YamlUtil.setNotEqual ( section , X_KEY , getX ( ) ) ? 1 : 0 )
				+ ( YamlUtil.setNotEqual ( section , Y_KEY , getY ( ) ) ? 1 : 0 ) 
				+ ( YamlUtil.setNotEqual ( section , Z_KEY , getZ ( ) ) ? 1 : 0 );
	}
	
	@Override
	public ConfigurableVector3i clone ( ) {
		return (ConfigurableVector3i) super.clone ( );
	}
}