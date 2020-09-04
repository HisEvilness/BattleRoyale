package com.hotmail.AdrianSR.BattleRoyale.game.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.meta.FireworkMeta;

import com.hotmail.AdrianSR.BattleRoyale.config.command.CustomConfigCommand;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.events.GameEndEvent;
import com.hotmail.AdrianSR.BattleRoyale.events.GameStartEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.complex.ComplexBattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.manager.BattleModeManager;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game.GameScoreboardHandler;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby.LobbyScoreboardHandler;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.CardinalBarsUpdater;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.gameend.GameEnd;
import com.hotmail.AdrianSR.BattleRoyale.game.time.border.BorderTimer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * Represents a Battle Royale Game Manager.
 * <p>
 * @author AdrianSR
 */
public final class GameManager extends PluginHandler {
	
	/**
	 * Global Class Values.
	 */
	private static boolean RUNNING;
//	private static BukkitTask WATCH_TASK;
//	private static long TIME_IN_GAME;
	private static long START_MILLIS = -1;
	
	/**
	 * Construct a new Game Manager.
	 *<p> 
	 * @param instance the BattleRoyale Plugin.
	 */
	public GameManager(final BattleRoyale plugin) {
		super(plugin); this.register ( );
		
		// remove cache worlds.
		MapsManager.removeCacheWorlds(plugin);
	}
	
	/**
	 * Start Game.
	 */
	public static void startGame() {
		if (RUNNING) {
			return;
		}
		
		/* check loaded battle mode */
		BattleMode                mode = BattleModeManager.getBattleMode();
		ComplexBattleMode complex_mode = ( mode instanceof ComplexBattleMode ? (ComplexBattleMode) mode : null );
		if (complex_mode != null && !complex_mode.canStart(false)) { /* check mode can start before map loading */
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The game could not start, the loaded mode is not ready to start!", BattleRoyale.getInstance());
			return;
		}
		
		// get battle map.
		BattleMap        map = null;
//		boolean load_minimap = false;
		
		// check current battle map.
		final BattleMap currMap = MapsManager.BATTLE_MAP;
		if (currMap == null) { // when is not loaded.
//			System.out.println ( "GameManager.startGame ( ) ----- 0" );
			// get config battle map folder.
			final File mapFolder = new File(MapsManager.checkBattleMapsFolder(), Config.MAP_TO_LOAD.toString());
//			System.out.println ( "GameManager.startGame ( ) ----- 1: " + mapFolder.getAbsolutePath ( ) );
			// load map.
			final BattleMap other = new BattleMap(mapFolder);
//			System.out.println ( "GameManager.startGame ( ) ----- 3" );
			/* load map world and start if is correctly loaded */
			if (other.getWorldManager().loadWorld(mapFolder, false)) {
				// set map
				map          = MapsManager.BATTLE_MAP;
//				load_minimap = map.getConfig().miniMapImageExists();
			}
		} else { // check Battle Map is not build loaded.
			if (currMap.isBuildLoaded()) {
				ConsoleUtil.sendPluginMessage(ChatColor.RED, "The game cannot be started when is loaded from the editor", BattleRoyale.getInstance());
				return;
			} else {
				map          = currMap;
//				load_minimap = (map.getMapMatrix() == null && map.getConfig().miniMapImageExists());
			}
		}
		
		// check Battle map.
		if (map == null) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "!The battle map could not be found!", BattleRoyale.getInstance());
			return;
		}
		
		// check battle map world.
		if (map.getWorld() == null) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "!The world of the battle map could not be found!", BattleRoyale.getInstance());
			return;
		}
		
		if (complex_mode != null && !complex_mode.canStart(true)) { /* check mode can start after map loading */
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The game could not start, the loaded mode is not ready to start!", BattleRoyale.getInstance());
			return;
		}
		
		// set running.
		RUNNING = true;
		
		/* game time */
		START_MILLIS = System.currentTimeMillis();
//		WATCH_TASK    = SchedulerUtil.runTaskTimer ( ( ) -> {
//			TIME_IN_GAME = TimeUnit.MILLISECONDS.toSeconds ( System.currentTimeMillis ( ) - START_INSTANT ); /* to seconds */
//		} , 0 , 0 , BattleRoyale.getInstance ( ) );

		// call game start event and check if is cancelled.
		final GameStartEvent event = new GameStartEvent ( );
		event.call ( );
		
		// send started game console message.
		ConsoleUtil.sendPluginMessage("Game Started!", BattleRoyale.getInstance());
		
		/* perform game start commands */
		for (CustomConfigCommand cmd : getGameStartCommands()) {
			if (cmd.isForPlayers()) {
				Bukkit.getOnlinePlayers().stream().forEach(player -> {
					player.performCommand(cmd.getArgumentForPlayer(player));	
				});
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.getArgument());
			}
		}

