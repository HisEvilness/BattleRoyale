package com.hotmail.AdrianSR.BattleRoyale.config.money;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.file.filter.YamlFileFilter;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the plugin money configuration class.
 * <p>
 * @author AdrianSR
 */
public enum Money {

	/**
	 * The money of reward.
	 */
	KILL_MONEY("GameReward.kill-money",     10),
	GAME_WIN_MONEY("GameReward.win-money", 100),
	
	/**
	 * The prices for the vehicles.
	 */
	CHICKEN_VEHICLE_COST("Cost.chicken-vehicle-cost", 1500),
	BAT_VEHICLE_COST("Cost.bat-vehicle-cost", 1300),
	BLAZE_VEHICLE_COST("Cost.blaze-vehicle-cost", 1900),
//	DRAGON_VEHICLE_COST("Cost.dragon-vehicle-cost", 5000),
//	WITHER_VEHICLE_COST("Cost.wither-vehicle-cost", 3500),
	COW_VEHICLE_COST("Cost.cow-vehicle-cost", 1200),
	SPIDER_VEHICLE_COST("Cost.spider-vehicle-cost", 2200),
	ENDERMAN_VEHICLE_COST("Cost.enderman-vehicle-cost", 3000),
	WOLF_VEHICLE_COST("Cost.wolf-vehicle-cost", 3000),
	PIG_VEHICLE_COST("Cost.pig-vehicle-cost", 2000),
	IRON_GOLEM_VEHICLE_COST("Cost.iron-golem-vehicle-cost", 2100),
	SHEEP_VEHICLE_COST("Cost.sheep-vehicle-cost", 1100),
	CREEPER_VEHICLE_COST("Cost.creeper-vehicle-cost", 1500),
	
	/**
	 * The prices for the vehicle particles.
	 */
	RAINBOW_PARTICLE_COST("Cost.rainbow-vehicle-particle-cost", 1100),
	FLAMES_PARTICLE_COST("Cost.flames-vehicle-particle-cost", 900),
	RAIN_PARTICLE_COST("Cost.rain-vehicle-particle-cost", 800),
	LAVA_PARTICLE_COST("Cost.lava-vehicle-particle-cost", 1050),
	UNDER_WATER_PARTICLE_COST("Cost.underwater-vehicle-particle-cost", 800),
	FIREWORK_PARTICLE_COST("Cost.firework-vehicle-particle-cost", 950),
	SMOKE_PARTICLE_COST("Cost.smoke-vehicle-particle-cost", 800),
	CRIT_PARTICLE_COST("Cost.crit-vehicle-particle-cost", 800),
	HATER_PARTICLE_COST("Cost.hater-vehicle-particle-cost", 800),
	ENCHANTMENT_PARTICLE_COST("Cost.enchantment-vehicle-particle-cost", 800),
	HEARTS_PARTICLE_COST("Cost.hearts-vehicle-particle-cost", 800),
	
	/**
	 * The pirces for the parachute colors.
	 */
	RED_PARACHUTE_COLOR_COST("Cost.red-parachute-color-cost", 800),
	BLUE_PARACHUTE_COLOR_COST("Cost.blue-parachute-color-cost", 800),
	GREEN_PARACHUTE_COLOR_COST("Cost.green-parachute-color-cost", 800),
	YELLOW_PARACHUTE_COLOR_COST("Cost.yellow-parachute-color-cost", 800),
	WHITE_PARACHUTE_COLOR_COST("Cost.white-parachute-color-cost", 800),
	;
	
	public static void setConfigurationFile(File yml_file) {
		Validate.notNull(yml_file, "The file cannot be null!");
		Validate.isTrue(new YamlFileFilter().accept(yml_file), "The given file must be a valid .yml file!");
		Validate.isTrue(yml_file.isFile(), "The file must exist!");
		
		setConfiguration(YamlConfigurationComments.loadConfiguration(yml_file));
	}
	
	public static void setConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		Arrays.asList(Money.values()).forEach(item -> item.load(section));
	}
	
	public static int saveDefaultConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		List < Money > save = Arrays.asList ( Money.values ( ) ).stream ( )
				.filter ( item -> !section.isSet ( 
						YamlUtil.alternatePathSeparator ( item.key , section.getRoot ( ).options ( ).pathSeparator ( ) ) ) )
				.collect ( Collectors.toList ( ) );
		
		save.forEach ( item -> section.set ( YamlUtil.alternatePathSeparator ( 
				item.key , section.getRoot ( ).options ( ).pathSeparator ( ) ) , item.default_value ) );
		return save.size ( );
		
