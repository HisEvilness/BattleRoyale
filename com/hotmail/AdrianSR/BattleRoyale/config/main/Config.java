package com.hotmail.AdrianSR.BattleRoyale.config.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.file.filter.YamlFileFilter;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the plugin configuration loader class.
 * <p>
 * @author AdrianSR / Tuesday 05 November, 2019 / 08:48 AM
 */
public enum Config {
	
	BATTLE_MODE("BattleMode.file-name", "Solo.yml", "The name of the .yml/.jar file of the battle mode for this arena."),
	BATTLE_MODE_TYPE("BattleMode.type", "simple", "The type of battle mode file to load, use 'simple' for simple battle modes, and 'complex' for complex battle modes."),
	
	SERVER_NAME("server-name", "server1", "The display name of this arena."),
	MAP_TO_LOAD("map-to-use-on-game", "Map Folder Name", "The name of the folder of your battle map that you previously set."),
	MAX_PLAYERS("max-players", 100, "The maximum number of players who can join this arena."),
	BUNGEECORD("bungeecord", true, "Enable bungeecord?"),
	USE_STATUS_MOTD("use-status-motd", false, "If enabled, this plugin will show the status of the game on the motd of this server."),
	
	AUTO_START_USE ("AutoStart.use", true, "If enabled, the game will start automatically when the minimum number of players is met."),
	AUTO_START_MIN_PLAYERS("AutoStart.min-players", 15, "The minimum number of online players that must be on the server for the game to start automatically."),
	AUTO_START_COUNTDOWN_SECONDS("AutoStart.countdown-seconds", 10, "The time for the countdown. (In seconds)"),
	
	AUTO_STOP_SECONDS("AutoStop.countdown-seconds", 15, "The time for the countdown at the end of the game. (In seconds)"),
	AUTO_STOP_COMMAND("AutoStop.command-to-perform", "stop", "The command to stop the server at the end of the game."),
	
	MAP_LOAD_ON_SEVER_START("Other.map-load-on-sever-start", false, "If enabled, the map to use on game will be loaded when the server starts."),

	LOBBY_ITEM_BUNGEE_SERVER_TARGET("Other.go-lobby-item-send-players-to", "target-server", "The name of the target server in the config.yml of your bungeecord"),
	
	KICK_PLAYERS_ON_GAME_END("Other.kick-players-when-game-end", true, "If enabled, any player that is online at the end of the game will be kicked."),
	TARGET_SERVER_ON_GAME_END("Other.send-players-to-when-game-end", "target-server", "The name of the target server in the config.yml of your bungeecord where send the players at the end of the game."),
	SCOREBOARDS_REFRESH_DELAY("Other.scoreboards-refresh-delay", 20, "The refresh delay of the scoreboards of this plugin. (In server ticks. 20 ticks == 1 second)"),
	CARDINAL_BAR_COLOR("Other.cardinal-bar-color", "GOLD", "The color of the cardinal bar."),
	VEHICLE_TRAVEL_VELOCITY("Other.vehicle-travel-velocity", 1.4D, "The travel velocity of the flying vehicles."),
	VEHICLE_SECONDS_TO_DISMOUNT("Other.vehicle-seconds-to-dismount", 7, "The time in seconds that the players will have to wait before dismounting the flying vehicles."),
	VEHICLE_AUTO_PARACHUTE_ON_JUMP("Other.vehicle-auto-parachute", true, "If enabled, the parachute of the players will be opened automatically when close to the ground after dismounting the flying vehicle."),
	MINIMAP_LOAD_ON_SERVER_START("Other.minimap-load-on-sever-start", false, "If enabled, the minimap of the map to use on game will be loaded when the server starts."),
//	MINIMAP_SAFE_LOAD("Other.minimap-safe-load", false, "If enabled, the minimap will be loaded slowly but safely. (Recommended for servers with few resources)"),
//	MINIMAP_SAFE_LOAD_MODE("Other.minimap-safe-load-mode", 2, "The load process will be most safely the greater load mode."),
	MAP_AUTO_LOOT_CHESTS("Other.map-auto-loot-chests", false, "If enabled, any chest on the map will be filled automatically."),
	AIR_SUPPLY_USE("Other.use-air-supply", true, "Enable the air suppply?"),
	AIR_SUPPLY_AMOUNT("Other.air-supply-amount-per-point" , -1 , "The amount of air supplies to drop per point, -1 to auto-calculate."),
	GAME_END_FIREWORKS("Other.fireworks-at-the-end", true, "Enable fireworks at the end of the game?"),
	
