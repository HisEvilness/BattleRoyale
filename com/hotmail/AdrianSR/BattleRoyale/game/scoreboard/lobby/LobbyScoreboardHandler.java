package com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby.config.LobbyScoreboardConfiguration;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.AutoStarter;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.scoreboard.ScoreboardUtils;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.scoreboard.SimpleScoreboard;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public final class LobbyScoreboardHandler extends PluginHandler {
	
	/* custom variables for formating the lobby scoreboard */
	public static final String   KEY_CURRENT_DATE = "%CURRENT_DATE%";
	public static final String ONLINE_PLAYERS_KEY = "%ONLINE_PLAYERS%";
	public static final String    MIN_PLAYERS_KEY = "%MIN_PLAYERS%";
	public static final String    GAME_STATUS_KEY = "%GAME_STATUS%";

	public static LobbyScoreboardHandler getInstance() {
		return (LobbyScoreboardHandler) HANDLER_INSTANCES.get(LobbyScoreboardHandler.class);
	}
	
	private final LobbyScoreboardConfiguration configuration;
	private final Map<UUID, SimpleScoreboard>    scoreboards;
	private       BukkitTask                         updater;

	public LobbyScoreboardHandler(BattleRoyale plugin) {
		super(plugin); this.register ( );
		this.configuration = new LobbyScoreboardConfiguration(
				ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "BATTLE ROYALE", 
				SimpleScoreboard.WHITESPACE_INDICATOR,
				ChatColor.WHITE + "Players: " + ChatColor.GREEN + ONLINE_PLAYERS_KEY + " / " + MIN_PLAYERS_KEY,
				SimpleScoreboard.WHITESPACE_INDICATOR,
				GAME_STATUS_KEY,
				SimpleScoreboard.WHITESPACE_INDICATOR,
				ChatColor.WHITE + "Server: " + ChatColor.GREEN  + "My server",
				SimpleScoreboard.WHITESPACE_INDICATOR,
				ChatColor.YELLOW + "www.SpigotMC.org");
		this.scoreboards = new HashMap<>();
		this.updater     = null;
	}

	public LobbyScoreboardConfiguration getConfiguration()  {
		return configuration;
	}
	
	public SimpleScoreboard getOf(Member owner) {
		scoreboards.putIfAbsent(owner.getUUID(), new SimpleScoreboard(configuration.getName(), configuration.getElements()));
		
		SimpleScoreboard scoreboard = scoreboards.get(owner.getUUID());
		scoreboard.addViewer(owner.getPlayer());
		return scoreboard;
	}
	
	public SimpleScoreboard updateTo(Member member) {
		SimpleScoreboard scoreboard = getOf(member);
		String[]     final_elements = Arrays.copyOfRange(configuration.getElements(), 0, configuration.getElements().length);
		for (int i = 0; i < final_elements.length; i++) {
			String element = final_elements[i];
			if (element == null) {
				continue;
			}
			
			final_elements[i] = element // replacing variables
					.replace(KEY_CURRENT_DATE,   ScoreboardUtils.getCurrentDate())
					.replace(ONLINE_PLAYERS_KEY, String.valueOf(Bukkit.getOnlinePlayers().size()))
					.replace(MIN_PLAYERS_KEY,    String.valueOf(Config.MAX_PLAYERS.getAsInteger()))
					.replace(GAME_STATUS_KEY,    getGameStatusLine());
		}
		
		scoreboard.addAll(0, final_elements);
		scoreboard.update();
		
		scoreboards.put(member.getUUID(), scoreboard);
		
		return scoreboard;
	}
	
	// -135 = DegreesDirection.SE
	// 0 = DegreesDirection.S
	// -45 = DegreesDirection.SW

	// 90 = DegreesDirection.E
	// -90 = DegreesDirection.W

	// 135 = DegreesDirection.NE
	// 180 = DegreesDirection.N
	// 45 = DegreesDirection.NW
	
	// {FIRST_ARROWS_LINE}
	// {SECOND_ARROWS_LINE}
	// {THIRD_ARROWS_LINE}
//	private String getArrowsLine(Member member, int id) {
//		BorderTimer border_timer = BorderTimer.getTimer();
//		if (border_timer == null) {
//			return "";
//		}
//		
//		// TODO: make done
//		
//		Player   player = member.getPlayer();
//		double angles[] = { 45, 0, -45, 90, -90, 135, 180, -135 };
//		for (int i = 0; i < angles.length; i++) {
//			angles[i] = Math.toRadians(angles[i]); // angles to to radians
//		}
//		
//		int     facing_angle = -1;
//		Loc        spawn_loc = MapsManager.LOBBY_MAP.getSpawn();
//		Vector vector_facing = player.getLocation().getDirection();
//		
//		// TODO: CHECK PLAYER IS SAFE
//		
//		Area test_area = new Area(spawn_loc.toVector3i(), spawn_loc.toVector3i());
//		test_area.setRadius(5);
//		for (int i = 0; i < angles.length; i++) {
//			double         angle = angles[i];
//			double angle_degrees = Math.toDegrees(angle);
//			Vector3d      facing = new Vector3d(vector_facing.getX(), vector_facing.getY(), vector_facing.getZ()).mul(1, 0, 1);
//			if (Math.abs(facing.length()) < GenericMath.DBL_EPSILON) {
//				facing_angle = (int) angle_degrees;
//				break;
//			}
//			
//			facing                     = facing.normalize();
//			Vector3d rotated_direction = new Vector3d(
//					( facing.getX() * Math.cos(angle) ) + ( facing.getZ() * Math.sin(angle) ), 
//					0,
//					( facing.getZ() * Math.cos(angle) ) - facing.getX() * Math.sin(angle) );
//			
//			if (test_area.intersectsVector(rotated_direction, new Loc(player.getLocation()).toVector3d())) {
//				facing_angle = (int) angle_degrees;
//				break;
//			}
//		}
//		
//		DegreesDirection direction = null;
//		switch (facing_angle) {
//		case -135:
//			direction = DegreesDirection.NW;
//			break;
//		case 0:
//		default:
//			direction = DegreesDirection.S;
//			break;
//		case -45:
//			direction = DegreesDirection.SW;
//			break;
//			
//		case 90:
//			direction = DegreesDirection.E;
//			break;
//		case -90:
//			direction = DegreesDirection.W;
//			break;
//
//		case 135:
//			direction = DegreesDirection.NE;
//			break;
//		case 180:
//			direction = DegreesDirection.N;
//			break;
//		case 45:
//			direction = DegreesDirection.SE;
//			break;
//		}
//		return getRawArrowsLine(id, direction);
//	}
//	
//	private String getRawArrowsLine(int id, DegreesDirection border_center_direction) {
//		String                      format = ( id == 1 ? "{0}     {1}" : "{0} {1} {2}" );
//		ChatColor               base_color = ChatColor.GRAY;
//		ChatColor               bcdd_color = ChatColor.GREEN;
//		DegreesDirection[] line_directions = null;
//		String[]               line_arrows = null;
//		switch(id) {
//		case 0: // first line directions
//			line_directions = new DegreesDirection[] { DegreesDirection.SE, DegreesDirection.S, DegreesDirection.SW };
//			line_arrows     = new String[] { Global.A1, Global.A2, Global.A3 };
//			break;
//		case 1: // second line directions
//			line_directions = new DegreesDirection[] { DegreesDirection.E, DegreesDirection.W };
//			line_arrows     = new String[] { Global.A7, Global.A8 };
//			break;
//		case 2: // third line directions
//			line_directions = new DegreesDirection[] { DegreesDirection.NE, DegreesDirection.N, DegreesDirection.NW };
//			line_arrows     = new String[] { Global.A4, Global.A5, Global.A6 };
//			break;
//		}
//		
//		// "{0} {1} {2}" -> SE, S, SW
//		// "{0}     {1}" -> E       W
//		// "{0} {1} {2}" -> NE, N, NW
//		for (int i = 0; i < line_directions.length; i++) {
//			DegreesDirection direction = line_directions[i];
//			ChatColor            color = ( direction == border_center_direction ? bcdd_color : base_color );
//			format                     = format.replace( ( "{" + i + "}" ), ( ChatColor.BOLD.toString() + color + line_arrows[i] ) );
//		}
//		return format;
//	}
	
	private String getGameStatusLine() {
		boolean starting = ( AutoStarter.getCountdown() != null );
		int        count = ( starting ? AutoStarter.getCountdown() : Config.AUTO_START_MIN_PLAYERS.getAsInteger() );
		return ( starting ? Lang.SCOREBOARD_LOBBY_STARTING : Lang.SCOREBOARD_LOBBY_PLAYERS_TO_START )
				.getValueReplacingNumber(count, true);
	}
	
	public BukkitTask getUpdaterTask() {
		return updater;
	}
	
	public void startUpdaterTask() {
		if (updater == null) {
			this.updater = SchedulerUtil.runTaskTimer(() -> {
				Bukkit.getOnlinePlayers().forEach(player -> updateTo(BRPlayer.getBRPlayer(player)));
			}, 0, Config.SCOREBOARDS_REFRESH_DELAY.getAsInteger(), plugin);
		}
	}

	public void stopUpdaterTask() {
		if (updater != null) {
			this.updater.cancel();
			this.updater = null;
		}
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}