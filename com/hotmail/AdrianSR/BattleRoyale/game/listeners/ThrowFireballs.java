package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a Class that allow 
 * players to throw Fireballs.
 * 
 * @author AdrianSR.
 */
public final class ThrowFireballs implements Listener {
	
	/**
	 * Global class values.
	 */
	public static final String FIRE_BALLS_METADATA = "BATTLE_ROYALE_FIREBALL";
	
	/**
	 * Construct a Fireballs Thrower listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public ThrowFireballs ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler (priority = EventPriority.HIGHEST )
	public void onThrow ( PlayerInteractEvent event) {
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
		if (!BattleItems.FIRE_BALL.isThis(stack)) {
			return;
		}
		
		// check is not knocked.
		if (!bp.isKnocked()) {
			// throw Fireball.
			final Fireball fire = p.launchProjectile(Fireball.class, p.getEyeLocation().getDirection().multiply(1.1));
			// set metadata.
			fire.setMetadata(FIRE_BALLS_METADATA, new FixedMetadataValue(BattleRoyale.getInstance(), FIRE_BALLS_METADATA));
			// set incendary
			fire.setIsIncendiary(true);
			// set explosion strength.
			fire.setYield(2.0F);
			
			// consume item.
			if (p.getGameMode() != GameMode.CREATIVE) { // check is not in creative mode.
				// consume.
				if ((stack.getAmount() - 1) > 0) {
					p.setItemInHand(BattleItems.FIRE_BALL.asItemStack(stack.getAmount() - 1));
				} else {
					p.setItemInHand(null);
				}
				p.updateInventory();
			}
		}
		
		// cancel interaction.
		event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onExplode(final EntityExplodeEvent event) {
		// get entity.
		final Entity ent = event.getEntity();
		
		// check is a Battle Royale Fireball.
		if (!(ent instanceof Fireball) 
				|| !ent.hasMetadata(FIRE_BALLS_METADATA)) {
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
		
		// check is a Battle Royale Fireball.
		if (!(ent instanceof Fireball) || !ent.hasMetadata(FIRE_BALLS_METADATA)) {
			return;
		}
		
		// cancell block ignite.
		event.setCancelled(true);
	}
}