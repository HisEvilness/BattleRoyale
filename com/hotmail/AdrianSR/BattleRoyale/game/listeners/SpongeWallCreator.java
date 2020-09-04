package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;

/**
 * Represents a class that allow
 * the players to create sponge
 * walls with the 'SpongeWall' item.
 * 
 * @author AdrianSR.
 */
public final class SpongeWallCreator implements Listener {
	
	/**
	 * Global class values.
	 */
	public static final String SPONG_WALL_BLOCK_METADATA = "SPONG-WALL-BLOCK";
	
	/**
	 * Construct a Sponge Wall creator Listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public SpongeWallCreator ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onThrowOrDrop(final PlayerInteractEvent eve) {
		// get event values.
		final Player p       = eve.getPlayer();
		final BRPlayer bp    = BRPlayer.getBRPlayer(p);
		final Action action  = eve.getAction();
		final ItemStack item = eve.getItem();
		
		// check is using the sponge walls item.
		if (!BattleItems.SPONGE_WALLS.isThis(item)) {
			return;
		}
		
		// check is not knocked.
		if (bp.isKnocked()) {
			eve.setCancelled(true);
			return;
		}
		
		// check no valid action.
		if (action == Action.PHYSICAL 
				|| action == Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		// consume item.
		if (p.getGameMode() != GameMode.CREATIVE) { // check is not in creative mode.
			// consume.
			if ((item.getAmount() - 1) > 0) {
				p.setItemInHand(BattleItems.FIRE_BALL.asItemStack(item.getAmount() - 1));
			} else {
				p.setItemInHand(null);
			}
			p.updateInventory();
		}
		
		// get player direction.
//		final BlockFace direction = DirectionUtils.getFacingDirection(p.getLocation().getYaw());
		BlockFace direction = DirectionUtil.getBlockFace ( p.getLocation ( ).getYaw ( ) );
		
		// spawn wall spawner falling block.
		final FallingBlock spawner = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.SPONGE, (byte) 0);
		// set modifiers.
		spawner.setMetadata(SPONG_WALL_BLOCK_METADATA, new FixedMetadataValue(BattleRoyale.getInstance(), direction.name()));
		spawner.setDropItem(false);
		spawner.setHurtEntities(false);
		
		// velocity.
		Vector dir = null;
		
		// check is throwing or droping. (Getting velocity).
		if (action.name().startsWith("LEFT_CLICK_")) { // when is throwing.
			dir = p.getEyeLocation().getDirection().clone().multiply(1.2D);
		} else if (action == Action.RIGHT_CLICK_AIR) { // when is droping.
			dir = p.getEyeLocation().getDirection().clone().multiply(0.3D);
		}
		
		// set velocity.
		spawner.setVelocity(dir);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onTouchGround(final EntityChangeBlockEvent eve) {
		// get event values.
		final Entity ent      = eve.getEntity();
		final Block block     = eve.getBlock();
		final Material change = eve.getTo();
		
		// check entity.
		if (!(ent instanceof FallingBlock)) {
			return;
		}
		
		// check change.
		if (change != Material.SPONGE) {
			return;
		}
		
		// get falling block and check is a wall block.
		final FallingBlock spawner = (FallingBlock) ent;
		if (!spawner.hasMetadata(SPONG_WALL_BLOCK_METADATA)) {
			return;
		}
		
		// set as player block.
		block.setMetadata(WorldBlockBreaking.PLAYER_BLOCKS_METADATA,
				new FixedMetadataValue(BattleRoyale.getInstance(), WorldBlockBreaking.PLAYER_BLOCKS_METADATA));
		
		// decompile metadata.
		final String dirName = spawner.getMetadata(SPONG_WALL_BLOCK_METADATA).get(0).asString();
		// get and check direction.
		final BlockFace facing = EnumReflection.getEnumConstant(BlockFace.class, dirName);
		if (facing == null) {       // check is not null.
			eve.setCancelled(true); // cancell if is null.
			return;
		}
		
		// get left and rigth face.
		final BlockFace left  = DirectionUtil.getLeftFace(facing);
		final BlockFace right = left.getOppositeFace();
		
		// SPAWN WALL.
		for (int y = 0; y < 3; y++) {
			// up wall main block.
			block.getRelative(BlockFace.UP, Math.max(y, 1)).setType(Material.SPONGE);
			
			// set as player block.
			block.setMetadata(WorldBlockBreaking.PLAYER_BLOCKS_METADATA,
					new FixedMetadataValue(BattleRoyale.getInstance(), WorldBlockBreaking.PLAYER_BLOCKS_METADATA));
			
			// set is sponge wall block
			block.setMetadata ( SPONG_WALL_BLOCK_METADATA,
					new FixedMetadataValue ( BattleRoyale.getInstance ( ) , SPONG_WALL_BLOCK_METADATA ) );
			
			// blocks at left and right.
			for (int x = 1; x <= 4; x++) {
				// get face to use and num of blocks.
				BlockFace face  = (x <= 2) ? (left) : (right);
				int numOfBlocks = (x <= 2) ? ( x )  : (x - 2);
				
				// get block to transform, and check is not solid.
				Block wall = block.getRelative(facing).getRelative(face, numOfBlocks).getLocation().add(0.0D, y, 0.0D).getBlock();
				if (wall.getType().isSolid()) {
					continue;
				}
				
				// set to sponge wall.
				wall.setType(Material.SPONGE);
				
				// set as player block.
				wall.setMetadata(WorldBlockBreaking.PLAYER_BLOCKS_METADATA,
						new FixedMetadataValue(BattleRoyale.getInstance(), WorldBlockBreaking.PLAYER_BLOCKS_METADATA));
				
				// set is sponge wall block
				wall.setMetadata ( SPONG_WALL_BLOCK_METADATA,
						new FixedMetadataValue ( BattleRoyale.getInstance ( ) , SPONG_WALL_BLOCK_METADATA ) );
			}
		}
	}

	/**
	 * Cancell place Sponge Wall as block.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlaceAsBlock(final BlockPlaceEvent event) {
		// get event values.
		final Block block    = event.getBlock();
		final ItemStack item = event.getItemInHand();
		
		// check is placing the Sponge Walls creator item as Block.
		if (!BattleItems.SPONGE_WALLS.isThis(item) 
				|| block.getType() != Material.SPONGE) {
			return;
		}
		
		// cancell.
		event.setCancelled(true);
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onBreak ( BlockBreakEvent event ) {
		if ( event.getBlock ( ).hasMetadata ( SPONG_WALL_BLOCK_METADATA ) ) {
			// we're stopping sponge wall block items to be dropped when breaking.
			event.getBlock ( ).setType ( Material.AIR );
			event.setCancelled ( true );
		}
	}
}