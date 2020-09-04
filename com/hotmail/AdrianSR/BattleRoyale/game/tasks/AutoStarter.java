package com.hotmail.AdrianSR.BattleRoyale.game.tasks;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.adriansr.core.util.sound.UniversalSound;
import com.hotmail.adriansr.core.util.titles.TitlesUtil;

/**
 * Represents the Battle
 * Royale Game Auto Starter.
 * <p>
 * @author AdrianSR
 */
public class AutoStarter extends BukkitRunnable {
	
	/**
	 * Countdown seconds to start.
	 */
	private static Integer COUNTDOWN = null;
	
	/**
	 * Get the current
	 * auto start countdown
	 * seconds to start.
	 * <p>
	 * @return current countdown.
	 */
	public static Integer getCountdown() {
		return COUNTDOWN;
	}
	
	/**
	 * Construct a new 
	 * Game Auto Starter.
	 * <p>
	 * @param plugin
	 */
	public AutoStarter(final BattleRoyale plugin) {
		COUNTDOWN = null;
	}

	@Override
	public void run() {
		if (GameManager.isRunning() || !Config.AUTO_START_USE.getAsBoolean()) {
			cancel();
			return;
		}
		
		/* start countdown when ready */
		if (canStart()) {
			if (COUNTDOWN == null) {
				COUNTDOWN = Config.AUTO_START_COUNTDOWN_SECONDS.getAsInteger();
			}
			COUNTDOWN --; /* do countdown */
		} else {
			/* when is auto starting */
			if (COUNTDOWN != null) { /* abort auto start */
				COUNTDOWN = null; 
				
				/* info auto start aborted */
				GameUtils.sendGlobalMessage(Lang.AUTO_START_ABORTED_MESSAGE.getValue(true)
						.replace(Lang.NUMBER_REPLACEMENT_KEY, String.valueOf(Config.AUTO_START_MIN_PLAYERS.getAsInteger())));
			}
		}
		
		// check countdown.
		if (COUNTDOWN == null) {
			return;
		}
		
		// messages.
		for (Player p : Bukkit.getOnlinePlayers()) {
			String message = "";
			Sound    sound = UniversalSound.LEVEL_UP.asBukkit();
			switch(COUNTDOWN) {
			case 5:
			case 4:
				message = ChatColor.RED + ChatColor.BOLD.toString() + String.valueOf(COUNTDOWN);
				sound   = UniversalSound.ORB_PICKUP.asBukkit();
				break;
			case 3:
			case 2:
			case 1:
				message = ChatColor.RED + ChatColor.BOLD.toString() + String.valueOf(COUNTDOWN);
				sound   = COUNTDOWN > 1 ? (UniversalSound.ORB_PICKUP.asBukkit()) : (UniversalSound.LEVEL_UP.asBukkit());
				break;
			default:
				message = "";
				break;
			}
			
			/* info */
			if (!message.isEmpty()) {
				/* info: title */
//				Titles.sendTitleMessages(p, message, null, 6, 80, 6);
				TitlesUtil.send ( p , message , null , 6, 80 , 6 );
				
				/* info: sound */
				p.playSound(p.getLocation(), sound, 2.0F, 0.0F);
			}
		}
		
		/* when the auto start is done */
		if (COUNTDOWN <= 1) {
			GameManager.startGame();
			cancel();
			
			/* clear players screen */
			TitlesUtil.broadcastReset ( );
		}
	}
	
	/**
	 * Returns true if all conditions are met to begin.
	 * <p>
	 * @return true when the game can start.
	 */
	private static boolean canStart() {
		/* donnot start game if there are not enough players */
		if (Bukkit.getOnlinePlayers().size() < Config.AUTO_START_MIN_PLAYERS.getAsInteger()) {
			return false;
		}

		/* donnot start the game if there are not enough teams to play */
		BattleMode mode = GameManager.getBattleMode();
		if (!mode.isSolo()) {
			int        ready_teams = BRTeam.getLivingTeams().stream().filter(team -> !team.getLivingMembers().isEmpty()).collect(Collectors.toList()).size();
			int       without_team = BRPlayer.getBRPlayers().stream().filter(bp -> bp != null && bp.isOnline() && !bp.hasTeam()).collect(Collectors.toList()).size();
			boolean       no_teams = ( ready_teams == 0 );
			boolean  unready_teams = ( ready_teams == 1 ); /* true when there is no more than 2 teams has living members */
			boolean ava_team_slots = ( BRTeam.getTeams().size() < mode.getMaxTeams() || !BattleModeUtils.isLimitedTeams(mode) );
			if ( ( unready_teams && ava_team_slots ) && without_team < 1) {
				return false;
			}
			
			if (no_teams || ( unready_teams && !ava_team_slots ) ) {
				return false;
			}
		}
		return true;
	}
}