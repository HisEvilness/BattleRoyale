package com.hotmail.AdrianSR.BattleRoyale.game.time.border;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.events.BorderShrinkingChangeEvent;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsYamlManager;
import com.hotmail.adriansr.core.util.Duration;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.titles.TitlesUtil;

/**
 * Represents the timer that manages the
 * world border of the game, change its
 * size and its location.
 * <p>
 * @author AdrianSR
 */
public final class BorderTimer implements Runnable {
	
	private static BorderTimer TIMER;
	
	public static BorderTimer getInstance ( ) {
		return TIMER;
	}
	
	/**
	 * Class values.
	 */
	protected final BattleRoyale              plugin;
	protected final int                      task_id;
	protected final WorldBorder               border;
	private         int                        index;
	private   final List < BorderShrink > succession = new ArrayList < > ( );
	private         BorderStatus              status;
	protected       Location                  center; // current border center
	
	private final BorderShrink initial;
	private       BorderShrink current;
	private       BorderShrink    next;
	
	private Long millis;
	
	protected BorderShrinkingTimer shrinking_timer;
	
	/**
	 * Construct a new Border Timer.
	 * <p>
	 * @param plugin the BattleRoyale instance.
	 */
	public BorderTimer ( final BattleRoyale plugin ) {
		if ( BorderTimer.TIMER == null ) {
			BorderTimer.TIMER = this;
			this.plugin       = plugin;
		} else {
			throw new UnsupportedOperationException ( "timer already initialized!" );
		}
		
		BattleMap            map = MapsManager.BATTLE_MAP;
		BRMapsYamlManager config = map.getConfig();
		
		// world border to handle as needed
		this.border = map.getWorld ( ).getWorldBorder ( );
		// (this is the amount of blocks a player may safely be outside the border before taking damage)
		this.border.setDamageBuffer ( 0.0D ); 
		
		// map configuration checking
		boolean flag0 = config.getMapCenter ( ) == null;
		boolean flag1 = config.getBorderSuccession ( ) == null;
		
		if ( flag0 || flag1 ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , "incomplete border configuration: " , plugin );
			
			if ( flag0 ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "- game area not set!" , plugin );
			}
			
			if ( flag1 ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "- border shrinking succession not set!" , plugin );
			}
			
			this.status = BorderStatus.STOPPED;
		} else {
			this.status = BorderStatus.WAITING;
		}
		
		if ( flag0 ) {
			this.initial = null;
			this.center  = null;
		} else {
			// initial border shrink. also, this will be the permanent if the border
			// succession is not set, but the game are is set.
			this.initial = new BorderShrink ( config.getMapCenter ( ) , ( config.getArea ( ).getRadius ( ) / 2 ) , 0.0D , 1 , 
						TimeUnit.SECONDS , 1 , TimeUnit.SECONDS );
			this.succession.add ( 0 , initial );
						
			// map center, represented by the center of the game area.
			this.center = config.getMapCenter ( );
			this.border.setCenter ( config.getMapCenter ( ) );
			this.border.setSize ( map.getArea ( ).getRadius ( ) / 2 );
		}
		
		if ( !flag1 ) {
			// border shrinking succession configuration
			this.succession.addAll ( config.getBorderSuccession ( ).getValidShrinks ( ) );
		}
		
		// we start the timer only if the configuration is valid/complete.