	GAME_START_PLAYERS_CUSTOM_COMMANDS("Other.perform-custom-commands.when-game-start.for-players", new ArrayList<String>(Arrays.asList("", "")), ""),
	GAME_START_CONSOLE_CUSTOM_COMMANDS("Other.perform-custom-commands.when-game-start.for-console", new ArrayList<String>(Arrays.asList("", "")), ""),
	
	GAME_END_PLAYERS_CUSTOM_COMMANDS("Other.perform-custom-commands.when-game-end.for-players", new ArrayList<String>(Arrays.asList("", "")), ""),
	GAME_END_CONSOLE_CUSTOM_COMMANDS("Other.perform-custom-commands.when-game-end.for-console", new ArrayList<String>(Arrays.asList("", "")), ""),
	
	/**
	 * MySQL config.
	 */
	MYSQL_USE("MySQL.use", false, "Enable MySQL?"),
	MYSQL_HOST("MySQL.host", "", "MySQL host"),
	MYSQL_PORT("MySQL.port", 3306, "MySQL port"),
	MYSQL_DATABASE("MySQL.database", "", "MySQL database name"),
	MYSQL_USERNAME("MySQL.username", "", "MySQL user name"),
	MYSQL_PASSWORD("MySQL.password", "", "MySQL user password"),
	
	; // END!
	
//	public static final String START_END_COMMANDS_SECTION         = "perform-custom-commands";
//	public static final String START_COMMANDS_SECTION             = "when-game-start";
//	public static final String END_COMMANDS_SECTION               = "when-game-end";
//	public static final String START_END_COMMANDS_FOR_PLAYERS_KEY = "for-players";
//	public static final String START_END_COMMANDS_FOR_CONSOLE_KEY = "for-console";
	
	public static void setConfigurationFile(File yml_file) {
		Validate.notNull(yml_file, "The file cannot be null!");
		Validate.isTrue ( new YamlFileFilter ( ).accept ( yml_file ) , "The given file must be a valid .yml file!");
		Validate.isTrue(yml_file.isFile(), "The file must exist!");
		
		setConfiguration(YamlConfigurationComments.loadConfiguration(yml_file));
	}
	
