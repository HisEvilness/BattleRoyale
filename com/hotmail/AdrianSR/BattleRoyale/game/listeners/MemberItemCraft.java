package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * disallow players to
 * craft any item.
 * <p>
 * @author AdrianSR.
 */
public final class MemberItemCraft implements Listener {
	
	public MemberItemCraft ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onCraft(final CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			if (((Player) event.getWhoClicked()).getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}
}