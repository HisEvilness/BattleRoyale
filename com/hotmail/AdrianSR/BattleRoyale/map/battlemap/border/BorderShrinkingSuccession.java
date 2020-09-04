package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;

/**
 * Border shrinking succession.
 * <p>
 * @author AdrianSR / Tuesday 07 July, 2020 / 08:47 AM
 */
public class BorderShrinkingSuccession {
	
	protected final List < BorderShrink > shrinks = new ArrayList < > ( );
	
	public BorderShrinkingSuccession ( ) {
		/* no shrinks */
	}
	
	public BorderShrinkingSuccession ( BorderShrink... shrinks ) {
		for ( BorderShrink shrink : shrinks ) {
			if ( shrink != null && shrink.isValid ( ) ) {
				this.shrinks.add ( shrink );
			}
		}
	}
	
	public BorderShrinkingSuccession ( ConfigurationSection section ) {
		for ( String key : section.getKeys ( false ) ) {
			if ( !section.isConfigurationSection ( key ) ) {
				continue;
			}
			
			ConfigurationSection root = section.getConfigurationSection ( key );
			if ( !root.isConfigurationSection ( "Location" ) ) {
				continue;
			}
			
			TimeUnit shrinking_unit = EnumReflection.getEnumConstant ( TimeUnit.class , 
					root.getString ( "TimeUnit-in-shrinking" ) );
			TimeUnit start_unit = EnumReflection.getEnumConstant ( TimeUnit.class , 
					root.getString ( "TimeUnit-to-start" ) );
			if ( shrinking_unit == null || start_unit == null ) {
				// unspecified time units.
				continue;
			}
			
			ConfigurableLocation        location = ConfigurableLocation.of ( root.getConfigurationSection ( "Location" ) );
			double       radius = root.getDouble ( "Radius" );
			double       damage = root.getDouble ( "RadiationDamage" );
			long shrinking_time = root.getLong ( "Time-in-shrinking" );
			long     start_time = root.getLong ( "Time-to-start" );
			
			// we register the loaded shrink if valid, otherwise, 
			// it makes no sense to registering it.
			if ( radius > 0 || shrinking_time > 0 || start_time > 0 ) {
				shrinks.add ( new BorderShrink ( location , radius , damage , 
						shrinking_time , shrinking_unit , 
						start_time , start_unit ) );
			}
		}
	}

	/**
	 * Add the next border shrink.
	 * 
	 * @param shrink the next {@link BorderShrink}.
	 * @return false if this succession already contains the provided {@code shrink}.
	 */
	public boolean addNextShrinkPoint(final BorderShrink shrink) {
		return (shrink == null || shrinks.contains(shrink)) ? false : shrinks.add(shrink);
	}
	
	/**
	 * Remove border shrink.
	 * 
	 * @param shrink the {@link BorderShrink} to remove.
	 * @return true if correctly removed.
	 */
	public boolean removeShrinkPoint(final BorderShrink shrink) {
		return shrinks.remove(shrink);
	}
	
	/**
	 * Get border shrinks.
	 * <p>
	 * @return the shrinks list.
	 */
	public List<BorderShrink> getShrinks() {
		return this.shrinks;
	}
	
	/**
	 * Gets a list containing the valid border shrinks.
	 * <p>
	 * @return a new list containing the valid shrinks.
	 */
	public List < BorderShrink > getValidShrinks ( ) {
		return getShrinks ( ).stream ( ).filter ( bs -> bs != null && bs.isValid ( ) ).collect ( Collectors.toList ( ) );
	}
	
	/**
	 * @return number of changes made to the configuration.
	 */
	public int saveToConfig ( ConfigurationSection section ) {
		if ( isValid ( ) ) {
			int changes = 0;
			int count   = 0;
			for ( BorderShrink shrink : shrinks ) {
				if ( shrink != null && shrink.isValid ( ) ) {
					changes += shrink.saveToConfig ( section.createSection ( "shrink-" + count ++ ) );
				}
			}
			return changes;
		} else {
			return 0;
		}
	}
	
	/**
	 * @return false if no valid {@link BorderShrink}s was found.
	 */
	public boolean isValid ( ) {
		return shrinks.stream ( ).filter ( BorderShrink :: isValid ).count ( ) > 0;
	}
}


