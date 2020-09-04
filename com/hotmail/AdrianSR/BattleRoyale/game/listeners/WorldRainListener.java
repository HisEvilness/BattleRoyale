package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * disallow the rain in the battle
 * royale worlds.
 * <p>
 * @author AdrianSR.
 */
public final class WorldRainListener implements Listener {

	/**
	 * Construct a new world rain remover.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public WorldRainListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	/**
	 * Detects time state change
	 * to rain, and disallow it.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void noRain(final WeatherChangeEvent event) {
		// check is stat changed to rain.
		if (event.toWeatherState()) {
			// stop rain.
			event.setCancelled(true);
		}
	}
}