//		if ( succession.size ( ) > 1 ) {
		if ( !succession.isEmpty ( )  ) {
			this.task_id = Bukkit.getScheduler ( ).scheduleSyncRepeatingTask ( plugin , this , 0L , 0L );
		} else {
			// -1 indicates it was never started.
			this.task_id = -1;
		}
		
		this.millis = System.currentTimeMillis();
	}
	
	@Override
	public void run ( ) {
		if ( succession.isEmpty ( ) ) { //  border == null || ( index + 1 ) >= succession.size() 
			stop ( ); return;
		}
		
		/* update points */
		this.current = succession.get ( index );
		this.next    = hasNextPoint ( ) ? succession.get ( index + 1 ) : null;
		
		/* manage border based on its status */
		Duration time = getTime ( ); // time since last status change
		switch ( status ) {
		case WAITING: {
			if ( time.toMillis ( ) >= current.getTimeToStart ( ).toMillis ( ) ) { // the 'waiting' phase has finished.
				this.status = BorderStatus.SHRINKING;
				this.statusChanged ( );
				
				// resize border
				this.border.setSize ( current.getRadius ( ) , 
						current.getShrinkingTime ( ).toSeconds ( ) );
				// renew shrinking timer
				this.shrinking_timer = new BorderShrinkingTimer ( this );
			}
			break;
		}

		case SHRINKING: {
			if ( time.toMillis ( ) >= current.getShrinkingTime ( ).toMillis ( ) ) { // the 'shrinking' phase has finished.
				if ( next ( ) ) {
					if ( index > 0 ) {
						new BorderShrinkingChangeEvent ( current , next ).call ( );
					}
					
					this.status = BorderStatus.WAITING;
				} else {
					this.status = BorderStatus.STOPPED;
				}
				
				this.statusChanged ( );
				this.shrinking_timer = null;
			} else {
				if ( shrinking_timer != null ) {
					this.shrinking_timer.run ( );
				}
			}
			break;
		}
		default: break;
		}
	}
	
	/**
	 * Called when the status of the world border
	 * is changed.
	 */
	private void statusChanged ( ) { /* send info about the status change */
		String message = Lang.INFO_BORDER_SHRINKING_STARTED_TITLE.getValue(true);
		switch (status) {
		case WAITING:
			message = Lang.INFO_BORDER_SHRINK_TITLE.getValue(true).replace(Lang.NUMBER_REPLACEMENT_KEY,
					format(next.getTimeToStart().toMillis()));
			break;
		case STOPPED:
			message = Lang.INFO_BORDER_STOPPED_TITLE.getValue(true);
			break;
		default:
			break;
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
//			Titles.sendTitleMessages(p, "", message, 10, 80, 10);
			TitlesUtil.send ( p, "", message, 10, 80, 10 );
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 4F, 1F);
		}
		
		// hey, update it!
		this.millis = System.currentTimeMillis ( );
	}
	
	/**
	 * Change of shrink point only if there
	 * is a next point.
	 */
	private boolean next() {
		if (hasNextPoint()) {
			this.index ++;
			return true;
		}
		return false;
	}
	
	/**
	 * Stop this.
	 */
	private void stop ( ) {
		if ( task_id != -1 ) {
			Bukkit.getScheduler ( ).cancelTask ( task_id );
		}
		
		this.status = BorderStatus.STOPPED;
		statusChanged ( );
	}
	
	/**
	 * The time that has passed from the last change of status.
	 * <p>
	 * @return the time that has passed from the last change of status.
	 */
	public Duration getTime ( ) {
		return Duration.ofMilliseconds ( System.currentTimeMillis ( ) - millis );
	}
	
	/**
	 * Get time format.
	 * <p>
	 * @param miliseconds the time in miliseconds.
	 * @return
	 */
	private static String format(long miliseconds) {
		return DurationFormatUtils.formatDuration(miliseconds, "mm:ss");
	}
	
	/**
	 * Get current border shrinking point.
	 * <p>
	 * @return current border shrinking point.
	 */
	public BorderShrink getCurrentShrink() {
		return current;
	}
	
	/**
	 * Get next border shrink point.
	 * <p>
	 * @return next border shrink point.
	 */
	public BorderShrink getNextShrink() {
		return next;
	}
	
	/**
	 * Gets the border center.
	 * <p>
	 * @return the border center.
	 */
	public Location getBorderCenter() {
		return border.getCenter();
	}
	
	/**
	 * Get the current border radius.
	 * <p>
	 * @return current border radius.
	 */
	public double getBorderRadius() {
		return border.getSize();
	}
	
	/**
	 * @return the current border status.
	 */
	public BorderStatus getStatus() {
		return status;
	}
	
	/**
	 * Returns true if the border is shrinking.
	 * <p>
	 * @return true if the border is shrinking.
	 */
	public boolean isShrinking() {
		return status == BorderStatus.SHRINKING;
	}
	
	/**
	 * Returns true if the border is stopped.
	 * <p>
	 * @return true if the border is stopped.
	 */
	public boolean isBorderStopped() {
		return status == BorderStatus.STOPPED;
	}
	
	/**
	 * Get the time to the next border shrinking.
	 * <p>
	 * @return the time to the next border shrinking.
	 */
	public long getTimeToShrinking() {
		if (isShrinking() || current == null || getTime() == null) {
			return 0L;
		}
		return Duration.ofMilliseconds(current.getTimeToStart().toMillis() - getTime().getDuration()).toSeconds();
	}
	
	/**
	 * Get the current shrinking progress.
	 * <p>
	 * @return the current shrinking progress.
	 */
	public long getShrinkingProgress() {
		if (!isShrinking() || current == null || getTime() == null) {
			return 0L;
		}
		return Duration.ofMilliseconds(current.getShrinkingTime().toMillis() - getTime().getDuration()).toSeconds();
	}
	
	/**
	 * Get a format time of the next border shrinking.
	 * <p>
	 * @return the next shrinking formated time.
	 */
	public String getFormatTimeToShrinking() {
		return format(TimeUnit.SECONDS.toMillis(getTimeToShrinking()));
	}
	
	/**
	 * Get a format time of the current border shrinking.
	 * <p>
	 * @return the current shrinking formated time.
	 */
	public String getFormatShrinkingProgress() {
		return format(TimeUnit.SECONDS.toMillis(getShrinkingProgress()));
	}
	
	/**
	 * Check if the border will change his point.
	 * <p>
	 * @return true if will change.
	 */
	public boolean hasNextPoint() {
		return succession.size() > ( index + 1 );
	}
}

