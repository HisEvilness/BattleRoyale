package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * disallow the friendly fire,
 * using weapons of the plugin 
 * CrackShot or CrackShotPlus.
 * <p>
 * @author AdrianSR.
 */
public final class CrackShotFriendlyFire implements Listener {
	
	/**
	 * Construct listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public CrackShotFriendlyFire ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onFriendlyFire(com.shampaggon.crackshot.events.WeaponDamageEntityEvent event) {
		if (!(event.getVictim() instanceof Player)) {
			return;
		}
		
		BRPlayer  victim = BRPlayer.getBRPlayer(event.getVictim().getUniqueId());
		BRPlayer damager = BRPlayer.getBRPlayer(event.getPlayer().getUniqueId());
		if ( (!victim.hasTeam() || !damager.hasTeam()) || Objects.equals(victim.getTeam(), damager.getTeam()) 
				|| ParachuteListener.drivingParachute((Player) event.getVictim()) ) {
			event.setDamage(0.0D);
			event.setCancelled(true);
		}
	}
}