//		List<Money> save = Arrays.asList(Money.values()).stream()
//				.filter(item -> !section.isSet(YmlUtils.getFixPathSeparator(section.getRoot(), item.key)))
//				.collect(Collectors.toList());
//		save.forEach(item -> section.set(YmlUtils.getFixPathSeparator(section.getRoot(), item.key), item.default_value));
//		return save.size();
	}
	
	private final String           key;
	private final Object default_value;
	private       Object         value;
	private final Class<?>  value_type;

	/**
	 * Config enum constructor.
	 *  <p>
	 * @param key the path.
	 * @param default_value the default value.
	 * @param comment the comment for the line.
	 */
	Money(String key, Object default_value) {
		this.key           = key;
		this.default_value = default_value;
		this.value_type    = default_value.getClass();
	}

	public String getKey() {
		return key;
	}
	
	public Object getRaw(boolean default_value) {
		return default_value ? this.default_value : value;
	}
	
	public Object getRaw() {
		return getRaw(false);
	}
	
	public String getAsString() {
		return getAsString(false);
	}
	
	public String getAsString(boolean default_value) {
		validate(String.class);
		return (String) ( default_value ? this.default_value : value );
	}
	
	public String getAsNotNullString() {
		return getAsString() != null ? getAsString() : getAsString(true);
	}
	
	public Integer getAsInteger() {
		return getAsInteger(false);
	}
	
	public Integer getAsInteger(boolean default_value) {
		validate(Integer.class);
		return (Integer) ( default_value ? this.default_value : value );
	}
	
	public Integer getAsNotNullInteger() {
		return getAsInteger() != null ? getAsInteger() : 0;
	}
	
	public Double getAsDouble() {
		return getAsDouble(false);
	}
	
	public Double getAsDouble(boolean default_value) {
		validate(Double.class);
		return (Double) ( default_value ? this.default_value : value );
	}
	
	public ArrayList<?> getAsList() {
		return getAsList(false);
	}
	
	public ArrayList<?> getAsList(boolean default_value) {
		validate(ArrayList.class);
		return (ArrayList<?>) ( default_value ? this.default_value : value );
	}
	
	public <T extends Enum<T>> T getAsEnumConstant(Class<T> enum_class) {
		return getAsEnumConstant(enum_class, false);
	}
	
	public <T extends Enum<T>> T getAsEnumConstant(Class<T> enum_class, boolean default_value) {
		if (!(this.default_value instanceof String)) {
			throw new UnsupportedOperationException("Unusupported for using from this Config item!");
		}
		return EnumReflection.getEnumConstant(enum_class, getAsString(default_value));
	}
	
	private void validate(Class<?> clazz) {
		Validate.isTrue(clazz == this.value_type, "This config item is not an instance of " + clazz.getSimpleName());
	}
	
	public void load(ConfigurationSection section) {
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		String fixed_key = YamlUtil.alternatePathSeparator ( key , section.getRoot ( ).options ( ).pathSeparator ( ) );
		String key_name  = fixed_key.substring(fixed_key.lastIndexOf(section.getRoot().options().pathSeparator()) + 1);
		Object       raw = section.get(fixed_key);
		if (raw != null && this.value_type == raw.getClass()) {
			this.value = raw;
		} else {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
					"(Money config) It was not possible to correctly determine the configuration of '" + key_name + "'!",
					BattleRoyale.getInstance ( ) );
		}
		
//		String fixed_key = YmlUtils.getFixPathSeparator(section.getRoot(), key);
//		String key_name  = fixed_key.substring(fixed_key.lastIndexOf(section.getRoot().options().pathSeparator()) + 1);
//		Object       raw = section.get(fixed_key);
//		if (raw != null && this.value_type == raw.getClass()) {
//			this.value = raw;
//		} else {
//			ConsoleUtil.sendPluginMessage ( ChatColor.RED,
//					"(Money config) It was not possible to correctly determine the configuration of '" + key_name + "'!",
//					BattleRoyale.getInstance());
//		}
	}
	
	public void set(Object value) {
		Validate.notNull(value, "The value cannot be null!");
		
		if (this.value_type.isAssignableFrom(value.getClass()) || this.value_type.equals(value.getClass())) {
			this.value = value;
		}
		throw new UnsupportedOperationException("Money." + name() + " is not an instance of " + value.getClass().getSimpleName());
	}
	
	@Override
	public String toString() {
		return getAsString();
	}
}