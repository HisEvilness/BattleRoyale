package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that disable the auto Players health regain
 * 
 * @author AdrianSR.
 */
public final class AutoRegainHealth implements Listener {
	
	/**
	 * Construct a new Anti Auto health regain.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public AutoRegainHealth ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onAutoHealth(final EntityRegainHealthEvent event) {
		// get and check entity.
		final Entity ent          = event.getEntity();
		final RegainReason reason = event.getRegainReason();
		if (!(ent instanceof Player)) { // check entity is a player.
			return;
		}
		
		// check regain reason.
		if (reason != RegainReason.SATIATED) { // check is regaining helath by satiated-
			return;
		}
		
		// cancell.
		event.setCancelled(true);
	}
}
