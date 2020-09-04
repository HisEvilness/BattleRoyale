package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that 
 * disallow players to break
 * no player blocks.
 * <p>
 * @author AdrianSR.
 */
public final class WorldBlockBreaking implements Listener {

	/**
	 * The player blocks
	 * metadata.
	 */
	public static final String PLAYER_BLOCKS_METADATA = "PLAYER-BLOCK-METADATA";
	
	/**
	 * Construct a anti World Block Break.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public WorldBlockBreaking ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	/**
	 * Set blocks as player block.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlace(final BlockPlaceEvent event) {
		// get block.
		final Block block = event.getBlock();
		
		// set metadata.
		block.setMetadata(PLAYER_BLOCKS_METADATA, 
				new FixedMetadataValue(BattleRoyale.getInstance(), PLAYER_BLOCKS_METADATA));
	}

	/**
	 * Avoid players to break world blocks.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBreak(final BlockBreakEvent event) {
		// check player gamemode.
		if (event.getPlayer().getGameMode() 
				== GameMode.CREATIVE) {
			return;
		}
		
		// get and check block.
		final Block block = event.getBlock();
		if (block.hasMetadata(PLAYER_BLOCKS_METADATA)) {
			return;
		}
		
		// cancell.
		event.setCancelled(true);
	}
}
