package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * <p>
 * @author AdrianSR
 */
public final class MemberLaunchProjectiles implements Listener {
	
	/**
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberLaunchProjectiles ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onLaunch(final ProjectileLaunchEvent event) {
		Projectile   projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) {
			return;
		}
		
		Player player = (Player) source;
		BRPlayer   bp = BRPlayer.getBRPlayer(player);
		if (projectile instanceof Arrow) { /* case arrows */
			if (bp.getPlayerMode() == BRPlayerMode.SPECTATOR) { /* disallow spectators to launch arrows */
				event.setCancelled(true);
				return;
			}
		}
	}
}