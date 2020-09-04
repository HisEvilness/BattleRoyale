package com.hotmail.AdrianSR.BattleRoyale.config.lang;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.file.filter.YamlFileFilter;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the Language plugin configuration class.
 * <p>
 * @author AdrianSR / Tuesday 05 November, 2019 / 11:27 AM
 */
public enum Lang {
	
	/**
	 * INFO.
	 */
	INFO_BORDER_SHRINK_TITLE("info-border-shrink-title", ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Border shrinking in " + Lang.NUMBER_REPLACEMENT_KEY),
	INFO_BORDER_SHRINKING_STARTED_TITLE("info-border-shrinking-started-title", ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Border shrinking..."),
	INFO_BORDER_STOPPED_TITLE("info-border-stopped-title", ChatColor.GREEN + ChatColor.BOLD.toString() + "Border stopped..."),
	
	/**
	 * SERVER FULL MESSAGE
	 */
	SERVER_FULL("server-full", ChatColor.RED + "!Server Full!"),
	
	/**
	 * MOTD
	 */
	MOTD_WAITING("motd-waiting", ChatColor.GREEN + "Waiting for Players" + "%N%" + ChatColor.LIGHT_PURPLE + "!Battle Royale!"),
	MOTD_IN_GAME("motd-in-game", ChatColor.YELLOW + "In Game" + "%N%" + ChatColor.LIGHT_PURPLE + "!Battle Royale!"),
	
	/**
	 * GAME SCOREBOARD.
	 */
	/* BORDER */
	SCOREBOARD_BORDER_SHRINK("Scoreboard.game.border-shrink", "Border Shrink in: " + ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY),
	SCOREBOARD_BORDER_SHRINKING("Scoreboard.game.border-shrinking", ChatColor.YELLOW + "Border Shrinking: "  + Lang.NUMBER_REPLACEMENT_KEY),
	SCOREBOARD_BORDER_STOPED("Scoreboard.game.border-stoped", ChatColor.GREEN  + "Border Stopped"),
	
	/* PLAYERS-TEAMS LEFT */
	SCOREBOARD_TEAMS_LEFT("Scoreboard.game.teams-left", ChatColor.WHITE + "Teams Left: " + ChatColor.GREEN + Lang.WORD_REPLACEMENT_KEY),
	SCOREBOARD_PLAYERS_LEFT("Scoreboard.game.players-left", ChatColor.WHITE + "Players Left: " + ChatColor.GREEN + Lang.WORD_REPLACEMENT_KEY),
	
	/* KILLS */
	SCOREBOARD_SOLO_KILLS("Scoreboard.game.solo-kills", ChatColor.WHITE + "Kills: " + ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY),
	SCOREBOARD_TEAM_KILLS("Scoreboard.game.team-kills", ChatColor.WHITE + "Team Kills: " + ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY),
	SCOREBOARD_GAME_KILLS("Scoreboard.game.game-kills", ChatColor.WHITE + "Game Kills: " + ChatColor.LIGHT_PURPLE + Lang.NUMBER_REPLACEMENT_KEY),
	
	SCOREBOARD_SAFE_TEXT("Scoreboard.game.safe", ChatColor.RESET + ChatColor.GREEN.toString() + "SAFE"),
	SCOREBOARD_UNSAFE_TEXT("Scoreboard.game.un-safe", ChatColor.RESET + ChatColor.RED.toString() + "UNSAFE"),
	
	/* TEAM MATES */
	SCOREBOARD_TEAM_MEMBER_HEALTHFULLY("Scoreboard.game.team-member-healthfully-line",       ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY + Global.HEART + " " + Lang.PLAYER_REPLACEMENT_KEY),
	SCOREBOARD_TEAM_MEMBER_REGULAR_HEALTH("Scoreboard.game.team-member-regular-health-line", ChatColor.YELLOW + Lang.NUMBER_REPLACEMENT_KEY + Global.HEART + " " + Lang.PLAYER_REPLACEMENT_KEY),
	SCOREBOARD_TEAM_MEMBER_BAD_HEALTH("Scoreboard.game.team-member-bad-health-line",         ChatColor.RED + Lang.NUMBER_REPLACEMENT_KEY + Global.HEART + " " + Lang.PLAYER_REPLACEMENT_KEY),
	SCOREBOARD_TEAM_MEMBER_KILLED("Scoreboard.game.team-member-killed-line",                 ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString() + Lang.PLAYER_REPLACEMENT_KEY),
	SCOREBOARD_YOU_SUFIX("Scoreboard.game.you-name-sufix", ChatColor.RESET + ChatColor.GRAY.toString() + "(You)"),
	
	/**
	 * LOBBY SCOREBOARD.
	 */
	SCOREBOARD_LOBBY_STARTING("Scoreboard.lobby.starting", "Starting in: " + ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY),
	SCOREBOARD_LOBBY_PLAYERS_TO_START("Scoreboard.lobby.players-to-start", "Min Players: " + ChatColor.GREEN + Lang.NUMBER_REPLACEMENT_KEY),
	
	/**
	 * CARDINAL BAR.
	 */
	CARDINAL_BAR_NORTH("CardinalBar.north-letter", "N"),
	CARDINAL_BAR_SOUTH("CardinalBar.south-letter", "S"),
	CARDINAL_BAR_WEST("CardinalBar.west-letter",   "W"),
	CARDINAL_BAR_EAST("CardinalBar.east-letter",   "E"),
	CARDINAL_BAR_SOUTH_EAST_NAME("CardinalBar.south-east-name", "SE"),
	CARDINAL_BAR_SOUTH_WEST_NAME("CardinalBar.south-west-name", "SW"),
	CARDINAL_BAR_NORTH_EAST_NAME("CardinalBar.north-east-name", "NE"),
	CARDINAL_BAR_NORTH_WEST_NAME("CardinalBar.north-west-name", "NW"),
	
	/**
	 * AUTO START ABORTED MESSAGE.
	 */
	AUTO_START_ABORTED_MESSAGE("AutoStart.aborted", ChatColor.RED + "The game will not start until there are at least " + Lang.NUMBER_REPLACEMENT_KEY + " players connected!"),
	
	/**
	 * MEMER DEATH MESSAGES.
	 */
	MEMBER_KILLED_MESSAGE("Death.member-killed", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " was killed by " + Lang.KILLER_REPLACEMENT_KEY + "."),
	MEMBER_DEATH_BY_FALL_MESSAGE("Death.member-death-by-fall", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " fell from a high place."),
	MEMBER_DEATH_UNKNOWN_MESSAGE("Death.member-death-unknown", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " died from unknown cause."),
	MEMBER_DEATH_VOID_MESSAGE("Death.member-death-void", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " fell out of the world."),
	MEMBER_DEATH_BY_RADIATION_MESSAGE("Death.member-death-radiation", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " was killed by the radiation."),
	MEMBER_DEATH_BY_EXPLOSION_MESSAGE("Death.member-death-explosion", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " was killed by a explosion."),
	MEMBER_DEATH_BLEEDING_OUT_MESSAGE("Death.member-death-bleeding-out", ChatColor.WHITE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.GRAY + ChatColor.BOLD.toString() + " died bleeding out."),
	
	/**
	 * MEMER KNOCKED MESSAGES.
	 */
	MEMBER_KNOCKED_MESSAGE("Knocked.member-knocked", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.YELLOW + ChatColor.BOLD.toString() + " was knocked down."),
	MEMBER_KNOCKED_BY_MESSAGE("Knocked.member-knocked-by", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.YELLOW + ChatColor.BOLD.toString() + " was knocked down by " + Lang.KNOCKER_REPLACEMENT_KEY),
	
	/**
	 * MEMBER POSITION TITLES.
	 */
	MEMBER_SOLO_TEAM_POSITION_TITLE("Position.position-title", ChatColor.YELLOW + ChatColor.BOLD.toString() + "#" + Lang.NUMBER_REPLACEMENT_KEY + ChatColor.RED + ChatColor.BOLD.toString() + " GAME OVER"),
	TIE_GAME_TITLE("Position.tie-game-title", ChatColor.WHITE + ChatColor.BOLD.toString() + "TIE GAME"),
	MEMBER_SOLO_WINNER_POSITION_TITLE("Position.solo-winner-title", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "#" + Lang.NUMBER_REPLACEMENT_KEY + ChatColor.GREEN + ChatColor.BOLD.toString() + " YOU WON"),
	MEMBER_TEAM_WINNER_POSITION_TITLE("Position.team-winner-title", ChatColor.GREEN + ChatColor.BOLD.toString() + "!YOUR TEAM WON!"),
	POSITIONS_SUBTITLE("Position.screen-subtitle", ChatColor.AQUA + "Battle Royale"),
	
	/**
	 * MAIN VEHICLE:
	 */
	BAR_CANNOT_DISMOUNT_VEHICLE("Bar.main-vehicle.cannot-dismount", ChatColor.RED + "CANNOT DISMOUNT RIGHT NOW!"),
	BAR_SECONDS_TO_DISMOUNT("Bar.main-vehicle.seconds-to-dismount", ChatColor.LIGHT_PURPLE + "YOU CAN DISMOUNT IN " + ChatColor.AQUA + Lang.NUMBER_REPLACEMENT_KEY),
	BAR_DISMOUNT_VEHICLE("Bar.main-vehicle.dismount", ChatColor.LIGHT_PURPLE + "PRESS " + ChatColor.AQUA + "SNEAK" + ChatColor.LIGHT_PURPLE + " TO DISMOUNT"),
	TITLE_DISMOUNT_VEHICLE("Title.main-vehicle-dismount", ChatColor.LIGHT_PURPLE + "PRESS " + ChatColor.AQUA + "SNEAK" + ChatColor.LIGHT_PURPLE + " TO DISMOUNT"),
	
	/**
	 * RESPAWNING TITLE.
	 */
	RESPAWNING_SUBTITLE("Title.respawning", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "RESPAWNING IN " + Lang.NUMBER_REPLACEMENT_KEY),
	
	/**
	 * BAR. KNOCK MESSAGES.
	 */
	BAR_BLEEDING_OUT("Bar.knocked.bleeding-out", ChatColor.RED + ChatColor.BOLD.toString() + "YOU ARE BLEEDING OUT!"),
	BAR_BEING_REANIMATED("Bar.knocked.in-resuscitation", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "YOU ARE BEING REANIMATED!"),
	BAR_REANIMATE_TEAM("Bar.knocked.revive-team", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "SNEAK TO REANIMATE"),
	BAR_REANIMATING_TEAM("Bar.knocked.reanimating-team", ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "REANIMATING " + Lang.WORD_REPLACEMENT_KEY),
	BAR_REANIMATED_TEAM("Bar.knocked.reanimated-team", ChatColor.GREEN + ChatColor.BOLD.toString() + "!REANIMATED!"),
	
	/**
	 * TITLE. KNOCK MESSAGES
	 */
	TITLE_REANIMATION_PROGRESS ( "Title.knocked.reanimation-progress" , ChatColor.GOLD + ChatColor.BOLD.toString ( ) + Lang.NUMBER_REPLACEMENT_KEY ),
	
	/**
	 * BAR. PARACHUTE
	 */
	BAR_HAVE_PARACHUTE("Bar.parachute.sneak-to-open", ChatColor.LIGHT_PURPLE + "TO PARACHUTE PRESS" + ChatColor.AQUA + " SNEAK"),
	
	/**
	 * JOIN SEVER.
	 */
	KICK_CANNOT_JOIN_SERVER_MESSAGE("kick-cannot-join-server-message", ChatColor.RED + "You cannot join server while the game is running!"),
	
	/**
	 * CHAT FORMAT.
	 */
	CHAT_FORMAT("Chat.chat-format", Lang.CHAT_MODE_REPLACEMENT_KEY + " " + Lang.RANGE_REPLACEMENT_KEY + " " + Lang.PLAYER_REPLACEMENT_KEY + "&r&f: " + Lang.WORD_REPLACEMENT_KEY),
	CHAT_TEAM_MODE("Chat.TeamMode-VariableReplacer",     ChatColor.RED + "[TEAM]"),
	CHAT_GLOBAL_MODE("Chat.GlobalMode-VariableReplacer", ChatColor.RED + "[GLOBAL]"),
	
	/**
	 * MENUS BACK ITEM NAME.
	 */
	MENUS_BACK_ITEM_NAME("menus-back-item-name", ChatColor.RED + "Back"),
	
	/**
	 * VEHICLES SELECTOR/SHOP MENU.
	 */
	VEHICLES_SELECTOR_MENU_NAME("CosmeticMenus.vehicle-selector-menu-name", ChatColor.BLUE + "!Select your fliying vehicle!"),
	VEHICLES_SELECTOR_ITEM_NAME("CosmeticMenus.vehicle-selector-item-name", ChatColor.AQUA + "Select '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	VEHICLES_VEHICLE_SELECTED_MESSAGE("CosmeticMenus.vehicle-selected-message", ChatColor.GREEN + "Vehicle Selected!"),
	VEHICLES_SHOP_ITEM_OPEN("CosmeticMenus.vehicle-item-open-name", ChatColor.LIGHT_PURPLE + "!Buy a vehicle!"),
	VEHICLES_SHOP_MENU_NAME("CosmeticMenus.vehicle-shop-name",          ChatColor.BLUE + "Fliying vehicle shop"),
	VEHICLES_SHOP_CANNOT_OPEN("CosmeticMenus.vehicle-cannot-open-shop", ChatColor.RED + "You have already bought all the vehicles available. :)"),
	VEHICLES_SHOP_ITEM_NAME("CosmeticMenus.vehicle-shop-item-name", ChatColor.LIGHT_PURPLE + "Click to buy the Vehicle '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	VEHICLES_PURCHASED_MESSAGE("CosmeticMenus.vehicle-purchased-message", ChatColor.GREEN + "Vehicle Purchased!"),
	
	/**
	 * VEHICLE PARTICLES SELECTOR/SHOP MENU.
	 */
	VEHICLE_PARTICLES_SELECTOR_MENU_NAME("CosmeticMenus.vehicle-particles-selector-menu-name", ChatColor.BLUE + "!Particles for your vehicle!"),
	VEHICLE_PARTICLES_SELECTOR_ITEM_NAME("CosmeticMenus.vehicle-particles-selector-item-name", ChatColor.AQUA + "Select particles '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	VEHICLE_PARTICLES_SELECTED_MESSAGE("CosmeticMenus.vehicle-particles-selected-message", ChatColor.GREEN + "Particles for your Vehicle Selected!"),
	
	VEHICLE_PARTICLES_SHOP_ITEM_OPEN("CosmeticMenus.vehicle-particles-item-open-name", ChatColor.LIGHT_PURPLE + "!Buy a particles!"),
	VEHICLE_PARTICLES_SHOP_MENU_NAME("CosmeticMenus.vehicle-particles-shop-name",          ChatColor.BLUE + "Vehicle Particles shop"),
	VEHICLE_PARTICLES_SHOP_CANNOT_OPEN("CosmeticMenus.vehicle-particles-cannot-open-shop", ChatColor.RED + "You have already bought all the vehicle particles available. :)"),
	VEHICLE_PARTICLES_SHOP_ITEM_NAME("CosmeticMenus.vehicle-particles-shop-item-name", ChatColor.LIGHT_PURPLE + "Click to buy the Particle '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	VEHICLE_PARTICLES_PURCHASED_MESSAGE("CosmeticMenus.particle-purchased-message", ChatColor.GREEN + "Particle Purchased!"),
	
	/**
	 * PARACHUTE COLORS SELECTOR/SHOP MENU.
	 */
	PARACHUTE_COLOR_SELECTOR_MENU_NAME("CosmeticMenus.parachute-color-selector-menu-name", ChatColor.BLUE + "!Select a parachute color!"),
	PARACHUTE_COLOR_SELECTOR_ITEM_NAME("CosmeticMenus.parachute-color-selector-item-name", ChatColor.AQUA + "Select color '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	PARACHUTE_COLOR_SELECTED_MESSAGE("CosmeticMenus.parachute-color-selected-message", ChatColor.GREEN + "Color Selected!"),
	
	PARACHUTE_COLOR_SHOP_MENU_NAME("CosmeticMenus.parachute-color-shop-name",      ChatColor.BLUE + "Parachute colors shop"),
	PARACHUTE_COLOR_SHOP_ITEM_OPEN("CosmeticMenus.parachute-color-item-open-name", ChatColor.LIGHT_PURPLE + "!Buy a parachute color!"),
	PARACHUTE_COLOR_SHOP_CANNOT_OPEN("CosmeticMenus.parachute-color-cannot-open-shop", ChatColor.RED + "You have already bought all the parachute colors available. :)"),
	PARACHUTE_COLOR_SHOP_ITEM_NAME("CosmeticMenus.parachute-color-shop-item-name", ChatColor.LIGHT_PURPLE + "Click to buy the Parachute color '" + Lang.WORD_REPLACEMENT_KEY + "'"),
	PARACHUTE_COLOR_PURCHASED_MESSAGE("CosmeticMenus.parachute-color-purchased-message", ChatColor.GREEN + "Parachute color Purchased!"),
	
	/**
	 * TEAM SELECTOR MENU.
	 */
	TEAM_SELECTOR_MENU_NAME("TeamSelector.menu-name", ChatColor.BLUE + "!Join a Team!"),
	TEAM_SELECTOR_JOIN_ITEM_NAME("TeamSelector.join-item-name", ChatColor.LIGHT_PURPLE + "Join the Team #" + Lang.NUMBER_REPLACEMENT_KEY),
	TEAM_SELECTOR_TEAM_JOINED_MESSAGE("TeamSelector.team-joined-message", ChatColor.GREEN + "Team joined!"),
	TEAM_SELECTOR_LEAVE_ITEM_NAME("TeamSelector.leave-item-name", ChatColor.RED + "Leave my Team"),
	TEAM_SELECTOR_TEAM_LEFT_MESSAGE("TeamSelector.team-left-message", ChatColor.GREEN + "Team left!"),
	TEAM_SELECTOR_ITEM_MEMBERS("TeamSelector.item-members", ChatColor.LIGHT_PURPLE + "members: " + Lang.NUMBER_REPLACEMENT_KEY),
	
	/**
	 * SHOPS.
	 */
	SHOPS_NOT_ENOUGH_MONEY_MESSAGE("CosmeticMenus.shops-not-enough-money-message", ChatColor.RED + "You do not have enough money to buy this cosmetic!"),
	SHOPS_PURCHASING_SYSTEM_NOT_AVAILABLE_MESSAGE("CosmeticMenus.shops-purchasing-not-available-message", ChatColor.RED + "The purchasing system is not available at this moment, try again later."),
	
	/**
	 * VEHICLES NAMES.
	 */
	VEHICLE_CHICKEN_SCREEN_NAME("CosmeticMenus.vehicle-chicken-screen-name", "Chicken"),
	VEHICLE_BAT_SCREEN_NAME("CosmeticMenus.vehicle-bat-screen-name", "Bat"),
	VEHICLE_BLAZE_SCREEN_NAME("CosmeticMenus.vehicle-blaze-screen-name", "Blaze"),
//	VEHICLE_DRAGON_SCREEN_NAME("CosmeticMenus.vehicle-dragon-screen-name", "Dragon"),
//	VEHICLE_WITHER_SCREEN_NAME("CosmeticMenus.vehicle-whiter-screen-name", "Wither"),
	VEHICLE_COW_SCREEN_NAME("CosmeticMenus.vehicle-cow-screen-name", "Cow"),
	VEHICLE_SPIDER_SCREEN_NAME("CosmeticMenus.vehicle-spider-screen-name", "Spider"),
	VEHICLE_ENDERMAN_SCREEN_NAME("CosmeticMenus.vehicle-enderman-screen-name", "Enderman"),
	VEHICLE_WOLF_SCREEN_NAME("CosmeticMenus.vehicle-wolf-screen-name", "Wolf"),
	VEHICLE_HORSE_SCREEN_NAME("CosmeticMenus.vehicle-horse-screen-name", "Horse"),
	VEHICLE_PIG_SCREEN_NAME("CosmeticMenus.vehicle-pig-screen-name", "Pig"),
	VEHICLE_IRON_GOLEM_SCREEN_NAME("CosmeticMenus.vehicle-irongolem-screen-name", "Iron Golem"),
	VEHICLE_SHEEP_SCREEN_NAME("CosmeticMenus.vehicle-sheep-screen-name", "Sheep"),
	VEHICLE_CREEPER_SCREEN_NAME("CosmeticMenus.vehicle-creeper-screen-name", "Creeper"),
	
	/**
	 * VEHICLE PARTICLES NAMES.
	 */
//	VEHIClE_PARTICLE_RAINBOW_NAME("", ""),
	
	/**
	 * PARACHUTE COLOR NAMES.
	 */
	PARACHUTE_COLOR_BLACK_NAME("CosmeticMenus.parachute-color-black-screen-name", "Black"),
	PARACHUTE_COLOR_RED_NAME("CosmeticMenus.parachute-color-red-screen-name", "Red"),
	PARACHUTE_COLOR_BLUE_NAME("CosmeticMenus.parachute-color-blue-screen-name", "Blue"),
	PARACHUTE_COLOR_GREEN_NAME("CosmeticMenus.parachute-color-green-screen-name", "Green"),
	PARACHUTE_COLOR_YELLOW_NAME("CosmeticMenus.parachute-color-yellow-screen-name", "Yellow"),
	PARACHUTE_COLOR_WHITE_NAME("CosmeticMenus.parachute-color-white-screen-name", "White"),
	
	// TEAM SELECTOR:
//	TEAM_SELECTOR_MENU_NAME(ChatColor.LIGHT_PURPLE + "Teams"),
//	TEAM_SELECTOR_COOP_ITEM_NAME(ChatColor.YELLOW + "Join Team."),
//	TEAM_SELECTOR_COOP_ITEM_LEAVE_TEAM_MESSAGE(ChatColor.RED + "You have to leave your team to join another!"),
//	TEAM_SELECTOR_COOP_ITEM_NO_TEAMS_TO_JOIN(ChatColor.RED + "No teams were found that you could join!"),
//	TEAM_SELECTOR_SELECT_TEAM_ITEM_NAME(ChatColor.LIGHT_PURPLE + "Enviar solicitud al equipo de " + ChatColor.GREEN.toString() + Lang.PLAYER_REPLACEMENT_KEY),
//	TEAM_JOIN_REQUEST_MENU_NAME(ChatColor.LIGHT_PURPLE + "El jugador " + ChatColor.GREEN + Lang.PLAYER_REPLACEMENT_KEY + " quiere unirse a tu equipo."),
//	TEAM_JOIN_REQUEST_ACCEPT_ITEM_NAME(ChatColor.GREEN + "Aceptar solicitud."),
//	TEAM_JOIN_REQUEST_REJECT_ITEM_NAME(ChatColor.RED   + "Rechazar solicitud."),
//	TEAM_JOIN_REQUEST_SENT_MESSAGE(ChatColor.GREEN + "Solicitud enviada!"),
	
	// TEAM CREATOR/EDITOR:
//	MI_TEAM_MENU_NAME(ChatColor.LIGHT_PURPLE + "Mi Equipo."),
//		TEAM_SELECTOR_EDIT_ITEM_NAME(ChatColor.LIGHT_PURPLE + "Editar Equipo."),
//		TEAM_SELECTOR_MAKE_ITEM_NAME(ChatColor.YELLOW + "Crear Equipo."),
//		TEAM_CREATOR_MENU_INV_MEMBER_ITEM_NAME( ChatColor.YELLOW + "Inivitar jugador a este equipo."),
//		TEAM_INV_MENU_NAME(ChatColor.LIGHT_PURPLE + "Inivitar a: "),
//		TEAM_INV_MENU_NO(ChatColor.RED + "No hay jugadores para invitar!"),
//		TEAM_INV_ITEM_NAME(ChatColor.YELLOW + "Invitar a " + ChatColor.GREEN + Lang.PLAYER_REPLACEMENT_KEY),
//		TEAM_INV_ITEM_MESSAGE(ChatColor.LIGHT_PURPLE + "Invitacion enviada!"),
//		TEAM_INV_REQ_MENU_NAME(ChatColor.LIGHT_PURPLE + "Invitacion de " + ChatColor.GREEN + Lang.PLAYER_REPLACEMENT_KEY + ChatColor.LIGHT_PURPLE + " a su equipo."),
//		TEAM_INV_REQ_ACCEPT_ITEM_NAME(ChatColor.GREEN + "Aceptar Invitacion."),
//		TEAM_INV_REQ_ACCEPT_MESSAGE(ChatColor.GREEN + "Invitacion Aceptada."),
//		TEAM_INV_REQ_REJECT_ITEM_NAME(ChatColor.RED + "Rechazar Invitacion."),
//		TEAM_INV_REQ_REJECT_MESSAGE(ChatColor.RED + "Invitacion Rechazada."),
//		TEAM_CREATOR_MENU_KICK_MEMBER_ITEM_NAME(ChatColor.YELLOW + "Expulsar jugador de este equipo."),
//		TEAM_KICKER_MENU_NAME(ChatColor.LIGHT_PURPLE + "Expulsar a:"),
//		TEAM_KICKER_MENU_NO(ChatColor.RED + "No hay jugadores en tu equipo para expulsar!"),
//		TEAM_KICKER_ITEM_NAME(ChatColor.YELLOW + "Expulsar a " + ChatColor.RED + Lang.PLAYER_REPLACEMENT_KEY),
//		TEAM_KICKER_ITEM_MESSAGE(ChatColor.LIGHT_PURPLE + "Jugador expulsado!"),
		
	AUTO_STOP_KICK_MESSAGE("AutoStop.kick-message", ChatColor.RED + ChatColor.LIGHT_PURPLE.toString() + "Game Over!"),
	AUTO_STOP_MESSAGE("AutoStop.message", ChatColor.RED + ChatColor.LIGHT_PURPLE.toString() + "Restarting server in " + Lang.NUMBER_REPLACEMENT_KEY),
	;
	
	
	public static final String NUMBER_REPLACEMENT_KEY          = "%#%";
	public static final String WORD_REPLACEMENT_KEY            = "%W%";
	public static final String SEPARATOR_REPLACEMENT_KEY       = "%S%";
	public static final String PLAYER_REPLACEMENT_KEY          = "%PLAYER%";
	public static final String KILLER_REPLACEMENT_KEY          = "%KILLER%";
	public static final String KNOCKER_REPLACEMENT_KEY         = "%KNOCKER%";
	public static final String RANGE_REPLACEMENT_KEY           = "%RANGE%";
	public static final String CHAT_MODE_REPLACEMENT_KEY       = "%MODE%";
	public static final String NEW_LINE_INDICATOR_VARIABLE     = "%N%";
	
	private static final String          LANG_CONFIG_HEADER = 
			" ---------------------------- Battle Royale Language ---------------------------- #"      + StringUtil.LINE_SEPARATOR
			+                               "This is the Battle Royale Language config file"           + StringUtil.LINE_SEPARATOR
			+ NUMBER_REPLACEMENT_KEY      + " will be replaced with a number when needed."             + StringUtil.LINE_SEPARATOR
			+ WORD_REPLACEMENT_KEY        + " will be replaced with a word when needed."               + StringUtil.LINE_SEPARATOR
			+ PLAYER_REPLACEMENT_KEY      + " will be replaced with the name of a player when needed." + StringUtil.LINE_SEPARATOR
			+ NEW_LINE_INDICATOR_VARIABLE + " is the line separator. "                                 + StringUtil.LINE_SEPARATOR
			+ " -------------------------------------------------------------------------------- #"    + StringUtil.LINE_SEPARATOR;
	
	public static void setConfigurationFile(File yml_file) {
		Validate.notNull(yml_file, "The file cannot be null!");
		Validate.isTrue(new YamlFileFilter().accept(yml_file), "The given file must be a valid .yml file!");
		Validate.isTrue(yml_file.isFile(), "The file must exist!");
		
		setConfiguration(YamlConfigurationComments.loadConfiguration(yml_file));
	}
	
	public static void setConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		
		Arrays.asList(Lang.values()).forEach(item -> item.load(section));
	}
	
	public static int saveDefaultConfiguration(YamlConfiguration section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		List < Lang > save = Arrays.asList ( Lang.values ( ) ).stream ( )
				.filter ( item -> !section.isSet ( 
						YamlUtil.alternatePathSeparator ( item.key , section.getRoot ( ).options ( ).pathSeparator ( ) ) ) )
				.collect ( Collectors.toList ( ) );
		
		if ( !save.isEmpty ( ) ) {
			section.options ( ).header ( LANG_CONFIG_HEADER );
			section.options ( ).copyHeader ( true );
			save.forEach ( item -> section.set ( YamlUtil.alternatePathSeparator ( item.key , section.options ( ).pathSeparator ( ) ) , 
					StringUtil.untranslateAlternateColorCodes ( item.default_value ) ) );
		}
		return save.size ( );
		
//		List<Lang> save = Arrays.asList(Lang.values()).stream()
//				.filter(item -> !section.isSet(YmlUtils.getFixPathSeparator(section.getRoot(), item.key)))
//				.collect(Collectors.toList());
//		
//		if (!save.isEmpty()) {
//			section.options().header(LANG_CONFIG_HEADER);
//			section.options().copyHeader(true);
//			save.forEach(item -> section.set(YmlUtils.getFixPathSeparator(section.getRoot(), item.key),
//					StringUtil.untranslateColors(item.default_value)));
//		}
//		return save.size();
		
		
	}
	
	private final String           key;
	private final String default_value;
	private       String         value;
	
	Lang(String key, String default_value) {
		this.key           = key;
		this.default_value = default_value;
		this.value         = default_value;
	}
	
	public String getDefaultValue(boolean colored) {
		return colored ? StringUtil.translateAlternateColorCodes(default_value) : StringUtil.stripColors(default_value);
	}
	
	public String getDefaultValue() {
		return getDefaultValue(false);
	}
	
	public String getValue(boolean colored) {
		return colored ? StringUtil.translateAlternateColorCodes(value) : StringUtil.stripColors(value);
	}
	
	public String getValue() {
		return getValue(false);
	}
	
	public String getShortenValue(int length, boolean colored) {
		return StringUtil.limit(getValue(colored), length);
	}
	
	public String getShortenValue(int length) {
		return getShortenValue(length, false);
	}
	
	public String getValueReplacing(String target, String replacement, boolean colored) {
		return this.getValue(colored).replace(target, replacement);
	}
	
	public String getValueReplacing(String target, String replacement) {
		return getValueReplacing(target, replacement, false);
	}
	
	public String getValueReplacingAll(String regex, String replacement, boolean colored) {
		return this.getValue(colored).replaceAll(regex, replacement);
	}
	
	public String getValueReplacingAll(String regex, String replacement) {
		return getValueReplacingAll(regex, replacement, false);
	}
	
	public String getValueReplacingNumber(Number replacement_number, boolean colored) {
		return getValueReplacingVariables(String.valueOf(replacement_number), colored, Lang.NUMBER_REPLACEMENT_KEY);
	}
	
	public String getValueReplacingNumber(String replacement_number, boolean colored) {
		return getValueReplacingVariables(replacement_number, colored, Lang.NUMBER_REPLACEMENT_KEY);
	}
	
	public String getValueReplacingNumber(Number replacement_number) {
		return getValueReplacingNumber(replacement_number, false);
	}
	
	public String getValueReplacingNumber(String replacement_number) {
		return getValueReplacingNumber(replacement_number, false);
	}
	
	public String getValueReplacingWord(String replacement_word, boolean colored) {
		return getValueReplacingVariables(replacement_word, colored, Lang.WORD_REPLACEMENT_KEY);
	}
	
	public String getValueReplacingWord(String replacement_word) {
		return getValueReplacingWord(replacement_word, false);
	}
	
	public String getValueReplacingPlayer(String replacement_player_name, boolean colored) {
		return getValueReplacingVariables(replacement_player_name, colored, Lang.PLAYER_REPLACEMENT_KEY);
	}
	
	public String getValueReplacingPlayer(String replacement_player_name) {
		return getValueReplacingPlayer(replacement_player_name, false);
	}
	
	public String getValueReplacingVariables ( String replacement , boolean colored , String... variables ) {
		String result = getValue ( colored );
		for ( String variable : variables ) {
			result = StringUtil.toUpperCase(result, variable).replace(variable, replacement);
			
		}
		return result;
	}
	
	public String getValueReplacingVariables(String replacement, String variable) {
		return getValueReplacingVariables(replacement, false, variable);
	}
	
	public String getValueReplacingVariables(String[] replacements, boolean colored, String[] variables) {
		Validate.isTrue(replacements.length == variables.length, "The replacements length and the variables length doesn't match!");
		String result = getValue(colored);
		for (int i = 0; i < variables.length; i++) {
			result = StringUtil.toUpperCase(result, variables[i]).replace(variables[i], replacements[i]);
		}
		return result;
	}
	
	public String[] getSplitLines(boolean colored) {
		return getSplit(Lang.NEW_LINE_INDICATOR_VARIABLE, colored);
	}
	
	public String[] getSplitLines() {
		return getSplitLines(false);
	}
	
	public String[] getSplit(String regex, boolean colored) {
		return getValue(colored).split(regex);
	}
	
	public String[] getSplit(String regex) {
		return getSplit(regex, false);
	}
	
	@Override
	public String toString() {
		return name();
	}

	/**
	 * Load the configuration of this {@link com.hotmail.AdrianSRJose.annihilation.config.lang.Lang}
	 * from the given {@link ConfigurationSection}.
	 * <p>
	 * @param section the section to load from.
	 */
	public void load(ConfigurationSection section) {
		if (section.isString(key)) {
			this.value = section.getString(key);
		} else {
			ConsoleUtil.sendPluginMessage(ChatColor.RED,
					"(Language config) It was not possible to correctly determine the configuration of '" + this.key + "'!",
					BattleRoyale.getInstance());
		}
	}
	
	/**
	 * Saves the default value of this {@link com.hotmail.AdrianSRJose.annihilation.config.lang.Lang}.
	 * <p>
	 * @param section the {@link ConfigurationSection} in which
	 * this will be saved.
	 */
	public void save(ConfigurationSection section) {
		section.set(key, default_value);
	}
}