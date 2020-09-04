package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.Duration;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Border shrink.
 * <p>
 * @author AdrianSR / Tuesday 07 July, 2020 / 08:47 AM
 */
public class BorderShrink {
	
	public static final double MIN_BORDERS_RADIUS = 5D;
	
	private final ConfigurableLocation location;
	
	private final double           radius;
	private final double radiation_damage;
	
	private final Duration shrinking_time;
	private final Duration  time_to_start;
	
	/**
	 * Construct a new Border Shrink.
	 * 
	 * @param location the point location.
	 * @param radius the new border radius.
	 * @param time the time to the shrink.
	 * @param unit the time unit to the shrink.
	 */
	public BorderShrink ( ConfigurableLocation location , double radius , double radiation_damage , long time_in_shrinking ,
			TimeUnit unit_in_shrinking , long time_to_start , TimeUnit unit_to_start ) {

		this.location         = location;
		this.radius           = Math.max ( radius , MIN_BORDERS_RADIUS ); // MIN_BORDERS_RADIUS is the minimum radius.
		this.radiation_damage = Math.max ( radiation_damage , 0 );
		this.shrinking_time   = Duration.of ( unit_in_shrinking , time_in_shrinking );
		this.time_to_start    = Duration.of ( unit_to_start , time_to_start );
	}
	
	public ConfigurableLocation getLocation ( ) {
		return location;
	}
	
	public double getRadius ( ) {
		return radius;
	}
	
	public double getRadiationDamage ( ) {
		return radiation_damage;
	}
	
	public Duration getShrinkingTime ( ) {
		return shrinking_time;
	}
	
	public Duration getTimeToStart ( ) {
		return time_to_start;
	}
	
	/**
	 * @return true if this Border shrink is valid.
	 */
	public boolean isValid ( ) {
		return location != null && location.isValid ( ) 
				&& radius > 0 && getShrinkingTime() != null && getTimeToStart() != null;
	}
	
	/**
	 * @return number of changes made to the configuration.
	 */
	public int saveToConfig(ConfigurationSection section) {
		if ( isValid ( ) ) {
			int save = location.save ( section.createSection ( "Location" ) )
					+ ( YamlUtil.setNotSet ( section , "Radius" , radius ) ? 1 : 0 )
					+ ( YamlUtil.setNotSet ( section , "RadiationDamage" , radiation_damage ) ? 1 : 0 )
					+ ( YamlUtil.setNotSet ( section , "Time-in-shrinking" , getShrinkingTime ( ).getDuration ( ) ) ? 1 : 0 )
					+ ( YamlUtil.setNotSet ( section , "TimeUnit-in-shrinking" , getShrinkingTime ( ).getUnit().name ( ) ) ? 1 : 0 )
					+ ( YamlUtil.setNotSet ( section , "Time-to-start" , time_to_start.getDuration ( ) ) ? 1 : 0 )
					+ ( YamlUtil.setNotSet ( section , "TimeUnit-to-start" , getTimeToStart ( ).getUnit ( ).name ( ) ) ? 1 : 0 );
			return save;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals ( Object obj ) {
		if ( obj == this ) {
			if ( obj instanceof BorderShrink ) {
				BorderShrink other = (BorderShrink) obj;
				return Objects.equals ( location , other.location )
						&& Double.doubleToLongBits ( radiation_damage ) == Double.doubleToLongBits ( other.radiation_damage )
						&& Double.doubleToLongBits ( radius ) == Double.doubleToLongBits ( other.radius )
						&& Objects.equals ( shrinking_time , other.shrinking_time )
						&& Objects.equals ( time_to_start , other.time_to_start );
			}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		long temp;
		temp = Double.doubleToLongBits(radiation_damage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(radius);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((shrinking_time == null) ? 0 : shrinking_time.hashCode());
		result = prime * result + ((time_to_start == null) ? 0 : time_to_start.hashCode());
		return result;
	}

//	@Override
//	public boolean equals(final Object obj) {
//		// return true if is this
//		if (obj == this) {
//			return true;
//		}
//		
//		// check instanceof BorderShrink
//		if (!(obj instanceof BorderShrink)) {
//			return false;
//		}
//		
//		// get other.
//		final BorderShrink other = (BorderShrink) obj;
//		
//		// check location.
//        if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) {
//            return false;
//        }
//        
//        // check is same radius and time.
//		return this.radius == other.radius 
//				&& this.shrinking_time.equals(other.shrinking_time)
//				&& this.time_to_start.equals(other.time_to_start);
//	}
}
