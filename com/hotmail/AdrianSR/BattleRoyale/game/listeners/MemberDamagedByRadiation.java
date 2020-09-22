package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;

/**
 * Represents a class that
 * modify the damage by the world border.
 * <p>
 * @author AdrianSR
 */
public final class MemberDamagedByRadiation implements Listener {
	
	/**
	 * Modify world border damage.
	 * <p>
	 * @author AdrianSR
	 */
	public MemberDamagedByRadiation ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageEvent event) {
		if (GameManager.isNotRunning()) {
			return;
		}

		// check is a player and the damage cause.
		if (event.getEntity() instanceof Player && event.getCause() == DamageCause.SUFFOCATION) {
			final Player player = (Player) event.getEntity();
			if (!LocUtils.isOnBattleMap(player)) {
				return;
			}

			// check player is not inside the border.
			if (LocUtils.isInsideOfBorder(player.getLocation(), player.getWorld().getWorldBorder())) {
				return;
			}
			
			event.setDamage ( player.getWorld ( ).getWorldBorder ( ).getDamageAmount ( ) );
		}
	}
}
