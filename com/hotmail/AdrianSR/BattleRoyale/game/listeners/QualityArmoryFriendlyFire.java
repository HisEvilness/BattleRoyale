package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * disallow the friendly fire,
 * using weapons of the plugin Quality Armory.
 * <p>
 * @author AdrianSR.
 */
public final class QualityArmoryFriendlyFire implements Listener {
	
	/**
	 * Construct listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public QualityArmoryFriendlyFire ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onFriendlyFire(final me.zombie_striker.qg.api.QAWeaponDamageEntityEvent event) {
		Entity damaged_entity = event.getDamaged();
		if (!(damaged_entity instanceof Player)) {
			return;
		}
		
		BRPlayer damager = BRPlayer.getBRPlayer(event.getPlayer().getUniqueId());
		BRPlayer  victim = BRPlayer.getBRPlayer(damaged_entity.getUniqueId());
		if ( (!victim.hasTeam() || !damager.hasTeam()) || Objects.equals(victim.getTeam(), damager.getTeam()) 
				|| ParachuteListener.drivingParachute((Player) damaged_entity) ) {
			event.setDamage(0.0D);
			event.setCanceled(true);
		}
	}
}