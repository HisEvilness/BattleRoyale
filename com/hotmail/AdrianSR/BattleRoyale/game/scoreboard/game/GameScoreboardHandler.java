package com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game.config.GameScoreboardConfiguration;
import com.hotmail.AdrianSR.BattleRoyale.game.time.border.BorderTimer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.util.DegreesDirection;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.scoreboard.ScoreboardUtils;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.scoreboard.SimpleScoreboard;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * Represents the handler for the handling of any game scoreboard.
 * <p>
 * This handler can load/save the configuration of the scoreobard, and
 * allow to update the scoreboard by using the method {@link #updateTo(Member)}.
 * <p>
 * @author AdrianSR / Thursday 07 November, 2019 / 12:38 AM
 */
public final class GameScoreboardHandler extends PluginHandler {
	
	/* custom variables for formating the game scoreboard */
	public static final String KEY_CURRENT_DATE       = "%CURRENT_DATE%";
	public static final String KEY_BORDER_STATUS      = "%BORDER_STATUS%";
	public static final String KEY_PLAYERS_TEAMS_LEFT = "%PLAYERS_TEAMS_LEFT%";
	public static final String KEY_KILLS              = "%KILLS%";
	public static final String KEY_FIRST_ARROWS_LINE  = "%FIRST_ARROWS_LINE%";
	public static final String KEY_SECOND_ARROWS_LINE = "%SECOND_ARROWS_LINE%";
	public static final String KEY_THIRD_ARROWS_LINE  = "%THIRD_ARROWS_LINE%";
	public static final String KEY_GAME_KILLS         = "%GAME_KILLS%";
	public static final String KEY_SAFE_UNSAFE        = "%SAFE_UNSAFE%";
	public static final String KEY_TEAM_MEMBERS       = "%TEAM_MEMBERS%";
	
	public static GameScoreboardHandler getInstance ( ) {
		return (GameScoreboardHandler) HANDLER_INSTANCES.get ( GameScoreboardHandler.class );
	}
	
	private final GameScoreboardConfiguration configuration;
	private final Map<UUID, SimpleScoreboard>   scoreboards;
	private       BukkitTask                        updater;
	
	public GameScoreboardHandler(BattleRoyale plugin) {
		super(plugin); this.register ( );
		this.configuration = new GameScoreboardConfiguration /* making the configuration class */
				(
						ChatColor.YELLOW + ChatColor.BOLD.toString() + "BATTLE ROYALE",
						ChatColor.GRAY.toString()                    + KEY_CURRENT_DATE,
						SimpleScoreboard.WHITESPACE_INDICATOR,
						KEY_BORDER_STATUS,
						SimpleScoreboard.WHITESPACE_INDICATOR,
						KEY_PLAYERS_TEAMS_LEFT,
						KEY_KILLS,
						SimpleScoreboard.WHITESPACE_INDICATOR,
						KEY_FIRST_ARROWS_LINE,
						KEY_SECOND_ARROWS_LINE,
						KEY_THIRD_ARROWS_LINE + " " + ChatColor.RESET + ChatColor.GREEN + KEY_SAFE_UNSAFE,
//						ChatColor.GREEN + ChatColor.BOLD.toString() + ( Global.A1 + " " + Global.A2 + " " + Global.A3 ),
//						ChatColor.GREEN + ChatColor.BOLD.toString() + ( Global.A7 + "   " + Global.A8 ),
//						ChatColor.GREEN + ChatColor.BOLD.toString() + ( Global.A4 + " " + Global.A5 + " " + Global.A6 ) + ChatColor.RESET + ChatColor.GREEN + " " + KEY_SAFE_UNSAFE,
						SimpleScoreboard.WHITESPACE_INDICATOR,
						KEY_TEAM_MEMBERS,
						SimpleScoreboard.WHITESPACE_INDICATOR,
						ChatColor.YELLOW + "www.SpigotMC.org"
				);
		this.scoreboards = new HashMap<>();
		this.updater     = null;
	}
	
	public GameScoreboardConfiguration getConfiguration()  {
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
					.replace(KEY_CURRENT_DATE,       ScoreboardUtils.getCurrentDate())
					.replace(KEY_BORDER_STATUS,      getBorderStatusLine())
					.replace(KEY_PLAYERS_TEAMS_LEFT, getPlayersTeamsLeftLine())
					.replace(KEY_KILLS,              getKillsLine(member))
					.replace(KEY_GAME_KILLS,         getGameKillsLine())
					.replace(KEY_FIRST_ARROWS_LINE,  getArrowsLine(member, 0))
					.replace(KEY_SECOND_ARROWS_LINE, getArrowsLine(member, 1))
					.replace(KEY_THIRD_ARROWS_LINE,  getArrowsLine(member, 2))
					.replace(KEY_SAFE_UNSAFE,        getSafeUnsafeReplacement(member));
		}
		
		/* looking for the members variable */
		for (int i = 0; i < final_elements.length; i++) {
			String element = final_elements[i];
			if (element == null || !element.toUpperCase().trim().contains(KEY_TEAM_MEMBERS)) {
				continue;
			}
			
			/* insert viewer first */
			final_elements[i] = ( getHealthLine(member) + " " + Lang.SCOREBOARD_YOU_SUFIX.getValue(true) );
			
			/* append team mates */
			final List<Member> members = ( member.hasTeam() ? member.getTeam().getOnlineMembers() : null );
			if (members != null && !members.isEmpty()) {
				members.remove(member); /* exclude viewer */
			} else {
				break;
			}
			
			if ( ( i + 1 ) >= final_elements.length ) { // there is no space for append the team mates
				break;
			}

			final Iterator<Member> mate_iterator = members.iterator();
			String[]                 bottom_copy = Arrays.copyOfRange(final_elements, ( i + 1 ), final_elements.length);
			int               bottom_final_index = ( i + 1 ); // where start adding the bottom copy
			for (int j = ( i + 1 ); j < final_elements.length; j++) {
				Member mate = null;
				while ( mate_iterator.hasNext() ) {
					final Member next_in_iterator = mate_iterator.next();
					if (next_in_iterator != null && next_in_iterator.isOnline()) {
						mate = next_in_iterator;
						break;
					}
				}
				
				if (mate == null) {
					break;
				}
				
				/* insert team mate health line */
				final_elements[j] = getHealthLine(mate);
				bottom_final_index ++;
			}
			
			/* insert bottom copy */
			for (int j = 0; j < bottom_copy.length; j++) {
				int index = ( bottom_final_index + j );
				if (index >= final_elements.length) {
					break;
				}
				final_elements[index] = bottom_copy[j];
			}
			break;
		}
		
		scoreboard.addAll(0, final_elements);
		scoreboard.update();
		
		scoreboards.put(member.getUUID(), scoreboard);
		
		return scoreboard;
	}
	
	private String getBorderStatusLine() {
		BorderTimer border_timer = BorderTimer.getInstance();
		if (border_timer == null) {
			return null;
		}
		
		String message_to_show = "";
		if (border_timer.isBorderStopped()) {
			message_to_show = Lang.SCOREBOARD_BORDER_STOPED.getValue(true);
		} else if (border_timer.isShrinking()) {
			message_to_show = Lang.SCOREBOARD_BORDER_SHRINKING.getValueReplacingNumber(border_timer.getFormatShrinkingProgress(), true);
		} else {
			message_to_show = Lang.SCOREBOARD_BORDER_SHRINK.getValueReplacingNumber(border_timer.getFormatTimeToShrinking(), true);
		}
		return message_to_show; // show current shrink state
	}
	
	private String getArrowsLine(Member member, int id) {
		BorderTimer border_timer = BorderTimer.getInstance();
		if (border_timer == null 
				|| border_timer.getCurrentShrink() == null
				|| border_timer.getCurrentShrink().getLocation() == null 
				|| member.getPlayer() == null) {
			return "";
		}
		
		Player   player = member.getPlayer();
		double angles[] = { 45, 0, -45, 90, -90, 135, 180, -135 };
		for (int i = 0; i < angles.length; i++) {
			angles[i] = Math.toRadians(angles[i]); // angles to radians
		}
		
		int     facing_angle = -1;
//		Loc   current_center = new Loc(border_timer.getBorderCenter()); // border_timer.getCurrentShrink().getLocation();
		ConfigurableLocation   current_center = border_timer.getCurrentShrink().getLocation();
		Vector vector_facing = player.getLocation().getDirection();
		
		/* if safe, all the arrows must be green */
		if (BattleModeUtils.isSafe(member)) {
			return getRawArrowsLine(id, null);
		}
		
		
		Area area = new Area ( 
				new Vector3i ( current_center.getBlockX ( ) , current_center.getBlockY ( ) , current_center.getBlockZ ( ) ) , 
				new Vector3i ( current_center.getBlockX ( ) , current_center.getBlockY ( ) , current_center.getBlockZ ( ) ) );
		area.setRadius((int) ( border_timer.getBorderRadius() / 2 ) );
		for (int i = 0; i < angles.length; i++) {
			double         angle = angles[i];
			double angle_degrees = Math.toDegrees(angle);
			Vector3d      facing = new Vector3d(vector_facing.getX(), vector_facing.getY(), vector_facing.getZ()).mul(1, 0, 1);
			if (Math.abs(facing.length()) < GenericMath.DBL_EPSILON) {
				facing_angle = (int) angle_degrees;
				break;
			}
			
			facing                     = facing.normalize();
			Vector3d rotated_direction = new Vector3d(
					( facing.getX() * Math.cos(angle) ) + ( facing.getZ() * Math.sin(angle) ), 
					0,
					( facing.getZ() * Math.cos(angle) ) - facing.getX() * Math.sin(angle) );
			
			Location player_location = player.getLocation ( );
			Vector3d   player_vector = new Vector3d ( player_location.getX ( ) , player_location.getY ( ) , player_location.getZ ( ) );
			
			if (area.intersectsVector(rotated_direction, player_vector )) {
				facing_angle = (int) angle_degrees;
				break;
			}
		}
		
		DegreesDirection direction = null;
		switch (facing_angle) {
		case -135:
			direction = DegreesDirection.NW;
			break;
		case 0:
		default:
			direction = DegreesDirection.S;
			break;
		case -45:
			direction = DegreesDirection.SW;
			break;
			
		case 90:
			direction = DegreesDirection.E;
			break;
		case -90:
			direction = DegreesDirection.W;
			break;

		case 135:
			direction = DegreesDirection.NE;
			break;
		case 180:
			direction = DegreesDirection.N;
			break;
		case 45:
			direction = DegreesDirection.SE;
			break;
		}
		return getRawArrowsLine(id, direction);
	}
	
	private String getRawArrowsLine(int id, DegreesDirection border_center_direction) {
		String                      format = ( id == 1 ? "{0}   {1}" : "{0} {1} {2}" );
		ChatColor               base_color = ChatColor.GRAY;
		ChatColor               bcdd_color = ChatColor.GREEN;
		DegreesDirection[] line_directions = null;
		String[]               line_arrows = null;
		switch(id) {
		case 0: // first line directions
			line_directions = new DegreesDirection[] { DegreesDirection.SE, DegreesDirection.S, DegreesDirection.SW };
			line_arrows     = new String[] { Global.A1, Global.A2, Global.A3 };
			break;
		case 1: // second line directions
			line_directions = new DegreesDirection[] { DegreesDirection.E, DegreesDirection.W };
			line_arrows     = new String[] { Global.A7, Global.A8 };
			break;
		case 2: // third line directions
			line_directions = new DegreesDirection[] { DegreesDirection.NE, DegreesDirection.N, DegreesDirection.NW };
			line_arrows     = new String[] { Global.A4, Global.A5, Global.A6 };
			break;
		}
		
		// "{0} {1} {2}" -> SE, S, SW
		// "{0}     {1}" -> E       W
		// "{0} {1} {2}" -> NE, N, NW
		for (int i = 0; i < line_directions.length; i++) {
			DegreesDirection direction = line_directions[i];
			ChatColor            color = ( ( border_center_direction == null || direction == border_center_direction ) ? bcdd_color : base_color );
			format                     = format.replace( ( "{" + i + "}" ), ( ChatColor.BOLD.toString() + color + line_arrows[i] ) );
		}
		return format;
	}
	
	private String getPlayersTeamsLeftLine() {
		BattleMode mode = GameManager.getBattleMode();
		if (!mode.isRespawnEnabled()) {
			if (!mode.isSolo()) { // for teams
				int total_teams = BRTeam.getTeams().size();
				int  teams_left = (int) BRTeam.getTeams().stream().filter(Team::isLiving).count();
				
				return Lang.SCOREBOARD_TEAMS_LEFT.getValueReplacingWord( ( teams_left + "/" + total_teams ), true );
			}  else { // for players
				int total_players = BRPlayer.getBRPlayers().size();
				int  players_left = (int) BRPlayer.getBRPlayers().stream().filter(Member::isLiving).count();

				return Lang.SCOREBOARD_PLAYERS_LEFT.getValueReplacingWord( ( players_left + "/" + total_players ), true );
			}
		}
		return ChatColor.RED + "Unsupported variable!"; // warning message
	}
	
	private String getKillsLine(Member member) {
		return ( GameManager.getBattleMode().isSolo() ? Lang.SCOREBOARD_SOLO_KILLS : Lang.SCOREBOARD_TEAM_KILLS )
				.getValueReplacingNumber( ( member.hasTeam() ? member.getTeam().getKills() : 0 ) );
	}
	
	private String getGameKillsLine() {
		if (!BattleModeUtils.isDeterminatedByKills(GameManager.getBattleMode())) {
			return ChatColor.RED + "Unsupported variable!"; // warning message
		}
		return Lang.SCOREBOARD_GAME_KILLS.getValueReplacingNumber(GameManager.getGameKills(), true);
	}
	
	private String getSafeUnsafeReplacement(Member member) {
		return ( BattleModeUtils.isSafe(member) ? Lang.SCOREBOARD_SAFE_TEXT : Lang.SCOREBOARD_UNSAFE_TEXT ).getValue(true);
	}
	
	/**
	 * Gets Scoreboard health line replace for a
	 * {@link Member} depending of his health.
	 * 
	 * health <  1,  Death line.
	 * health <= 8,  Bad line.
	 * health <= 15, Normal line.
	 * health <= 20, Good line.
	 * 
	 * @param mem the Member to get.
	 * @return the health line replacer.
	 */
	private String getHealthLine(final Member mem) {
		if (mem.isOnline()) {
			final double health = mem.getPlayer().getHealth();
			String         line = "";
			if (health < 1) {
				line = Lang.SCOREBOARD_TEAM_MEMBER_KILLED.getValueReplacingPlayer(mem.getName(), true); // already killed
			} else if (health < 8) {
				line = Lang.SCOREBOARD_TEAM_MEMBER_BAD_HEALTH.getValueReplacingPlayer(mem.getName(), true); // bad health
			} else if (health < 15) {
				line = Lang.SCOREBOARD_TEAM_MEMBER_REGULAR_HEALTH.getValueReplacingPlayer(mem.getName(), true); // regular health
			} else if (health <= 20) {
				line = Lang.SCOREBOARD_TEAM_MEMBER_HEALTHFULLY.getValueReplacingPlayer(mem.getName(), true); // healthfully
			}
			return line.replace(Lang.NUMBER_REPLACEMENT_KEY, String.valueOf((int) health));
		}
		return Lang.SCOREBOARD_TEAM_MEMBER_KILLED.getValueReplacingPlayer(mem.getName(), true);
	}
	
	public BukkitTask getUpdaterTask() {
		return updater;
	}
	
	public void startUpdaterTask() {
		if (updater != null && Bukkit.getScheduler().isCurrentlyRunning(updater.getTaskId())) {
			throw new UnsupportedOperationException("The updater task is already started!");
		}
		
		this.updater = SchedulerUtil.runTaskTimer ( ( ) -> {
			BRTeam.getTeams().stream()
			.filter(team -> team != null)
			.forEach(team -> team.getOnlineMembers().forEach(member -> updateTo(member)));
		}, 0, Config.SCOREBOARDS_REFRESH_DELAY.getAsInteger(), plugin);
	}
	
	public void stopUpdaterTask() {
		if (updater == null || !Bukkit.getScheduler().isCurrentlyRunning(updater.getTaskId())) {
			throw new UnsupportedOperationException("The updater task has never been started!");
		}
		
		this.updater.cancel();
		this.updater = null;
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}