//		/* load minimap */
//		if (load_minimap) {
//			if (Config.MINIMAP_SAFE_LOAD.getAsBoolean()) {
//				MapsManager.BATTLE_MAP.getConfig().safeRestoreMiniMap();
//			} else {
//				MapsManager.BATTLE_MAP.getConfig().restoreMiniMap();
//			}
//		}
	}
	
	private static List<CustomConfigCommand> getGameStartCommands() {
		List<CustomConfigCommand> commands = new ArrayList<CustomConfigCommand>();
		for (int x = 0; x < 2; x++) {
			boolean     for_players = ( x == 0 );
			Config list_config_item = ( for_players ? Config.GAME_START_PLAYERS_CUSTOM_COMMANDS : Config.GAME_START_CONSOLE_CUSTOM_COMMANDS );
			
			if (list_config_item.getAsList() != null) {
				for (Object command_arg : list_config_item.getAsList()) {
					CustomConfigCommand command = new CustomConfigCommand(command_arg.toString(), for_players);
					if (command.isValid()) {
						commands.add(command);
					}
				}
			}
		}
		return commands;
	}
	
	/**
	 * Stop Game.
	 */
	public static void stopGame() {
		stopGame(() -> new GameEnd().Initialize().runTaskTimer(BattleRoyale.getInstance(), 20L, 20L));
	}
	
	/**
	 * Stop game.
	 * <p>
	 * @param runnable {@link StopRunnable} runnable to execute.
	 */
	public static void stopGame(StopRunnable runnable) {
		if (!RUNNING) { /* check is game running */
			return;
		}
		
		/* call game end event */
		final GameEndEvent event = new GameEndEvent ( );
		event.call ( );
		if (!event.isCancelled()) {
			RUNNING = false;
			
			/* execute runnable */
			runnable.stop();
			
			/* unknock members */
			Bukkit.getOnlinePlayers().forEach(player -> {
				BRPlayer bp = BRPlayer.getBRPlayer(player);
				if (bp.hasTeam()) {
					Vehicle vehicle = Vehicle.getVehicle(player);
					if (vehicle instanceof BRVehicle) {
						vehicle.close();
					}
					
					if (bp.isKnocked()) {
						bp.setKnocked ( false );
//						GameUtils.ejectKnockedSeat(player);
					}
				}
			});
		}
	}
	
	/**
	 * @return true if the game is running.
	 */
	public static boolean isRunning() {
		return RUNNING;
	}
	
	/**
	 * @return false if the game is running.
	 */
	public static boolean isNotRunning() {
		return !isRunning();
	}
	
	/**
	 * Gets time in game.
	 * <p>
	 * @param time_unit the time unit that will return the time in game.
	 * @return the time in game.
	 */
	public static long getTimeInGame ( TimeUnit unit ) {
		if ( START_MILLIS > -1 ) {
			long time_since = System.currentTimeMillis ( ) - START_MILLIS;
			switch ( unit ) {
				case             DAYS: return TimeUnit.MILLISECONDS.toDays ( time_since );
				case            HOURS: return TimeUnit.MILLISECONDS.toHours ( time_since );
				case     MICROSECONDS: return TimeUnit.MILLISECONDS.toMicros ( time_since );
				case     MILLISECONDS: return TimeUnit.MILLISECONDS.toMillis ( time_since );
				case          MINUTES: return TimeUnit.MILLISECONDS.toMinutes ( time_since );
				case      NANOSECONDS: return TimeUnit.MILLISECONDS.toNanos ( time_since );
				default: case SECONDS: return TimeUnit.MILLISECONDS.toSeconds ( time_since );
			}
		} else {
			return 0L;
		}
	}
	
