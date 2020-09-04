package com.hotmail.AdrianSR.BattleRoyale.game.tasks.gameend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.BattleRoyale.config.command.CustomConfigCommand;
import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MotdManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.channel.BRPluginChannel;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * Represents the Battle
 * Royale Game End Task.
 * <p>
 * @author AdrianSR
 */
public class GameEnd extends BukkitRunnable {
	
	/**
	 * Countdown seconds to end.
	 */
	private static Integer Countdown = null;
	
	/**
	 * Get the current
	 * countdown seconds 
	 * to end.
	 * <p>
	 * @return current countdown.
	 */
	public static int getCountdown() {
		return Countdown;
	}
	
	protected boolean initialized;
	
	public GameEnd Initialize() {
		initialized = true;
		return this;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public void run() {
		if (!isInitialized()) {
			cancel();
			return;
		}
		
		// check countdown.
		if (Countdown == null) {
			Countdown = Config.AUTO_STOP_SECONDS.getAsInteger();
		}
		
		// do coundown.
		Countdown --;
		
		// send server restart message.
//		ActionBars.broadCastBar(Lang.AUTO_STOP_MESSAGE.getValueReplacingNumber(Countdown, true));
		ActionBarUtil.broadcast ( Lang.AUTO_STOP_MESSAGE.getValueReplacingNumber ( Countdown , true ) );
		
		// check coundown time.
		if (Countdown <= 1) { // is ready to stop server.
			end();
		}
	}
	
	public void end() {
		/* perform game end commands */
		for (CustomConfigCommand cmd : getGameEndCommands()) { // CustomConfigCommand cmd : Config.getGameEndCommands()
			if (cmd.isForPlayers()) {
				Bukkit.getOnlinePlayers().stream().forEach(player -> {
					player.performCommand(cmd.getArgumentForPlayer(player));
				});
			} else {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.getArgument());
			}
		}
		
		/* kick or connect to other server */
		for ( Player player : Bukkit.getOnlinePlayers ( ) ) {
			if ( Config.KICK_PLAYERS_ON_GAME_END.getAsBoolean ( ) ) { /* kicking */
				player.kickPlayer ( Lang.AUTO_STOP_KICK_MESSAGE.getValue ( true ) );
			} else { /* connecting to other */
				SchedulerUtil.runTaskLater ( ( ) -> 
					BRPluginChannel.getInstance ( ).sendPlayer ( player , Config.LOBBY_ITEM_BUNGEE_SERVER_TARGET.toString ( ) ) , 20 , 
						BattleRoyale.getInstance ( ) ) ;
			}
		}
		
		/* update motd */
		MotdManager.setRunning(false);
		MotdManager.setAvailable(false);
		MotdManager.setPlayersLeft(-1);
		MotdManager.setMaxPlayers(-1);
		
		// perform command.
		SchedulerUtil.runTaskLater ( ( ) -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.AUTO_STOP_COMMAND.toString());
		}, ( Config.KICK_PLAYERS_ON_GAME_END.getAsBoolean() ? 0 : 60 ), BattleRoyale.getInstance());
	}
	
	private List<CustomConfigCommand> getGameEndCommands() {
		List<CustomConfigCommand> commands = new ArrayList<CustomConfigCommand>();
		for (int x = 0; x < 2; x++) {
			boolean     for_players = ( x == 0 );
			Config list_config_item = ( for_players ? Config.GAME_END_PLAYERS_CUSTOM_COMMANDS : Config.GAME_END_CONSOLE_CUSTOM_COMMANDS );
			
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
}