package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.events.DeathCause;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberDeathEvent;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberKnockedEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * Represents a class that
 * is listen the knock events.
 * <p>
 * @author AdrianSR.
 */
public final class MemberKnockListener implements Listener {
	
	/**
	 * Armor Stand seats for knocked players METADATA.
	 */
	public static final String KNOCKED_MEMBERS_SEATS_METADATA = "SEAT-FOR-A-KNOCKED-PLAYER";
	
	/**
	 * Construct a new knock events listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberKnockListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
		
		// start bleeding out task.
		SchedulerUtil.runTaskTimer ( new Runnable ( ) {
			@Override public void run ( ) {
				for ( Player player : Bukkit.getOnlinePlayers ( ) ) {
					BRPlayer br_player = BRPlayer.getBRPlayer ( player );
					if ( !br_player.isKnocked ( ) ) {
						continue;
					}
					
					if ( !br_player.isBeingReanimated ( ) ) {
						final Member knocker = br_player.getKnocker ( );
						if ( ( player.getHealth ( ) - 1.0D ) <= 0.0D || br_player.getTeam ( ).getLivingMembers ( ).isEmpty ( ) ) {
							br_player.setKnocked ( false );
							br_player.setKnocker ( null );
							
							new MemberDeathEvent ( br_player , DeathCause.BLEEDING_OUT , 
									knocker != null && knocker.isOnline ( ) ? knocker : null , player.getName ( ) + " died bleeding out!" , 
									player.getLocation ( ).add ( 0.0D , 1.0D , 0.0D ) , 
									ItemStackUtil.getAllContents ( player.getInventory ( ) , false ) ).call ( );
						} else {
							player.damage ( 1.0D );
							ActionBarUtil.send ( player , Lang.BAR_BLEEDING_OUT.getValue ( true ) );
						}
						
					}
				}
			}
		} , 20 , 20 , plugin );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onLeaveKnockSeat ( EntityDismountEvent event ) {
		if ( !( event.getEntity ( ) instanceof Player ) ) {
			return;
		}
		
		Player      player = (Player) event.getEntity ( );
		BRPlayer br_player = BRPlayer.getBRPlayer ( player );
		
		if ( br_player.isKnocked ( ) && br_player.getKnockSeat ( ) != null 
				&& Objects.equals ( br_player.getKnockSeat ( ).getUniqueId ( ) , event.getDismounted ( ).getUniqueId ( ) ) ) {
			// we're clearing the renewing the knock seat by setting player as not-knocked,
			// and then we set it knocked again.
			br_player.setKnocked ( false );
			br_player.setKnocked ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onMemberKnocked ( MemberKnockedEvent event ) {
		GameUtils.sendGlobalMessage ( event.getKnockMessage ( ) );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onInteract ( PlayerInteractEvent event ) {
		if ( BRPlayer.getBRPlayer ( event.getPlayer ( ) ).isKnocked ( ) ) {
			event.setUseInteractedBlock ( Result.DENY );
			event.setUseItemInHand ( Result.DENY );
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onDisconect ( PlayerQuitEvent event ) {
		BRPlayer.getBRPlayer ( event.getPlayer ( ) ).setKnocked ( false );
	}
	
	/**
	 * Eject knocked seats.
	 */
	@EventHandler (priority = EventPriority.MONITOR)
	public void onDisable(final PluginDisableEvent eve) {
		// check battle map world.
		final BattleMap map = MapsManager.BATTLE_MAP;
		if (map == null) {
			return;
		}
		
		// check world.
		if (map.getWorld() == null) {
			return;
		}
		
		// check plugin.
		if (!eve.getPlugin().getName().equals(BattleRoyale.getInstance().getName())) {
			return;
		}
		
		// eject knocked seats.
		for ( BRPlayer player : BRPlayer.getBRPlayers ( ) ) {
			player.setKnocked ( false );
		}
	}
}