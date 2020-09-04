package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.reanimation.ReanimationTask;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.math.target.TargetUtil;

/**
 * Re-animating team mates.
 * <p>
 * @author AdrianSR / Sunday 23 August, 2020 / 04:54 PM
 */
public final class MemberReanimationListener implements Listener {
	
	private static final double                        REANIMATION_TARGET_RANGE = 2.0D;
	private static final Map < UUID , ReanimationTask > ACTIVE_REANIMATION_TASK = new HashMap < > ( );

	public MemberReanimationListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler
	public void onReanimation ( PlayerToggleSneakEvent event ) {
		Player      player = event.getPlayer ( );
		BRPlayer br_player = BRPlayer.getBRPlayer ( player );
		
		if ( event.isSneaking ( ) && br_player.isLiving ( ) && !br_player.isKnocked ( ) ) {
			Player target = TargetUtil.getTargetPlayer ( player , REANIMATION_TARGET_RANGE );
			if ( target != null ) {
				BRPlayer br_target = BRPlayer.getBRPlayer ( target );
				if ( br_target.isKnocked ( ) && !br_target.isBeingReanimated ( ) 
						&& br_player.getTeam ( ).equals ( br_target.getTeam ( ) ) ) {
					startReanimation ( br_player , br_target );
					return;
				}
			}
		}
		
		stopReanimation ( br_player );
	}
	
	@EventHandler
	public void onReanimation ( PlayerMoveEvent event ) {
		Player      player = event.getPlayer ( );
		BRPlayer br_player = BRPlayer.getBRPlayer ( player );
		
		if ( br_player.isLiving ( ) && !br_player.isKnocked ( ) ) {
			Player target = TargetUtil.getTargetPlayer ( player , REANIMATION_TARGET_RANGE );
			if ( target != null ) {
				BRPlayer br_target = BRPlayer.getBRPlayer ( target );
				if ( br_target.isKnocked ( ) && !br_target.isBeingReanimated ( ) && br_player.getTeam ( ).equals ( br_target.getTeam ( ) ) ) {
					if ( player.isSneaking ( ) ) {
						startReanimation ( br_player , br_target );
						return;
					} else {
						ActionBarUtil.send ( player , Lang.BAR_REANIMATE_TEAM.getValue ( true ) );
					}
				}
			}
		}
		
		stopReanimation ( br_player );
	}
	
	private void startReanimation ( BRPlayer reanimator , BRPlayer target ) {
		ReanimationTask task = ACTIVE_REANIMATION_TASK.get ( reanimator.getUUID ( ) );
		if ( task == null || !task.getTarget ( ).getUUID ( ).equals ( target.getUUID ( ) ) ) {
			if ( task != null ) {
				try {
					task.cancel ( );
				} catch ( IllegalStateException ex ) {
					// ignored exception
				}
			}
			
			task = new ReanimationTask ( reanimator , target );
			task.runTaskTimer ( BattleRoyale.getInstance ( ) , 0 , 0 );
		}
		
		ACTIVE_REANIMATION_TASK.put ( reanimator.getUUID ( ) , task );
	}
	
	private void stopReanimation ( BRPlayer reanimator ) {
		ReanimationTask task = ACTIVE_REANIMATION_TASK.get ( reanimator.getUUID ( ) );
		if ( task != null ) {
			try {
				task.cancel ( );
			} catch ( IllegalStateException ex ) {
				// ignored exception
			}
		}
		
		ACTIVE_REANIMATION_TASK.remove ( reanimator.getUUID ( ) );
	}
}