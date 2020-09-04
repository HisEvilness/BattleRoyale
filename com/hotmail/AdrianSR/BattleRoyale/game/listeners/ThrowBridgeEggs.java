package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.game.item.ConfigItem;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.EventUtil;
import com.hotmail.adriansr.core.util.entity.EntityUtil;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.sound.UniversalSound;

/**
 * Represents a Class that allow players 
 * to throw Bridge Egg.
 * <p>
 * @author AdrianSR.
 */
public final class ThrowBridgeEggs implements Listener {
	
	/**
	 * Construct a Bridge Eggs Thrower listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public ThrowBridgeEggs ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGH )
	public void onThrow ( PlayerEggThrowEvent event ) {
		event.setHatching ( false );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onLaunch ( PlayerInteractEvent event ) {
		Player     player = event.getPlayer ( );
		ItemStack clicked = event.getItem ( );
		if ( !EventUtil.isRightClick ( event.getAction ( ) ) 
				|| !BattleItems.BRIDGE_EGG.isThis ( clicked ) ) {
			return;
		}
		
		BRPlayer br = BRPlayer.getBRPlayer ( player );
		if ( !br.isKnocked ( ) ) {
			// okay, it seems everything is right, then throw it!
			onThrow ( player.launchProjectile ( Egg.class , 
					player.getEyeLocation ( ).getDirection ( ).multiply ( 2 ) ) , player );
			
			// item consuming
			if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
				PlayerInventory inventory = event.getPlayer ( ).getInventory ( );
				if ( inventory.getItemInMainHand ( ).getAmount ( ) <= 1 ) {
					inventory.setItemInMainHand ( new ItemStack ( Material.AIR ) );
				} else {
					clicked.setAmount ( clicked.getAmount ( ) - 1 );
					inventory.setItemInMainHand ( clicked );
				}
			}
		}
		
		event.setCancelled ( true );
		event.setUseItemInHand ( Result.DENY );
	}
	
	private void onThrow(Egg egg, Player player) {// start BridgeEggTask.
		SchedulerUtil.runTaskLater ( new Runnable() {
			@Override public void run ( ) {
				new BridgeEggTask ( egg , BattleRoyale.getInstance ( ) );
			}
		}, 1, BattleRoyale.getInstance ( ) );
	}
	
	/**
	 * Represents a class that check the
	 * Bridge Egg postion and set it path
	 * to SANDSTONE blocks.
	 * 
	 * @author AdrianSR
	 */
	private static class BridgeEggTask implements Runnable {
		
		/**
		 * Class values.
		 */
		private final UUID  a;
		private final World b;
		private final int   c;
		private       int   d;
		
		/**
		 * Construct a new Bridge Egg task.
		 * 
		 * @param egg the Bridge Egg entity.
		 */
		BridgeEggTask(final Egg egg, final BattleRoyale plugin) {
			a = egg.getUniqueId();
			b = egg.getWorld();
			c = SchedulerUtil.runTaskTimer ( this , 0 , 0 , plugin ).getTaskId ( );
		}

		@Override
		public void run() {
			// get and check Egg.
//			final Egg egg = ReflectionUtils.getEntityByClass(Egg.class, b, a);
			final Egg egg = EntityUtil.getEntity ( b , Egg.class , a );
			if (egg == null || egg.isDead()) {
				 // stop repeating task.
				stop();
				return;
			}
			
			/* check path length */
			final ConfigItem<?> path_config = BattleItems.BRIDGE_EGG.getConfiguration().getFromKey("max-path-blocks");
			if (path_config != null
					&& path_config.getSafeValue(BattleItems.BRIDGE_EGG.getConfigurationSection()) instanceof Integer) {
				if (d >= (Integer) path_config.getSafeValue(BattleItems.BRIDGE_EGG.getConfigurationSection())) {
					stop();
					return;
				}
			}
			
			// get BlockFace direction.
			final BlockFace dir   = DirectionUtil.getBlockFace(egg.getLocation().getYaw());
			final BlockFace back  = dir.getOppositeFace();
			final BlockFace left  = DirectionUtil.getLeftFace(back);
//			final BlockFace right = left.getOppositeFace();
			
			// get path.
			final Block[] path = new Block[] {
				egg.getLocation().getBlock().getRelative(BlockFace.DOWN, 2),
				egg.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getRelative(back).getRelative(left),
				egg.getLocation().getBlock().getRelative(BlockFace.DOWN, 2).getRelative(left),
			};
			
			SchedulerUtil.runTaskLater ( new Runnable ( ) {
				@Override
				public void run() {
					// set path to SANDSTONE
					for (Block block : path) {
						// check the path is Air.
						if (!block.getType().isSolid()) {
							// set to SANDSTONE.
							block.setType(Material.SANDSTONE);
							block.setData((byte) 2);
							
							// play sound. // CHICKEN_EGG_POP
							block.getWorld().playSound(block.getLocation(), UniversalSound.CHICKEN_EGG_POP.asBukkit(), 1.5F, 1.0F);
							
							// set as player block.
							block.setMetadata(WorldBlockBreaking.PLAYER_BLOCKS_METADATA,
									new FixedMetadataValue(BattleRoyale.getInstance(), WorldBlockBreaking.PLAYER_BLOCKS_METADATA));
						}
					}
					
					/* count path length */
					d ++;
				}
			}, 2 , BattleRoyale.getInstance ( ) );
		}
		
		/**
		 * Stop this.
		 */
		private void stop() {
			Bukkit.getScheduler().cancelTask(c);
		}
	}
}