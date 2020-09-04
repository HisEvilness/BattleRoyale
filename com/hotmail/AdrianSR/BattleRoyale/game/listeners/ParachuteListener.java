package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberJumpFromFlyingVehicle;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.Parachute;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * The class that allows the
 * players to open their parachutes
 * playing battle royale.
 * <p>
 * @author AdrianSR
 */
public final class ParachuteListener implements Listener {
	
	/**
	 * Global class values.
	 */
	private static final Set < UUID >    RECENTLY_DISMOUNTING = new HashSet < > ( );
	private static final Map<UUID, Long>          LAST_USAGES = new HashMap<UUID, Long>();
	private static final Map<UUID, Parachute> LAST_PARACHUTES = new HashMap<UUID, Parachute>();
	private static final long                     USAGE_DELAY = 1L; // seconds.
	private static final double         USAGE_GROUND_DISTANCE = 8;

	/**
	 * Construct a new Parachute listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public ParachuteListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
		
		// start bar sender task.
		SchedulerUtil.runTaskTimer ( new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					// get player.
					BRPlayer bp = BRPlayer.getBRPlayer(p);

					// check parachute.
					if ( !( RECENTLY_DISMOUNTING.contains ( p.getUniqueId ( ) ) || bp.hasParachute ( ) 
							|| GameManager.getBattleMode ( ).isRedeployEnabled ( ) ) || bp.isSpectator ( ) ) {
						continue;
					}
					
					// check is not knocked.
					if (bp.isKnocked()) {
						continue;
					}
					
					// check is on air.
					if (!isOnAir(p)) {
						continue;
					}
					
					if (!BRVehicle.playersCanClose()) {
						continue;
					}
					
					// check is dismount from his vehicle.
					if (Vehicle.hasVehicle(p)) {
						continue;
					}
					
					// check gamemode.
					if (p.getGameMode() != GameMode.SURVIVAL && p.getGameMode() != GameMode.ADVENTURE) {
						continue;
					}

					// send bar.
//					ActionBars.sendActionBar(p, Lang.BAR_HAVE_PARACHUTE.getValue(true));
					ActionBarUtil.send ( p , Lang.BAR_HAVE_PARACHUTE.getValue ( true ) );
				}
			}
		}, 40 , 40 , plugin);
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onJumpsFromVehicle ( final MemberJumpFromFlyingVehicle event ) {
		if ( event.isAutoParachute ( ) ) {
			RECENTLY_DISMOUNTING.add ( event.getMember ( ).getUUID ( ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void autoOpening ( PlayerMoveEvent event ) {
		Player player = event.getPlayer ( );
		if ( RECENTLY_DISMOUNTING.contains ( player.getUniqueId ( ) ) && event.getTo ( ).getY ( ) < event.getFrom ( ).getY ( ) ) {
			if  ( closeToGround ( player ) ) {
				openParachute ( player ); /* just open! (saving the life of the player)! */
				BRPlayer.getBRPlayer ( player ).setHasParachute ( false );
				RECENTLY_DISMOUNTING.remove ( player.getUniqueId ( ) );
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDimountVehicle(MemberJumpFromFlyingVehicle event) {
		LAST_PARACHUTES.remove(event.getMember().getUUID());
	}
	
	/**
	 * Detect if the player wants to open his parachute.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onParachute(final PlayerToggleSneakEvent event) {
		// check is not cancelled.
		if (event.isCancelled()) {
			return;
		}
		
		/* event data */
		final Player    p = event.getPlayer();
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		
		// check can open.
		if (!canOpenParachute(p)) {
			return;
		}
		
		// check has parachute.
		if ( !bp.hasParachute ( ) ) {
			return;
		}
		
		// check is not sneaking.
		if (event.isSneaking()) {
			return;
		}
		
		// Check is not player on ground.
		if (p.isOnGround()) {
			return;
		}
		
		// check ground - player distance is >= USAGA_GROUND_DISTANCE.
		if (!isOnAir(p)) {
			return;
		}
		
		// remove parachute.
		bp.setHasParachute(false);
		
