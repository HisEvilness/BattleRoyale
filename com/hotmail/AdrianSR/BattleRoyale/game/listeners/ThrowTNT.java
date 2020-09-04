package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.events.MemberThrowTNT;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a Class that allow 
 * players to throw TNT grenades.
 * 
 * @author AdrianSR.
 */
public final class ThrowTNT implements Listener {
	
	/**
	 * Global class values.
	 */
	public static final String TNT_GRENADE_METADATA = "BATTLE_ROYALE_TNT_GRENADE";
	
	/**
	 * Construct a TNT Grenade Thrower listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public ThrowTNT ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onThrow ( PlayerInteractEvent event ) {
		// get and check action.
		final Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_AIR 
				&& action != Action.LEFT_CLICK_AIR) { // check is left or right click to air.
			return;
		}
		
		// get event values.
		final Player p        = event.getPlayer();
		final BRPlayer bp     = BRPlayer.getBRPlayer(p);
		final ItemStack stack = event.getItem();
		
		// check item.
		if (!BattleItems.TNT_GRENADE.isThis(stack)) {
			return;
		}
		
		/* check is not knocked */
		if (!bp.isKnocked()) {
			// spawn and modify tnt if the event is not cancelled
			final TNTPrimed tnt = (TNTPrimed) p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
			MemberThrowTNT evemt = new MemberThrowTNT(bp, tnt).call();
			if (evemt.isCancelled()) {
				tnt.remove();
				return;
			}
			
			/* apply modifiers */
			tnt.setVelocity(p.getLocation().getDirection().clone().multiply(1.1D));
			tnt.setFuseTicks(30); /* 80 by default */
			tnt.setMetadata(TNT_GRENADE_METADATA, new FixedMetadataValue(BattleRoyale.getInstance(), TNT_GRENADE_METADATA));
			tnt.setIsIncendiary(true);
			tnt.setYield(evemt.getExplosionStrength()); /* set explosion strength */
			
			/* consume item */
			if (evemt.isCosumeItemInHand()) {
				if (p.getGameMode() != GameMode.CREATIVE) { // check is not in creative mode.
					// consume.
					if ((stack.getAmount() - 1) > 0) {
						p.setItemInHand(BattleItems.TNT_GRENADE.asItemStack(stack.getAmount() - 1));
					} else {
						p.setItemInHand(null);
					}
					p.updateInventory();
				}
			}
		}
		
		/* cancel interaction */
		event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onExplode(final EntityExplodeEvent event) {
		// get entity.
		final Entity ent = event.getEntity();
		
		// check is a TNT Grenade.
		if (!(ent instanceof TNTPrimed) || !ent.hasMetadata(TNT_GRENADE_METADATA)) {
			return;
		}
		
		// cancell world block breaks.
		for (Block block : new ArrayList<Block>(event.blockList())) {
			// check is not a player block.
			if (!block.hasMetadata(WorldBlockBreaking.PLAYER_BLOCKS_METADATA)) {
				// remove from list..
				event.blockList().remove(block);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onGetFire(final BlockIgniteEvent event) {
		// get entity.
		final Entity ent = event.getIgnitingEntity();
		
		// check is a TNT Grenade.
		if (!(ent instanceof TNTPrimed) || !ent.hasMetadata(TNT_GRENADE_METADATA)) {
			return;
		}
		
		// cancell block ignite.
		event.setCancelled(true);
	}
	
	/**
	 * Cancell TNT place.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlaceTNT(final BlockPlaceEvent event) {
		if (BattleItems.TNT_GRENADE.isThis(event.getItemInHand())) {
			event.setCancelled(true);
		}
	}
}