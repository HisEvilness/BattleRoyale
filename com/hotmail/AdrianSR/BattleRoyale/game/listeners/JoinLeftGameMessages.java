package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents the class 
 * of the Battle royale plugin
 * that remove join an left game
 * messages.
 * <p>
 * @author AdrianSR.
 */
public final class JoinLeftGameMessages implements Listener {
	
	/**
	 * Construct new join-left game messages
	 * remover.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public JoinLeftGameMessages ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	/**
	 * Remove join game message. 
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(final PlayerJoinEvent event) {
		event.setJoinMessage("");
	}
	
	/**
	 * Remove left game message. 
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onQuit(final PlayerQuitEvent event) {
		event.setQuitMessage("");
	}
}