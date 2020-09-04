package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.airsupply.AirSupply;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Saturday 01 August, 2020 / 03:58 PM
 */
public class AirSupplyInteraction implements Listener {

	public AirSupplyInteraction ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onManipulate ( PlayerArmorStandManipulateEvent event ) {
		if ( event.getRightClicked ( ).hasMetadata ( AirSupply.AIR_SUPPLY_META_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onInteract ( PlayerInteractEvent event ) {
		Block clicked = event.getClickedBlock ( );
		if ( clicked != null && ( clicked.hasMetadata ( AirSupply.AIR_SUPPLY_META_KEY ) 
				|| clicked.hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) ) {
			
			event.setCancelled ( true );
			event.setUseInteractedBlock ( Result.DENY );
			
			if ( event.getPlayer ( ).getGameMode ( ) != GameMode.SPECTATOR 
					&& BRPlayer.getBRPlayer ( event.getPlayer ( ) ).getPlayerMode ( ) != BRPlayerMode.SPECTATOR ) {
				if ( clicked.getType ( ) == Material.CHEST 
						&& clicked.hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) {
					AirSupply instance = (AirSupply) ((FixedMetadataValue) 
							clicked.getMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ).get ( 0 )).value ( );
					instance.open ( );
					
					// open sound
					event.getPlayer ( ).playSound ( clicked.getLocation ( ) , Sound.ENTITY_SHULKER_OPEN , 4F , 1F );
					event.getPlayer ( ).playSound ( clicked.getLocation ( ) , Sound.ENTITY_WITCH_DRINK , 4F , 1F );
					event.getPlayer ( ).playSound ( clicked.getLocation ( ) , Sound.BLOCK_LAVA_EXTINGUISH , 4F , 1F );
				}
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onBreak ( BlockBreakEvent event ) {
		if ( event.getBlock ( ).hasMetadata ( AirSupply.AIR_SUPPLY_META_KEY ) ) {
			event.setCancelled ( true );
		}
	}
}