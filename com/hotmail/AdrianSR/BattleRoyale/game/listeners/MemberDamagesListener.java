package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hotmail.AdrianSR.BattleRoyale.events.DeathCause;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberDeathEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.RespawnAndPositionSender;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Movable;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.Parachute;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

/**
 * Represents a class that 
 * disallow players to get
 * damage from unallowed 
 * causes.
 * <p>
 * @author AdrianSR.
 */
public final class MemberDamagesListener implements Listener {
	
	/**
	 * Disable players to get damage from
	 * unallowed causes.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberDamagesListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	/**
	 * Avoid the players to
	 * get damage when its damage ticks
	 * are more than 0.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onFallDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) { /* check is a player */
			return;
		}
		
		/* disallow first falls damages */
		Player player = (Player) event.getEntity();
		if (player.getNoDamageTicks() > 0) {
			event.setDamage(0D);
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void friendlyDamage(final EntityDamageByEntityEvent event) {
		Entity  victim_entity = event.getEntity();
		Entity damager_entity = (event.getDamager() instanceof Projectile
				? (Entity) ((Projectile) event.getDamager()).getShooter()
				: event.getDamager());
		if (!(victim_entity instanceof Player) || !(damager_entity instanceof Player)) {
			return;
		}

		BRPlayer victim = BRPlayer.getBRPlayer(victim_entity.getUniqueId());
		BRPlayer damager = BRPlayer.getBRPlayer(damager_entity.getUniqueId());
		if ((!victim.hasTeam() || !damager.hasTeam()) || Objects.equals(victim.getTeam(), damager.getTeam())
				|| !victim.isLiving() || !damager.isLiving() || damager.isKnocked ( ) ) {
			event.setDamage(0.0D);
			event.setCancelled(true);
		}

		if (damager.getPlayerMode() == BRPlayerMode.SPECTATOR || victim.getPlayerMode() == BRPlayerMode.SPECTATOR) {
			event.setDamage(0.0D);
			event.setCancelled(true);
		}
	}

	@EventHandler ( priority = EventPriority.LOWEST )
	public void onDamage ( EntityDamageEvent event ) {
		if ( GameManager.isRunning ( ) && event.getEntity ( ) instanceof Player ) {
			final Player     player = (Player) event.getEntity();
			final GameMode gamemode = player.getGameMode ( );
			final BRPlayer       bp = BRPlayer.getBRPlayer ( player );
			
			if ( event.getCause ( ) == DamageCause.VOID ) {
				Location respawn_location = null;
				if ( bp.hasTeam ( ) && bp.getTeam ( ).getLivingMembers ( ).size ( ) > 1 ) {
					Member team_mate = bp.getTeam ( ).getLivingMembers ( ).stream ( )
							.filter ( other -> !other.getUUID ( ).equals ( player.getUniqueId ( ) ) ).findAny ( ).orElse ( null );
					if ( team_mate != null && team_mate.getPlayer ( ).getLocation ( ).getY ( ) > 0 ) {
						respawn_location = team_mate.getPlayer ( ).getLocation ( );
					}
				}
				
				if ( respawn_location == null ) {
					Location random_spawn = MapsManager.BATTLE_MAP.getConfig ( ).getRandomSpawn ( );
					if ( random_spawn != null ) {
						respawn_location = random_spawn;
					}
				}
				
				if ( respawn_location == null ) {
					respawn_location = player.getWorld ( ).getSpawnLocation ( );
				}
				
				if ( bp.isDead ( ) ) {
					new RespawnAndPositionSender ( bp , respawn_location , "" , "" )
							.runTaskLater ( BattleRoyale.getInstance ( ) , 2L );
				} else {
					if ( Vehicle.hasVehicle ( player ) ) {
						Vehicle.getVehicle ( player ).close ( );
					}
					
					for ( Movable movable : Movable.MOVABLES ) {
						if ( movable instanceof Parachute ) {
							Parachute parachute = (Parachute) movable;
							if ( parachute.isOwnerInside ( ) ) {
								parachute.destroy ( );
							}
						}
					}
					
					bp.setKnocked ( false );
					new MemberDeathEvent ( bp , DeathCause.VOID , null , player.getName ( ) + " fell into the void!" , 
							respawn_location , ItemStackUtil.getAllContents ( player.getInventory ( ) , false ) ).call ( );
				}
				
				event.setCancelled ( true );
			} else {
				boolean cancell = false;
				
				// checking is will cancell the event.
				if ( gamemode == GameMode.ADVENTURE || bp.isSpectator ( ) || bp.isDead ( ) ) {
					cancell = true;
				}
				
				// cancell event.
				if ( cancell ) {
					event.setDamage ( 0.0D );
					event.setCancelled ( true );
				}
			}
		}
	}
}