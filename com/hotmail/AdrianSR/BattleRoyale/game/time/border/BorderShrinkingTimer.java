package com.hotmail.AdrianSR.BattleRoyale.game.time.border;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.util.tps.TPS;

public final class BorderShrinkingTimer implements Runnable {
	
	protected final BorderTimer   timer;
	protected final BorderShrink shrink;
	
	protected final List < Double > tps_history = new ArrayList < > ( );
	
	public BorderShrinkingTimer ( BorderTimer timer ) {
		this.timer  = timer;
		this.shrink = timer.getCurrentShrink ( );
	}
	
	@Override
	public void run ( ) {
		final double distance = getDistance ( shrink.getLocation ( ) , timer.border.getCenter ( ) );
		if ( distance > 0.0D ) {
			final Vector direction = shrink.getLocation ( ).clone ( )
					.subtract ( timer.border.getCenter ( ) ).toVector ( ).normalize ( );
			
			final long           time = timer.getTime ( ).toMillis ( );
			final long shrinking_time = shrink.getShrinkingTime ( ).toMillis ( );
			
			final double d0 = ( shrinking_time - time ); // Loc
			final double d1 = d0 / 1000.0D; // remaining time in seconds
			final double d2 = distance / d1;
			final double d3 = d2 / getTpsAverage ( );
			
			if ( Double.isNaN ( d3 ) || Double.isInfinite ( d3 ) ) {
				timer.border.setCenter ( shrink.getLocation ( ).clone ( ) );
			} else {
				timer.border.setCenter ( timer.border.getCenter ( ).clone ( ).add ( direction.multiply ( d3 ) ) );
			}
		}
	}
	
	// we are using TPS average for a better precision.
	private double getTpsAverage ( ) {
		tps_history.add ( TPS.getTicksPerSecond ( ) );
		if ( tps_history.size ( ) > 1 ) {
			double average = 0;
			for ( double tps : tps_history ) {
				average += tps;
			}
			return average / tps_history.size ( );
		} else {
			return tps_history.get ( 0 );
		}
	}
	
	/**
	 * Calculates the distance between two locations excluding the y component.
	 */
	private double getDistance ( Location loc_a , Location loc_b ) {
		double x = ( loc_a.getX ( ) - loc_b.getX ( ) );
		double z = ( loc_a.getZ ( ) - loc_b.getZ ( ) );
		return Math.sqrt ( ( x * x ) + ( z * z ) );
	}
}

//package com.hotmail.AdrianSR.BattleRoyale.game.time.border;
//
//import org.bukkit.Bukkit;
//import org.bukkit.util.Vector;
//
//import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
//import com.hotmail.AdrianSR.core.util.localization.DirectionUtils;
//import com.hotmail.AdrianSR.core.util.localization.ConfigurableLocation;
//import com.hotmail.AdrianSR.core.util.time.Duration;
//
//public final class BorderShrinkingTimer implements Runnable {
//	
//	private final BorderTimer   timer;
//	private final int         task_id;
//	private final Duration   duration;
//	private final BorderShrink shrink;
//	private final long         millis;
//
//	public BorderShrinkingTimer ( BorderTimer timer ) {
//		this.timer    = timer;
//		this.shrink   = timer.getCurrentShrink();
//		this.duration = timer.getCurrentShrink().getShrinkingTime();
//		this.task_id  = Bukkit.getScheduler().runTaskTimer(timer.plugin, this, 0L, 0L).getTaskId();
//		this.millis   = System.currentTimeMillis();
//	}
//	
//	@Override
//	public void run() {
//		if ( (millis > 100L && getTime().getDuration() >= duration.toMillis()) ) {
//			stop();
//			return;
//		}
//		
//		float        yaw = DirectionUtils.pointLocationTo(timer.getBorderCenter(), shrink.getLocation().toLocation());
//		double  distance = getDistance(shrink.getLocation(), new ConfigurableLocation(timer.getBorderCenter(), true));	
//		Vector direction = DirectionUtils.getDirection(yaw, 0F).multiply( (distance / getTimeRemaining().toSeconds()) / 20 );
//		if (distance > 0.0D && isValidVector(direction)) {
//			timer.border.setCenter(timer.border.getCenter().add(direction));
//		} else {
//			stop();
//		}
//	}
//	
//	private double getDistance(ConfigurableLocation loc_a, ConfigurableLocation loc_b) {
//		double x = (loc_a.getX() - loc_b.getX());
//		double z = (loc_a.getZ() - loc_b.getZ());
//		return Math.sqrt( (x * x) + (z * z) );
//	}
//	
//	private boolean isValidVector(Vector vector) {
//		for (double axis : new double[] { vector.getX(), vector.getZ() }) {
//			if (Double.isInfinite(axis) || Double.isNaN(axis)) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	public Duration getTime() {
//		return Duration.ofMilliseconds(System.currentTimeMillis() - millis);
//	}
//	
//	public Duration getTimeRemaining() {
//		return Duration.ofMilliseconds(shrink.getShrinkingTime().toMillis() - getTime().getDuration());
//	}
//	
//	public void stop() {
//		Bukkit.getScheduler().cancelTask(task_id);
//	}
//	
//	public BorderTimer getTimer() {
//		return timer;
//	}
//}