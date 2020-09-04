package com.hotmail.AdrianSR.BattleRoyale.game.managers;

import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public final class MotdManager extends PluginHandler {
	
	private static boolean AVAILABLE;
	private static boolean   RUNNING;
	private static int  PLAYERS_LEFT;
	private static int   MAX_PLAYERS;
	
	public MotdManager(final BattleRoyale plugin) {
		super(plugin);
		
		/* start updater task */
		SchedulerUtil.runTaskTimer ( ( ) -> {
			updateMotd ( );
		} , 60 , 60 , plugin );
	}
	
	public static void setAvailable(boolean available) {
		AVAILABLE = available;
	}

	public static void setRunning(boolean running) {
		RUNNING = running;
	}
	
	public static void setPlayersLeft(int players_left) {
		PLAYERS_LEFT = players_left;
	}
	
	public static void setMaxPlayers(int max_players) {
		MAX_PLAYERS = max_players;
	}
	
	public static void updateMotd() {
		if (Config.BUNGEECORD.getAsBoolean()) { /* just change motd if the bungeecord is enabled */
			try { /* make and set motd */
				StringBuilder motd_builder = new StringBuilder()
				.append("{")
				.append(AVAILABLE).append(";")
				.append( RUNNING ? "running" : "waiting" ).append(";")
				.append(PLAYERS_LEFT).append(";")
				.append(MAX_PLAYERS)
				.append("}");
				
				BukkitReflection.setMotd ( motd_builder.toString ( ) );
//				ReflectionUtils.setServerMotd(motd_builder.toString());
				
				/* log motd updated */
//				PLUGIN.getFileLogger().info("[STATUS MOTD] The server status motd has been updated successfully...");
			} catch(Throwable t) { /* log error */
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Couldn't update the server status motd!" , BattleRoyale.getInstance ( ) );
				t.printStackTrace();
//				PLUGIN.getFileLogger().log(Level.SEVERE, "[STATUS MOTD] Could not update the server status motd:", t);
			}
		} else { /* log status motd disabled */
//			PLUGIN.getFileLogger().info("[STATUS MOTD] The bungeecord is disable, this server status won't be updated!");
		}
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}