//package com.hotmail.AdrianSR.BattleRoyale.game.time.border;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.commons.lang.time.DurationFormatUtils;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Sound;
//import org.bukkit.WorldBorder;
//import org.bukkit.entity.Player;
//
//import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
//import com.hotmail.AdrianSR.BattleRoyale.events.BorderShrinkingChangeEvent;
//import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
//import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
//import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
//import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
//import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsYamlManager;
//import com.hotmail.AdrianSR.core.titles.Titles;
//import com.hotmail.AdrianSR.core.util.PrintUtils;
//import com.hotmail.AdrianSR.core.util.localization.LocationUtils;
//import com.hotmail.AdrianSR.core.util.time.Duration;
//
///**
// * Represents the timer that manages the
// * world border of the game, change its
// * size and its location.
// * <p>
// * @author AdrianSR
// */
//public final class BorderTimer implements Runnable {
//	
//	private static BorderTimer TIMER;
//	
//	public static BorderTimer getInstance ( ) {
//		return TIMER;
//	}
//	
//	/**
//	 * Class values.
//	 */
//	protected final BattleRoyale            plugin;
//	protected final int                    task_id;
//	protected final WorldBorder             border;
//	private         int                      index;
//	private         List<BorderShrink>  succession;
//	private         BorderStatus            status;
//	protected       Location                center; // current border center
//	
//	private final BorderShrink initial;
//	private       BorderShrink current;
//	private       BorderShrink    next;
//	
//	private Long millis;
//	
//	/**
//	 * Construct a new Border Timer.
//	 * <p>
//	 * @param plugin the BattleRoyale instance.
//	 */
//	public BorderTimer ( final BattleRoyale plugin ) {
//		if ( BorderTimer.TIMER == null ) {
//			BorderTimer.TIMER = this;
//			this.plugin       = plugin;
//		} else {
//			throw new UnsupportedOperationException ( "timer already initialized!" );
//		}
//		
//		BattleMap            map = MapsManager.BATTLE_MAP;
//		BRMapsYamlManager config = map.getConfig();
//		if (config.getBorderSuccession() == null || !LocationUtils.isValidLoc(config.getMapCenter())) {
//			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The Border configuration is incomplete: ", BattleRoyale.getInstance());
//			
//			String[] invalid = new String[2];
//			if (!LocationUtils.isValidLoc(config.getMapCenter())) {
//				invalid[0] = "!Map battle Area is not set!";
//			}
//
//			if (config.getBorderSuccession() == null) {
//				invalid[1] = "!The Border shrinking succession is not set!";
//			}
//			
//			for (String in : invalid) {
//				ConsoleUtil.sendPluginMessage(ChatColor.RED, "- " + in, BattleRoyale.getInstance());
//			}
//			this.border = null;
//		} else {
//			this.border = map.getWorld().getWorldBorder();
//		}
//		
//		this.status     = BorderStatus.WAITING;
//		this.initial    = new BorderShrink(config.getMapCenter(), (config.getArea().getRadius() / 2), 0D, 1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);
//		this.succession = config.getBorderSuccession().getValidShrinks(); this.succession.add(0, initial);
//		this.center     = config.getMapCenter().toLocation();
//		this.border.setCenter(config.getMapCenter().toLocation());
//		this.border.setSize( map.getArea().getRadius() / 2 );
//		this.border.setDamageBuffer(0.0D); // (this is the amount of blocks a player may safely be outside the border before taking damage)
//		
//		/* set task id to -1 if the configuration is invalid, or run if valid */
//		if (border != null && succession.size() > 1) {
//			this.task_id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin , this, 0L, 0L);
//		} else {
//			this.task_id = -1;
//		}
//		this.millis = System.currentTimeMillis();
//	}
//
//	@Override
//	public void run() {
//		if ( border == null ) { //  border == null || ( index + 1 ) >= succession.size() 
//			stop();
//			return;
//		}
//		
//		/* update points */
//		this.current = succession.get(index);
//		this.next    = hasNextPoint() ? succession.get(index + 1) : null;
//		
//		/* manage border based on its status */
//		Duration time = getTime(); // the time that has passed from the last change of status
//		switch (status) {
//		case WAITING: {
//			if (time.toMillis() >= current.getTimeToStart().toMillis()) { // waiting phase finished
//				this.status = BorderStatus.SHRINKING;
//				this.statusChanged();
//				
//				/* shrink border and move its center */
//				this.border.setSize(current.getRadius(), current.getShrinkingTime().toSeconds());
//				new BorderShrinkingTimer(this);
//			}
//			break;
//		}
//
//		case SHRINKING: {
//			if (time.toMillis() >= current.getShrinkingTime().toMillis()) { // shrinking phase finished
//				if (next()) {
//					new BorderShrinkingChangeEvent(current, next).call();
//					this.status = BorderStatus.WAITING;
//				} else {
//					this.status = BorderStatus.STOPPED;
//				}
//				this.statusChanged();
//			}
//			break;
//		}
//		default: break;
//		}
//	}
//	
//	/**
//	 * Called when the status of the world border
//	 * is changed.
//	 */
//	private void statusChanged() { /* send info about the changed status of the border */
//		String message = Lang.INFO_BORDER_SHRINKING_STARTED_TITLE.getValue(true);
//		switch (status) {
//		case WAITING:
//			message = Lang.INFO_BORDER_SHRINK_TITLE.getValue(true).replace(Lang.NUMBER_REPLACEMENT_KEY,
//					format(next.getTimeToStart().toMillis()));
//			break;
//		case STOPPED:
//			message = Lang.INFO_BORDER_STOPPED_TITLE.getValue(true);
//			break;
//		default:
//			break;
//		}
//		
//		for (Player p : Bukkit.getOnlinePlayers()) {
//			Titles.sendTitleMessages(p, "", message, 10, 80, 10);
//			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 4F, 1F);
//		}
//		this.millis = System.currentTimeMillis();
//	}
//	
//	/**
//	 * Change of shrink point only if there
//	 * is a next point.
//	 */
//	private boolean next() {
//		if (hasNextPoint()) {
//			this.index ++;
//			return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * Stop this.
//	 */
//	private void stop() {
//		Bukkit.getScheduler().cancelTask(task_id);
//		this.status = BorderStatus.STOPPED;
//		statusChanged();
//	}
//	
//	/**
//	 * The time that has passed from the last change of status.
//	 * <p>
//	 * @return the time that has passed from the last change of status.
//	 */
//	public Duration getTime() {
//		return Duration.ofMilliseconds(System.currentTimeMillis() - millis);
//	}
//	
//	/**
//	 * Get time format.
//	 * <p>
//	 * @param miliseconds the time in miliseconds.
//	 * @return
//	 */
//	private static String format(long miliseconds) {
//		return DurationFormatUtils.formatDuration(miliseconds, "mm:ss");
//	}
//	
//	/**
//	 * Get current border shrinking point.
//	 * <p>
//	 * @return current border shrinking point.
//	 */
//	public BorderShrink getCurrentShrink() {
//		return current;
//	}
//	
//	/**
//	 * Get next border shrink point.
//	 * <p>
//	 * @return next border shrink point.
//	 */
//	public BorderShrink getNextShrink() {
//		return next;
//	}
//	
//	/**
//	 * Get border center.
//	 * <p>
//	 * @return the border center.
//	 */
//	public Location getBorderCenter() {
//		return border.getCenter();
//	}
//	
//	/**
//	 * Get the current border radius.
//	 * <p>
//	 * @return current border radius.
//	 */
//	public double getBorderRadius() {
//		return border.getSize();
//	}
//	
//	/**
//	 * @return the current border status.
//	 */
//	public BorderStatus getStatus() {
//		return status;
//	}
//	
//	/**
//	 * Returns true if the border is shrinking.
//	 * <p>
//	 * @return true if the border is shrinking.
//	 */
//	public boolean isShrinking() {
//		return status == BorderStatus.SHRINKING;
//	}
//	
//	/**
//	 * Returns true if the border is stopped.
//	 * <p>
//	 * @return true if the border is stopped.
//	 */
//	public boolean isBorderStopped() {
//		return status == BorderStatus.STOPPED;
//	}
//	
//	/**
//	 * Get the time to the next border shrinking.
//	 * <p>
//	 * @return the time to the next border shrinking.
//	 */
//	public long getTimeToShrinking() {
//		if (isShrinking() || current == null || getTime() == null) {
//			return 0L;
//		}
//		return Duration.ofMilliseconds(current.getTimeToStart().toMillis() - getTime().getDuration()).toSeconds();
//	}
//	
//	/**
//	 * Get the current shrinking progress.
//	 * <p>
//	 * @return the current shrinking progress.
//	 */
//	public long getShrinkingProgress() {
//		if (!isShrinking() || current == null || getTime() == null) {
//			return 0L;
//		}
//		return Duration.ofMilliseconds(current.getShrinkingTime().toMillis() - getTime().getDuration()).toSeconds();
//	}
//	
//	/**
//	 * Get a format time of the next border shrinking.
//	 * <p>
//	 * @return the next shrinking formated time.
//	 */
//	public String getFormatTimeToShrinking() {
//		return format(TimeUnit.SECONDS.toMillis(getTimeToShrinking()));
//	}
//	
//	/**
//	 * Get a format time of the current border shrinking.
//	 * <p>
//	 * @return the current shrinking formated time.
//	 */
//	public String getFormatShrinkingProgress() {
//		return format(TimeUnit.SECONDS.toMillis(getShrinkingProgress()));
//	}
//	
//	/**
//	 * Check if the border will change his point.
//	 * <p>
//	 * @return true if will change.
//	 */
//	public boolean hasNextPoint() {
//		return succession.size() > ( index + 1 );
//	}
//}