	public static void setConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		Arrays.asList(Config.values()).forEach(item -> item.load(section));
	}
	
	public static int saveDefaultConfiguration ( ConfigurationSection section ) {
		Validate.notNull ( section , "The configuration cannot be null!" );
		Validate.notNull ( section.getRoot ( ) , "The root of the configuration section cannot be null!" );
		
		List < Config > save = Arrays.asList ( Config.values ( ) ).stream ( )
				.filter ( item -> !section.isSet ( 
						YamlUtil.alternatePathSeparator ( item.key , section.getRoot ( ).options ( ).pathSeparator ( ) ) ) )
				.collect ( Collectors.toList ( ) );
		return save.size ( );
		
//		List<Config> save = Arrays.asList(Config.values()).stream()
//				.filter(item -> !section.isSet(YmlUtils.getFixPathSeparator(section.getRoot(), item.key)))
//				.collect(Collectors.toList());
//		save.forEach(item -> section.set(YmlUtils.getFixPathSeparator(section.getRoot(), item.key), item.default_value));
//		return save.size();
	}
	
	public static int saveCommentedDefaultConfiguration(YamlConfigurationComments yaml) {
		Validate.notNull(yaml, "The configuration cannot be null!");
		
		List<Config> save = Arrays.asList(Config.values())
				.stream()
				.filter(item -> !yaml.isSet(item.key))
				.collect(Collectors.toList());
		
		save.forEach ( item -> {
			String key = YamlUtil.alternatePathSeparator ( item.key , yaml.options ( ).pathSeparator ( ) );
			
			yaml.set ( key , item.default_value );
			yaml.options ( ).comment ( key , item.path_comment );
		});
		
//		save.forEach(item -> {
//			String fixed_key = YmlUtils.getFixPathSeparator(yaml, item.key);
//
//			yaml.set(fixed_key, item.default_value);
//			yaml.options().comment(YmlUtils.getConfigurationSection(yaml, fixed_key), YmlUtils.getPathName(yaml, item.key),
//					item.path_comment);
//		});
		return save.size();
	}

	private final String           key;
	private final Object default_value;
	private       Object         value;
	private final Class<?>  value_type;
	private final String  path_comment;

	/**
	 * Config enum constructor.
	 *  <p>
	 * @param key the path.
	 * @param default_value the default value.
	 * @param comment the comment for the line.
	 */
	Config(String key, Object default_value, String comment) {
		this.key           = key;
		this.default_value = default_value;
		this.value_type    = default_value.getClass();
		this.path_comment  = comment;
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
	
	public boolean getAsBoolean() {
		return getAsBoolean(false);
	}
	
	public boolean getAsBoolean(boolean default_value) {
		validate(Boolean.class);
		return (Boolean) ( default_value ? this.default_value : value );
	}
	
	public String getAsString() {
		return getAsString(false);
	}
	
	public String getAsString(boolean default_value) {
		validate(String.class);
		return (String) ( default_value ? this.default_value : value );
	}
	
	public String getAsColoredString ( boolean default_value ) {
		return StringUtil.translateAlternateColorCodes ( getAsNotNullString ( ) );
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
		return EnumReflection.getEnumConstant ( enum_class , getAsString ( default_value ) );
	}
	
	private void validate(Class<?> clazz) {
		Validate.isTrue(clazz == this.value_type, "This config item is not an instance of " + clazz.getSimpleName());
	}
	
	public void load ( ConfigurationSection section ) {
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		String fixed_key = YamlUtil.alternatePathSeparator ( key , section.getRoot ( ).options ( ).pathSeparator ( ) );
		String key_name  = fixed_key.substring(fixed_key.lastIndexOf(section.getRoot().options().pathSeparator()) + 1);
		Object       raw = section.get(fixed_key);
		if (raw != null && this.value_type == raw.getClass()) {
			this.value = raw;
		} else {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
					"(Main config) It was not possible to correctly determine the configuration of '" + key_name + "'!" ,
					BattleRoyale.getInstance ( ) );
		}
		
//		String fixed_key = YmlUtils.getFixPathSeparator(section.getRoot(), key);
//		String key_name  = fixed_key.substring(fixed_key.lastIndexOf(section.getRoot().options().pathSeparator()) + 1);
//		Object       raw = section.get(fixed_key);
//		if (raw != null && this.value_type == raw.getClass()) {
//			this.value = raw;
//		} else {
//			ConsoleUtil.sendPluginMessage ( ChatColor.RED,
//					"(Main config) It was not possible to correctly determine the configuration of '" + key_name + "'!",
//					BattleRoyale.getInstance());
//		}
	}
	
	public void set(Object value) {
		Validate.notNull(value, "The value cannot be null!");
		
		if (this.value_type.isAssignableFrom(value.getClass()) || this.value_type.equals(value.getClass())) {
			this.value = value;
		}
		throw new UnsupportedOperationException("Config." + name() + " is not an instance of " + value.getClass().getSimpleName());
	}
	
	@Override
	public String toString() {
		return getAsString();
	}
}