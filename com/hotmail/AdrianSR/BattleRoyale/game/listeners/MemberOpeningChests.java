package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootContainer;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.adriansr.core.util.math.DirectionUtil;

/**
 * Represents a class that 
 * allow plugin to auto loot
 * chests without setting up
 * the loot chests with the battle
 * map editor.
 * <p>
 * @author AdrianSR.
 */
public final class MemberOpeningChests implements Listener {
	
	/**
	 * Auto loot emtpy chests.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberOpeningChests ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler ( priority = EventPriority.LOWEST )
	public void onOpen ( final PlayerInteractEvent event ) {
		final BattleMap     map = MapsManager.BATTLE_MAP;
		final Block chest_block = event.getClickedBlock();
		if (chest_block == null || !(chest_block.getState() instanceof Chest)) {
			return;
		}
		
		BRPlayer player = BRPlayer.getBRPlayer ( event.getPlayer ( ) );
		if ( player.isSpectator ( ) || player.isDead ( ) || player.isBeingReanimated ( )
				|| player.isKnocked ( ) ) {
			event.setCancelled ( true );
			event.setUseInteractedBlock ( Result.DENY );
			return;
		}
		
		// we're going to check if a loot chest if clicked
		if (GameManager.isNotRunning()) {
			return;
		}
		
		if (!Config.MAP_AUTO_LOOT_CHESTS.getAsBoolean()) {
			return;
		}
		
		if (map.getConfig().isRandomChest(chest_block.getLocation())) {
			return;
		}
		
		if ( chest_block.hasMetadata ( GameUtils.DEATH_LOOT_CHEST_METADATA_KEY ) ) {
			return;
		}
		
		/* disapper chest */
		final Chest chest = (Chest) chest_block.getState();
		if (chest.getInventory() instanceof DoubleChestInventory) {
			Arrays.stream(DirectionUtil.FACES_90).forEach(face -> {
				Block at = chest_block.getRelative(face);
				if (at.getState() instanceof Chest) {
					at.setType(Material.AIR);
					at.getState().update();
				}
			});
		}
		
		chest_block.setType(Material.AIR);
		chest_block.getState().update();
		
		/* drop loot */
		int loot_array_length = 0;
		for (LootItem li : LootContainer.GAME.getLoadedLoot()) {
			loot_array_length += li.getProbabilityPercent();
		}
		
		int setted_slots = 0;
		final LootItem[] loot_items = new LootItem[loot_array_length];
		for (LootItem li : LootContainer.GAME.getLoadedLoot()) {
			for (int x = 0; x < li.getProbabilityPercent(); x++) {
				loot_items[setted_slots] = li;
				setted_slots ++;
			}
		}
		
		final int          min_lq = Math.min(LootContainer.GAME.getLoadedLoot().size(), 3);
		final int          max_lq = Math.min(LootContainer.GAME.getLoadedLoot().size(), RandomUtils.nextInt(6));
		final int   loot_quantity = Math.max(max_lq, min_lq);
		final List<LootItem> loot = new ArrayList<LootItem>();
		int                 count = 0;
		while(count < loot_quantity) {
			LootItem random = loot_items[RandomUtils.nextInt(loot_items.length)];
			if (loot.contains(random)) {
				continue;
			}
			
			loot.add(random);
			count ++;
		}
		
		for (LootItem li : new ArrayList<LootItem>(loot)) {
			for (LootItem parent : li.getParents()) {
				loot.add(parent);
			}
		}
		
		for (int x = 0; x < loot.size(); x++) {
			chest_block.getWorld().dropItem(chest_block.getLocation(), loot.get(x).getItemStack());
		}
	}
}