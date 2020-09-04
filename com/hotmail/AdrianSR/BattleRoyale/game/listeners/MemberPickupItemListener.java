package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Listener that avoid members to pickup items at times that don't make any sense.
 * <p>
 * @author AdrianSR / Saturday 15 August, 2020 / 06:52 PM
 */
public class MemberPickupItemListener implements Listener {

	public MemberPickupItemListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onItemPickup ( PlayerPickupItemEvent event ) {
		BRPlayer player = BRPlayer.getBRPlayer ( event.getPlayer ( ) );
		if ( player.isDead ( ) || ( player.isKnocked ( ) || player.isBeingReanimated ( ) ) || player.isSpectator ( ) ) {
			event.setCancelled ( true );
		}
	}
}