		/* open parachute */
		openParachute(p);
	}
	
	/**
	 * Open player parachute.
	 * <p>
	 * @param p the parachute owner.
	 */
	public static void openParachute(final Player p)  {
		final Parachute parachute = new Parachute(p);
		
		// update parachute.
		updateLastParachute(p, parachute);
		
		// register events.
		parachute.register();
				
		// mount parachute.
		parachute.mount(p);
		
		// update usage.
		updateLastUsage(p);
		
		// don't auto-open
		RECENTLY_DISMOUNTING.remove ( p.getUniqueId ( ) );
	}
	
	/**
	 * Check if a player can open the parachute.
	 * <p>
	 * @param p the player to check.
	 * @return true if can.
	 */
	public static boolean canOpenParachute(final Player p) {
		// check is on the battle map.
		if (!LocUtils.isOnBattleMap(p)) {
			return false;
		}
		
		// check vehicle.
		if (Vehicle.hasVehicle(p)) {
			return false;
		}
		
		/* check game mode */
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR 
				|| bp.isSpectator()) {
			return false;
		}
		
		// check players can dismount vehicles.
		if (!BRVehicle.playersCanClose()) {
			return false;
		}
		
		// get current millis.
		final long current = System.currentTimeMillis();
		
		// check time from flying vehicle dismount.
		long close_millis = BRVehicle.getDismountMillis ( p );
		if ( close_millis != -1L ) {
			if ( ( ( current - close_millis ) / 1000 ) < 1 ) {
				// wait a bit please...
				return false;
			}
		} else {
			// player still flying with his vehicle
			return false;
		}
		
//		final Long instan = bp.getAssignableData(BRVehicle.KEY_VEHICLE_DISMOUNT_INSTANT_DATA, Long.class);
//		if ( instan != null ) {
//			final long total = ( ( current - instan.longValue() ) / 1000 );
//			if ( total < 1 ) {
//				return false;
//			}
//		} else { // if the player never has dismount his vehicle.
//			return false; 
//		}
		
		// check last parachute.
		if (drivingParachute(p)) {
			return false;
		}
		
		// get last usage and check it.
		final Long last = LAST_USAGES.get(p.getUniqueId());
		if (last == null) {
			return true;
		}
		
		// get time.
		final long time = ( ( current - last.longValue() ) / 1000 );
		
		// return check
		return (time >= USAGE_DELAY);
	}
	
	public static Parachute getLastParachute(Player player) {
		return LAST_PARACHUTES.get(player.getUniqueId());
	}
	
	public static boolean drivingParachute(Player player) {
		return (getLastParachute(player) != null && !getLastParachute(player).isFinished());
	}
	
	/**
	 * Check if a player is on air.
	 * <p>
	 * @param p the player to check.
	 * @return true if is on.
	 */
	private static boolean isOnAir(final Player p) {
		return isOnAir(p, (int) USAGE_GROUND_DISTANCE);
	}
	
	private static boolean isOnAir(final Player p, int ground_distance) {
		final Location loc = p.getLocation();
		for (int x = 0; x < (ground_distance + 1); x++) {
			Block down = loc.getBlock().getRelative(BlockFace.DOWN, Math.max(x, 1));
			if (down.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if the player is close to 
	 * the ground and the auto parachute is
	 * necesary to save the life!!!
	 * <p>
	 * @param player the player to save!!!!
	 * @return true when is close to the ground.
	 */
	private static boolean closeToGround(Player player) {
		Location loc = player.getLocation();
		int    close = (int) ( USAGE_GROUND_DISTANCE * 2 );
		for (int x = 1; x <= close; x++) {
			if (!loc.getBlock().getRelative(BlockFace.DOWN, x).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if a player is inside some vehicle
	 * <p>
	 * @param p the player to check.
	 * @return true if the player is inside.
	 */
//	private static boolean inInsideVehicle(final Player p) {
//		return p.isInsideVehicle() || p.getVehicle() != null;
//	}
	
	/**
	 * Update the last parachute usage instant.
	 * <p>
	 * @param p the player to update.
	 */
	private static void updateLastUsage(final Player p) {
		LAST_USAGES.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
	}
	
	/**
	 * Update the last used parachute.
	 * <p>
	 * @param p the player to update.
	 */
	private static void updateLastParachute(final Player p, final Parachute parachute) {
		LAST_PARACHUTES.put(p.getUniqueId(), parachute);
	}
}