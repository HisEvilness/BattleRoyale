package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.math.LocationUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

import de.slikey.effectlib.util.ParticleEffect;

/**
 * Represents a class that 
 * allow players to place
 * slime launch pads.
 * 
 * @author AdrianSR.
 */
public final class PlaceLaunchPad implements Listener {
	
	/**
	 * Global class values.
	 */
	public static final  String LAUNCH_PAD_BLOCK_METADATA = "LAUNCH-PAD-BLOCK";
	private static final List<Location>       LAUNCH_PADS = new ArrayList<Location>();
	
	/**
	 * Construct a new Launch Pad place listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public PlaceLaunchPad ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
		
		// start particles task.
		SchedulerUtil.runTaskTimer ( new Runnable() {
			@Override
			public void run() {
				for (Location lp : new ArrayList<Location>(LAUNCH_PADS)) {
					// check loaction.
					if (lp == null) {
						continue;
					}
					
					// check is a slime block.
					if (lp.getBlock().getType() != Material.SLIME_BLOCK
							|| !lp.getBlock().hasMetadata(LAUNCH_PAD_BLOCK_METADATA)) {
						LAUNCH_PADS.remove(lp);
						continue;
					}
					
					// play particles.
					ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.0F, 0.3F, 0.1F, 8,
							lp.clone().add(0.5D, 0.9D, 0.5D),
							90000);
				}
			}
		} , 20 , 20 , plugin);
	}
	
	/**
	 * Detect if a player is placing a launch pad.
	 */
	@EventHandler (priority = EventPriority.HIGHEST , ignoreCancelled = true )
	public void onPlace(final BlockPlaceEvent eve) {
		final Player p       = eve.getPlayer();
		final BRPlayer bp    = BRPlayer.getBRPlayer(p);
		final ItemStack item = eve.getItemInHand();
		if (!BattleItems.LAUNCH_PAD.isThis(item)) {
			return;
		}
		
		// check is not knocked.
		if (bp.isKnocked()) {
			// cancell.
			eve.setCancelled(true);
			return;
		}
		
		// get placed block.
		final Block block = eve.getBlock();
		
		// set to slime.
		setLaunchPad(block);
		
		// move player to back.
		if (p.getLocation().distance(block.getLocation()) <= 1.0D) {
			final Location plloc = p.getLocation().clone();
			// get current direction.
			final BlockFace face = DirectionUtil.getBlockFace ( plloc.getYaw ( ) ).getOppositeFace();
			final float    yawTo = DirectionUtil.getYaw ( face );
			// change yaw and clear pitch.
			plloc.setYaw(yawTo);
			plloc.setPitch(0.0F);
			// move to back.
			p.setVelocity(plloc.getDirection().clone().multiply(1.1));
		}
		
		// place launch pads.
		for (Location lp : LocationUtil.getCuboid(block.getLocation(), 1, 1)) {
			// get lp block.
			Block lblock = lp.getBlock();
			
			// set to slime.
			setLaunchPad(lblock);
		}
		
		// place launch pads cruz.
		for (BlockFace adrian : DirectionUtil.FACES_90) {
			// get block.
			Block lblock = block.getRelative(adrian);
			
			// set to slime.
			setLaunchPad(lblock);
		}
	}
	
	/**
	 * Transform block to Slime Launpad,
	 * and restore 6 seconds later.
	 * 
	 * @param block the block to Transform.
	 */
	private void setLaunchPad(final Block block) {
		// check is not solid.
		if (block.getType().isSolid() && block.getType() != Material.SLIME_BLOCK) {
			return;
		}
		
		// change to block slime.
		block.setType(Material.SLIME_BLOCK);

		// set metadata.
		block.setMetadata(LAUNCH_PAD_BLOCK_METADATA,
				new FixedMetadataValue(BattleRoyale.getInstance(), LAUNCH_PAD_BLOCK_METADATA));
		
		// add to the list.
		LAUNCH_PADS.add(block.getLocation());

		// restore task.
		SchedulerUtil.runTaskLater ( new Runnable() {
			@Override
			public void run() {
				// restore.
				block.setType(Material.AIR);

				// rem metadata.
				block.removeMetadata(LAUNCH_PAD_BLOCK_METADATA, BattleRoyale.getInstance());
				
				// remove from list.
				LAUNCH_PADS.remove(block.getLocation());
			}
		}, (20 * 6), BattleRoyale.getInstance ( ) );
	}
	
	/**
	 * Check player is moving on a launch pad.
	 */
	@EventHandler
	public void onLauch(final PlayerMoveEvent eve) {
		// get player.
		final Player p    = eve.getPlayer();
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		
		// get and check block down to.
		final Block to    = eve.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (to.getType() != Material.SLIME_BALL
				&& !to.hasMetadata(LAUNCH_PAD_BLOCK_METADATA)
				&& !LAUNCH_PADS.contains(to.getLocation())) {
			return;
		}
		
		// add parachute.
		bp.setHasParachute(true);
		
		// throw player.
		p.setVelocity(p.getLocation().getDirection().multiply(0.0).setY(1).multiply(2.6));
	}
}
