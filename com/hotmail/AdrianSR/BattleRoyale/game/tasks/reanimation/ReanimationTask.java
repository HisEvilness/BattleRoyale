package com.hotmail.AdrianSR.BattleRoyale.game.tasks.reanimation;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.titles.TitlesUtil;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Monday 24 August, 2020 / 10:12 AM
 */
public class ReanimationTask extends BukkitRunnable {
	
	protected final BRPlayer reanimator;
	protected final BRPlayer     target;
	
	protected final long time;
	protected       long timestamp = -1;
	
	public ReanimationTask ( BRPlayer reanimator , BRPlayer target ) {
		this.reanimator = reanimator;
		this.target     = target;
		this.time       = TimeUnit.SECONDS.toMillis ( Math.max ( GameManager.getBattleMode ( ).getReanimationSeconds ( ) , 0 ) );
	}
	
	public long getTime ( ) {
		return time;
	}

	/**
	 * 
	 * @return -1 if this task is not scheduled yet.
	 */
	public long getTimestamp ( ) {
		return timestamp;
	}

	public BRPlayer getReanimator ( ) {
		return reanimator;
	}

	public BRPlayer getTarget ( ) {
		return target;
	}

	@Override
	public void run ( ) {
		if ( !reanimator.isLiving ( ) || !target.isLiving ( ) ) {
			cancel ( );
			return;
		}
		
		long millis = System.currentTimeMillis ( );
		if ( timestamp == -1 ) {
			timestamp = millis;
		}
		
		Player reanimator_player = reanimator.getPlayer ( );
		Player     target_player = target.getPlayer ( );
		
		long time_since = millis - timestamp;
		if ( time_since < time ) {
			target.setReanimating ( true );
			
			double        progress = (double) ( time - time_since ) / 1000;
			String progress_string = String.format ( "%.1f" , progress ).replace ( ',' , '.' );
			
			TitlesUtil.send ( reanimator_player , "" , Lang.TITLE_REANIMATION_PROGRESS.getValueReplacingNumber ( progress_string , true ) , 0 , 70 , 0 );
			TitlesUtil.send ( target_player     , "" , Lang.TITLE_REANIMATION_PROGRESS.getValueReplacingNumber ( progress_string , true ) , 0 , 70 , 0 );
			
			ActionBarUtil.send ( reanimator_player , Lang.BAR_REANIMATING_TEAM.getValueReplacingWord ( target_player.getName ( ) , true ) );
			ActionBarUtil.send ( target_player     , Lang.BAR_BEING_REANIMATED.getValue ( true ) );
		} else {
			TitlesUtil.send ( reanimator_player , "" , "" );
			TitlesUtil.send ( target_player , "" , "" );
			
			ActionBarUtil.send ( reanimator_player , Lang.BAR_REANIMATED_TEAM.getValue ( true ) );
			ActionBarUtil.send ( target_player , Lang.BAR_REANIMATED_TEAM.getValue ( true ) );
			
			target.setKnocked ( false );
			target_player.setHealth ( Math.max ( GameManager.getBattleMode ( ).getHealthAfterReanimation ( ) , 1.0D ) );
			
			cancel ( );
		}
	}
	
	@Override
	public synchronized void cancel ( ) throws IllegalStateException {
		target.setReanimating ( false );
		super.cancel ( );
	}
}