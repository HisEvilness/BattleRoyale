package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that disable
 * the food level change.
 * <p>
 * @author AdrianSR.
 */
public final class FoodLevelChange implements Listener {
	
	/**
	 * Construct a new Anti food level change.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public FoodLevelChange ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChange(final FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
}