//package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import org.bukkit.configuration.ConfigurationSection;
//
//import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
//import com.hotmail.AdrianSR.core.util.localization.ConfigurableLocation;
//
///**
// * Represents a Battle Royale border shrink succession.
// * <p>
// * @author AdrianSR
// */
//public class BorderShrinkingSuccession {
//	
//	/**
//	 * Class values.
//	 */
//	private final List<BorderShrink> shrinks;
//	
//	/**
//	 * Construct a new Border Shrinking Succession.
//	 */
//	public BorderShrinkingSuccession() {
//		shrinks = new ArrayList<BorderShrink>();
//	}
//	
//	/**
//	 * Construct a new Border Shrinking Succession from config..
//	 */
//	public BorderShrinkingSuccession(final ConfigurationSection sc) {
//		this();
//		
//		// load points.
//		for (String key : sc.getKeys(false)) {
//			// check section.
//			ConfigurationSection ot = sc.getConfigurationSection(key);
//			if (ot == null) {
//				continue;
//			}
//			
//			// check location section.
//			ConfigurationSection loc = ot.getConfigurationSection("Location");
//			if (loc == null) {
//				continue;
//			}
//			
//			// check time unit in shrinking.
//			TimeUnit unit_in_shrinking = ReflectionUtils.getEnumConstant(TimeUnit.class, ot.getString("TimeUnit-in-shrinking"));
//			if (unit_in_shrinking == null) {
//				continue;
//			}
//			
//			// check time unit to start shrinking.
//			TimeUnit unit_to_start = ReflectionUtils.getEnumConstant(TimeUnit.class, ot.getString("TimeUnit-to-start"));
//			if (unit_to_start == null) {
//				continue;
//			}
//			
//			// get and check the time in shrinking, time to start, location and radius.
//			ConfigurableLocation location           = new ConfigurableLocation(loc);
//			double radio           = ot.getDouble("Radius");
//			double damage          = ot.getDouble("RadiationDamage");
//			long time_in_shrinking = ot.getLong("Time-in-shrinking");
//			long time_to_start     = ot.getLong("Time-to-start");
//			if (radio <= 0 || time_in_shrinking <= 0 || time_to_start <= 0) {
//				continue;
//			}
//			
//			// add loaded Shrink.
//			shrinks.add(new BorderShrink(location, radio, damage,
//					time_in_shrinking, unit_in_shrinking,
//					time_to_start,     unit_to_start));
//		}
//	}
//
//	/**
//	 * Add the next border shrink.
//	 * 
//	 * @param shrink the next {@link BorderShrink}.
//	 * @return false if is already added.
//	 */
//	public boolean addNextShrinkPoint(final BorderShrink shrink) {
//		return (shrink == null || shrinks.contains(shrink)) ? false : shrinks.add(shrink);
//	}
//	
//	/**
//	 * Remove border shrink.
//	 * 
//	 * @param shrink the {@link BorderShrink} to remove.
//	 * @return true if correctly removed.
//	 */
//	public boolean removeShrinkPoint(final BorderShrink shrink) {
//		return shrinks.remove(shrink);
//	}
//	
//	/**
//	 * Get border shrinks.
//	 * <p>
//	 * @return the shrinks list.
//	 */
//	public List<BorderShrink> getShrinks() {
//		return this.shrinks;
//	}
//	
//	/**
//	 * Get valid border shrinks.
//	 * <p>
//	 * @return the valid shrinks list.
//	 */
//	public List<BorderShrink> getValidShrinks() {
//		return getShrinks().stream().filter(bs -> bs != null && bs.isValid()).collect(Collectors.toList());
//	}
//	
//	/**
//	 * @return a number > 1 if the config get changes.
//	 */
//	public int saveToConfig(ConfigurationSection section) {
//		// get int.
//		int save = 0;
//		
//		// check.
//		if (!isValid()) {
//			return save;
//		}
//		
//		// save to config.
//		int saved = 0;
//		for (BorderShrink sh : shrinks) {
//			// check is valid.
//			if (sh.isValid()) {
//				// save.
//				save += sh.saveToConfig(section.createSection("shrink-" + saved));
//				
//				// ++
//				saved += 1;
//			}
//		}
//		return save;
//	}
//	
//	/**
//	 * @return true if has more that one valid Shrinks.
//	 */
//	public boolean isValid() {
//		// get valid shrinks
//		int valid = 0;
//		for (BorderShrink sh : shrinks) {
//			if (sh != null && sh.isValid()) {
//				valid++;
//			}
//		}
//		return valid > 0;
//	}
//}