//	public static long getTimeInGame(final TimeUnit time_unit) {
//		// get time.
//		long time = 0L;
//		
//		switch(time_unit) {
//		case DAYS:
//			time = TimeUnit.SECONDS.toDays(TIME_IN_GAME);
//			break;
//		case HOURS:
//			time = TimeUnit.SECONDS.toHours(TIME_IN_GAME);
//			break;
//		case MICROSECONDS:
//			time = TimeUnit.SECONDS.toMicros(TIME_IN_GAME);
//			break;
//		case MILLISECONDS:
//			time = TimeUnit.SECONDS.toMillis(TIME_IN_GAME);
//			break;
//		case MINUTES:
//			time = TimeUnit.SECONDS.toMinutes(TIME_IN_GAME);
//			break;
//		case NANOSECONDS:
//			time = TimeUnit.SECONDS.toNanos(TIME_IN_GAME);
//			break;
//		case SECONDS:
//		default:
//			time = TIME_IN_GAME;
//			break;
//		}
//		return time;
//	}
	
	/**
	 * Returns the battle mode
	 * players will play in this server.
	 * <p>
	 * @return the battle mode.
	 */
	public static BattleMode getBattleMode() {
		return BattleModeManager.getBattleMode();
	}
	
	/**
	 * Sets the battle mode
	 * players will play in this serve.
	 * <p>
	 * @param mode to play.
	 */
	public static void setBattleMode(BattleMode mode) throws IllegalArgumentException {
		BattleModeManager.setBattleMode(mode);
	}
	
	/**
	 * Returns the currently
	 * team that is winning the
	 * game.
	 * <p>
	 * @return winning team.
	 */
	private static int MAX_KILLS_BY_A_TEAM = 0;
	public static Team getWinningTeam() {
		BattleMode mode = getBattleMode();
		if (BattleModeUtils.isDeterminatedByKills(mode)) {
			for (Team tm : BRTeam.getLivingTeams()) {
				MAX_KILLS_BY_A_TEAM = (tm.getKills() >= MAX_KILLS_BY_A_TEAM ? tm.getKills() : MAX_KILLS_BY_A_TEAM);
			}
			
			List<Team> teams = BRTeam.getLivingTeams().stream().filter(tm -> (tm.getKills() >= MAX_KILLS_BY_A_TEAM))
					.collect(Collectors.toList());
			
			/* returns null when the game is tie */
			if (teams.size() > 1) {
				return null;
			}
			
			if (!teams.isEmpty()) {
				return teams.get(0);
			}
		} else {
			return (BRTeam.getLivingTeams().size() == 1 ? BRTeam.getLivingTeams().get(0) : null);
		}
		return null;
	}
	
	/**
	 * Returns the current number 
	 * of deaths that have occurred 
	 * while the game is running.
	 * <p>
	 * @return ocurred deaths while the game is running.
	 */
	public static int getGameKills() {
		int kills = 0; for (Team team : BRTeam.getLivingTeams()) { kills += team.getKills(); }
		return kills;
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onGameStart(final GameStartEvent eve) {
		/* update motd */
		MotdManager.setRunning(true);
		MotdManager.setAvailable(false);
		
		// get Battle Map.
		final BattleMap map = MapsManager.BATTLE_MAP;
		
		// prepare world.
		map.preareWorld();
		
		// delayed.
		SchedulerUtil.runTaskLater ( new Runnable() {
			@Override
			public void run() {
				// send players to the battle map.
				for (BRPlayer bp : BRPlayer.getBRPlayers()) {
					if (bp == null || bp.isDead()) {
						continue;
					}
					map.sendToSpawn(bp.getPlayer(), true, true);
				}
				
				// climb members to his vehicle.
				map.mountPlayersOnVehicles();
				
				/* stop lobby scoreboard */
				LobbyScoreboardHandler.getInstance().stopUpdaterTask();
				
				/* start updating the game scoreboard */
				GameScoreboardHandler.getInstance().startUpdaterTask();
				
				// start border timer.
				new BorderTimer(BattleRoyale.getInstance());
				
				// start cardinal bars updater.
				new CardinalBarsUpdater(BattleRoyale.getInstance());
			}
		} , 20 , BattleRoyale.getInstance ( ) );
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onGameEnd(GameEndEvent eve) {
//		/* stop watch task */
//		WATCH_TASK.cancel();
		
		/* fireworks at the end of the game */
		if (Config.GAME_END_FIREWORKS.getAsBoolean()) {
			SchedulerUtil.runTaskTimer ( ( ) -> {
				Location location = null;
				BRPlayer bp = BRPlayer.getBRPlayers().stream().filter(BRPlayer :: isLiving).findAny().orElse(null);
				if (bp != null) {
					location = bp.getPlayer().getLocation();
				} else {
					if (!Bukkit.getOnlinePlayers().isEmpty()) {
						location = new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0).getLocation();
					}
				}
				
				/* throw fireworks */
				if (location != null) {
					for (int times = 0; times < 3; times ++) {
						Location spawn = location;
						SchedulerUtil.runTaskLater ( ( ) -> {
							/* throw */
							Firework firework = spawn.getWorld().spawn(spawn, Firework.class);
							FireworkMeta meta = firework.getFireworkMeta();
							
							/* add colors and form */
							FireworkEffect.Type [ ] types = FireworkEffect.Type.values ( );
							FireworkEffect.Type      type = types [ RandomUtils.nextInt ( types.length ) ];
							
							Color first_color = Color.fromBGR ( RandomUtils.nextInt ( 255 ) , RandomUtils.nextInt ( 255 ) , 
									RandomUtils.nextInt ( 255 ) );
							Color second_color = Color.fromBGR ( RandomUtils.nextInt ( 255 ) , RandomUtils.nextInt ( 255 ) , 
									RandomUtils.nextInt ( 255 ) );
							
							FireworkEffect effect = FireworkEffect.builder ( )
									.withFade ( second_color )
									.withColor ( first_color )
									.with ( type )
									.flicker ( RandomUtils.nextBoolean ( ))
									.build ( );
							
							meta.addEffect(effect);
							meta.setPower(RandomUtils.nextInt(2) + 1);
							firework.setFireworkMeta(meta);
						}, 8, BattleRoyale.getInstance());
					}
				}
			}, 0, 35, BattleRoyale.getInstance());
		}
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}