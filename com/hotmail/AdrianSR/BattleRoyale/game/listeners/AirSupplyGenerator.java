package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.flowpowered.math.vector.Vector2i;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.events.BorderShrinkingChangeEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.time.border.BorderTimer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.airsupply.AirSupply;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.WorldUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.math.LocationUtil;

/**
 * Represents a class that generates
 * Air Supply boxes always that the border shrinking
 * succession change of point.
 * <p>
 * @author AdrianSR.
 */
public final class AirSupplyGenerator implements Listener {
	
	private static final int                 AMOUNT_EACH_RADIUS = 100;
	private static final Set < String > UNLEASHED_LOCATION_KEYS = new HashSet < > ( );
	
	/**
	 * Construct listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public AirSupplyGenerator ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
//	@EventHandler
//	public void onChat ( AsyncPlayerChatEvent event ) {
//		if ( !event.getMessage ( ).toLowerCase ( ).contains ( "airsupply" ) ) {
//			return;
//		}
//		
//		Location player_location = event.getPlayer ( ).getLocation ( );
//		Block                 to = player_location.getWorld ( ).getHighestBlockAt ( player_location );
//		if ( to == null ) {
//			event.getPlayer ( ).sendMessage ( ChatColor.RED + "Nei!" );
//			return;
//		}
//		
//		new AirSupply ( to.getLocation ( ) ).start ( );
//		event.getPlayer ( ).sendMessage ( ChatColor.RED + "Yupi!" );
//	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onManipulate ( PlayerArmorStandManipulateEvent event ) {
		if ( event.getRightClicked ( ).hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onInteract ( PlayerInteractAtEntityEvent event ) {
		if ( event.getRightClicked ( ).hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onUnleash ( EntityUnleashEvent event ) {
		if ( event.getEntity ( ).hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) {
			UNLEASHED_LOCATION_KEYS.add ( LocationUtil.format ( event.getEntity ( ).getLocation ( ).getBlock ( ).getLocation ( ) , 
					false , false , false ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void leadItemSpawn ( EntitySpawnEvent event ) {
		// this implementation could be a dirty implementation, but is the unique way to
		// detect when a lead item is spawned after an entity is unleased.
		
		// TODO: class/method names must be checked in any new spigot version.
		if ( !Bukkit.isPrimaryThread ( ) || event.getEntityType ( ) != EntityType.DROPPED_ITEM ) {
			return;
		}
		
		Item    dropped = (Item) event.getEntity ( );
		ItemStack stack = dropped.getItemStack ( );
		if ( stack != null && stack.getType ( ) != null ) {
			boolean     this_class = false;
			boolean unleash_method = false;
			
			StackTraceElement [ ] stacktrace = Thread.currentThread ( ).getStackTrace ( );
			for ( StackTraceElement element : stacktrace ) {
				if ( element.getClassName ( ).equals ( this.getClass ( ).getName ( ) ) ) {
					this_class = true;
				}
				
				if ( element.getClassName ( ).contains ( "EntityInsentient" ) ) {
					if ( element.getMethodName ( ).equals ( "unleash" ) ) {
						unleash_method = true;
					}
				}
			}
			
			if ( this_class && unleash_method ) {
				String location_key = LocationUtil.format ( event.getLocation ( ).getBlock ( ).getLocation ( ) , 
						false , false , false );
				if ( UNLEASHED_LOCATION_KEYS.contains ( location_key ) ) {
					event.setCancelled ( true );
					UNLEASHED_LOCATION_KEYS.remove ( location_key );
				}
			}
		}
	}

	@EventHandler
	public void onShrinkingChange ( BorderShrinkingChangeEvent event ) {
		if ( Config.AIR_SUPPLY_USE.getAsBoolean ( ) ) {
			BorderTimer border_timer = BorderTimer.getInstance ( );
			if ( border_timer != null && border_timer.getNextShrink ( ) != null ) {
				dropAirSupply ( border_timer.getNextShrink ( ) , MapsManager.BATTLE_MAP.getWorld ( ) );
			}
		}
	}
	
	private void dropAirSupply ( BorderShrink shrink , World world ) {
		List < Vector2i > dropped = new ArrayList < > ( );
		
		// amount calculation
		int amount = Config.AIR_SUPPLY_AMOUNT.getAsInteger ( );
		if ( amount == -1 ) {
			// auto-calculating
			amount = (int) ( shrink.getRadius ( ) / AMOUNT_EACH_RADIUS );

//			// FIXME
//			System.out.println  ( "shrink.getRadius ( ) = " + shrink.getRadius ( ) + " then amount = " + amount );
		}
		
		if ( amount == 0 ) {
			return;
		}
		
		long         begin = System.currentTimeMillis ( );
		Location       loc = shrink.getLocation ( ).withWorld ( world ); // make sure world is not null
		int         radius = (int) ( shrink.getRadius ( ) / 2 );
		int          count = 0;
		while ( count < amount ) {
			if ( ( System.currentTimeMillis ( ) - begin ) > 500 ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , 
						"Something went wrong when calculating air supply locations!" , BattleRoyale.getInstance ( ) );
				break;
			}
			
			int x = Location.locToBlock ( loc.getX ( ) + ( Math.random ( ) < 0.5D 
					? ( radius * Math.random ( ) ) : -( radius * Math.random ( ) ) ) );
			int z = Location.locToBlock ( loc.getZ ( ) + ( Math.random ( ) < 0.5D 
					? ( radius * Math.random ( ) ) : -( radius * Math.random ( ) ) ) );
			
			Block highest = WorldUtil.getHighestSolidBlockAt ( world , x , z );
			if ( highest == null ) {
				continue;
			}
			
			AirSupply airsupply = new AirSupply ( world , x , z );
			if ( !airsupply.isValidPlace ( ) ) {
				continue;
			}
			
			// checks: distance between this and other air supplies
			boolean valid = true;
			for ( Vector2i other_drop : dropped ) {
				if ( other_drop.distance ( x , z ) < ( radius / 2 ) ) {
					// this avoids this air supply to be dropped close to another
					valid = false; break;
				}
			}
			
			if ( valid ) {
				airsupply.start ( );
				dropped.add ( new Vector2i ( x , z ) );
				count ++;
				
//				// FIXME prints the location of the dropped air supply.
//				System.out.println ( "/tp " + (int) x + " " + " 100 " + (int) z );
			}
		}
	}
}