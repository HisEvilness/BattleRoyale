package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.airsupply;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootContainer;
import com.hotmail.AdrianSR.BattleRoyale.util.WorldUtil;
import com.hotmail.AdrianSR.BattleRoyale.util.entity.UUIDArmorStand;
import com.hotmail.AdrianSR.BattleRoyale.util.entity.UUIDChicken;
import com.hotmail.adriansr.core.menu.size.ItemMenuSize;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.reflection.bukkit.EntityReflection;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

import de.slikey.effectlib.util.ParticleEffect;

@SuppressWarnings("deprecation")
public class AirSupply implements Runnable {
	
	public static final String          AIR_SUPPLY_META_KEY = "air-supply-object";
	public static final String AIR_SUPPLY_INSTANCE_META_KEY = "air-supply-instance";
	
	protected static final int PARACHUTE_PARTS = 4;
	protected static final int  RESTORE_BLOCKS = 9 + 1 + 1; // 9 iron blocks + 1 beacon + 1 chest.
	protected static final double   FALL_SPEED = 0.20;
	
	// CONFIGURABLE DE LOS AIR SUPPLIES: Cuantos por shrinking point, loot, color de la signal
	
	protected final Block   to;
	protected boolean finished;
	
	protected BukkitTask executer;
	
	protected UUIDArmorStand chest_holder;
	protected UUIDChicken [ ]   parachute;
	protected Block [ ]             place;

	public AirSupply ( World world , int x , int z ) {
		to = WorldUtil.getHighestSolidBlockAt ( world , x , z );
		if ( to == null ) {
			throw new IllegalStateException ( "couldn't found a solid block at the specified location!" );
		}
	}
	
	public AirSupply ( Location location ) { 
		this ( location.getWorld ( ) , Location.locToBlock ( location.getX ( ) ) , 
				Location.locToBlock ( location.getZ ( ) ) );
	}
	
	/**
	 * Gets whether this air supply has landed.
	 * <p>
	 * @return whether this air supply is finished.
	 */
	public boolean isFinished ( ) {
		return finished;
	}
	
	/**
	 * 
	 */
	public boolean start ( ) {
		if ( !Bukkit.isPrimaryThread ( ) ) {
			throw new IllegalStateException ( "must run on server primary thread!" );
		}
		
		if ( isValidPlace ( ) ) {
			if ( executer == null ) {
				if ( spawn ( ) ) {
					executer = SchedulerUtil.runTaskTimer ( this , 1 , 1 , BattleRoyale.getInstance ( ) );
					return true;
				}
			} else {
				throw new IllegalStateException ( "airsupply already started!" );
			}
		} else {
			throw new IllegalStateException ( "this airsupply has not a valid place!" );
		}
		return false;
	}
	
	public void stop ( ) {
		if ( executer != null ) {
			executer.cancel ( );
			
			if ( Bukkit.isPrimaryThread ( ) ) {
				despawn ( );
			} else {
				// running on server primary thread.
				Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> despawn ( ) );
			}
		} else {
			throw new IllegalStateException ( "airsupply never started!" );
		}
	}
	
	/**
	 * Causes this air supply to explode and drop the contents of this supply.
	 */
	public void open ( ) {
		/* restoring... */
		for ( int i = 0 ; i < place.length ; i ++ ) {
			Block block = place [ i ];
			if ( block.getType ( ) == Material.CHEST ) {
				// dropping contents
				Inventory inventory = ((Chest) block.getState ( )).getBlockInventory ( );
				for ( ItemStack item : inventory.getContents ( ) ) {
					if ( item == null ) { continue; }
					// PoP, drop it!
					block.getWorld ( ).dropItem ( block.getLocation ( ).add ( 0.5 , 0.5 , 0.5 ) , item );
				}
				// we are clearing the inventory to avoid items to stay within the chest,
				// because of if the previous material has an inventory, then the items
				// will stay, making it too OP.
				inventory.clear ( );
			}
			
			// restoring to previous type
			block.setType ((Material) ((FixedMetadataValue) block.getMetadata ( AIR_SUPPLY_META_KEY ).get ( 0 )).value ( ));
			// removing metadatas
			block.removeMetadata ( AIR_SUPPLY_META_KEY , BattleRoyale.getInstance ( ) );
			block.removeMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , BattleRoyale.getInstance ( ) );
		}
		
		/* explode effect */
		ParticleEffect.LAVA.display ( 2F , 2F , 2F , 0.2F , 150 , 
				to.getLocation ( ).add ( 0.5D , 1.0D , 0.5D ) , 1000 );
	}
	
	/**
	 * Gets whether this air supply has a valid place.
	 * <p>
	 * @return whether this air supply has a valid place.
	 */
	public boolean isValidPlace ( ) {
		return placeCheck ( );
	}
	
	protected boolean spawn ( ) {
		// beacon placing
		if ( !placeBeacon ( ) ) {
			return false;
		}
		
		// chest holder spawning.
		if ( !spawnChestHolder ( ) ) {
			return false;
		}
		
		// parachute spawning.
		Location spawn_location = calculateSpawnLocation ( );
		if ( spawn_location.getWorld ( ).isChunkInUse ( Location.locToBlock ( spawn_location.getX ( ) ) , 
				Location.locToBlock ( spawn_location.getZ ( ) ) ) ) {
			spawnParachute ( );
		}
		return true;
	}
	
	protected boolean placeBeacon ( ) {
		// place checking
		if ( !placeCheck ( ) ) {
			return false;
		}
		
		// then placing...
		for ( int i = 0 ; i < place.length - 1 ; i ++ ) {
			if ( i < place.length - 2 ) { // iron blocks
				Block iron_block = place [ i ];
				iron_block.setMetadata ( AIR_SUPPLY_META_KEY , 
						new FixedMetadataValue ( BattleRoyale.getInstance ( ) , iron_block.getType ( ) ) );
				iron_block.setType ( Material.IRON_BLOCK );
			} else { // beacon block
				to.setMetadata ( AIR_SUPPLY_META_KEY , 
						new FixedMetadataValue ( BattleRoyale.getInstance ( ) , to.getType ( ) ) );
				to.setType ( Material.BEACON );
				place [ i ] = to;
			}
		}
		return true;
	}
	
	protected boolean placeCheck ( ) {
		this.place = new Block [ RESTORE_BLOCKS ];
		
		// iron blocks check
		Block [ ] iron_blocks = new Block [ 9 ];
		for ( int i = 0 ; i < iron_blocks.length ; i ++ ) {
			Block block = null;
			switch ( i ) {
			case 0:
				block = to.getRelative ( BlockFace.DOWN );
				break;
			case 1:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.EAST );
				break;
			case 2:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.WEST );
				break;
			case 3:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.NORTH );
				break;
			case 4:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.SOUTH );
				break;
			case 5:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.EAST ).getRelative ( BlockFace.NORTH );
				break;
			case 6:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.EAST ).getRelative ( BlockFace.SOUTH );
				break;
			case 7:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.WEST ).getRelative ( BlockFace.SOUTH );
				break;
			case 8:
				block = to.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.WEST ).getRelative ( BlockFace.NORTH );
				break;
			}
			
			// block check
			if ( block.getLightFromSky ( ) != (byte) 0 
					|| !block.getRelative ( BlockFace.UP ).getType ( ).isOccluding ( ) ) {
				return false;
			}
			
			place [ i ] = block;
		}
		return true;
	}
	
	/**
	 * Request the chest holder to be spawned.
	 * <p>
	 * @return false if the chest holder has already been spawned.
	 */
	protected boolean spawnChestHolder ( ) {
		if ( chest_holder == null ) {
			final Location spawn_location = calculateSpawnLocation ( );
			
			ArmorStand chest_holder = to.getWorld ( ).spawn ( spawn_location , ArmorStand.class );
			chest_holder.setGravity ( false );
			chest_holder.setVisible ( false );
			chest_holder.setHelmet ( new ItemStack ( Material.CHEST ) );
			chest_holder.setInvulnerable ( true );
			this.chest_holder = new UUIDArmorStand ( chest_holder );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Request the parachute to be spawned.
	 * <p>
	 * @return false if the parachute has already been spawned, the chest holder has
	 *         never been spawned, or if the chest holder has die.
	 */
	protected boolean spawnParachute ( ) {
		ArmorStand chest_holder = this.chest_holder.get ( );
		if ( chest_holder != null && parachute == null ) {
			// we take the chest holder eye location at the spawn location because of the
			// parachute might be spawned too late due to chunk usage.
			final Location spawn_location = chest_holder.getEyeLocation ( );
			
			this.parachute = new UUIDChicken [ PARACHUTE_PARTS ];
			for ( int i = 0 ; i < parachute.length ; i ++ ) {
				BlockFace face = i < DirectionUtil.FACES_90.length ? DirectionUtil.FACES_90 [ i ] 
						: DirectionUtil.FACES_90 [ i % DirectionUtil.FACES_90.length ].getOppositeFace ( ) ;
				Chicken part = to.getWorld ( ).spawn ( spawn_location.clone ( )
						.add ( face.getModX ( ) * 1.9 , 0D , face.getModZ ( ) * 1.9 ) , Chicken.class );
				
				part.setLeashHolder ( chest_holder );
				part.setInvulnerable ( true );
//				part.setAI ( false );
				part.setMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , 
						new FixedMetadataValue ( BattleRoyale.getInstance ( ) , AirSupply.this ) );
				
				this.parachute [ i ] = new UUIDChicken ( part );
			}
			return true;
		} else {
			return false;
		}
	}
	
	protected Location calculateSpawnLocation ( ) {
		Location spawn = to.getLocation ( );
		spawn.setX ( spawn.getX ( ) + 0.5D ); // centering
		spawn.setY ( spawn.getWorld ( ).getMaxHeight ( ) - 1 );
		spawn.setZ ( spawn.getZ ( ) + 0.5D ); // centering
		return spawn;
	}
	
	protected void placeLootChest ( ) {
		Block chest_block = to.getRelative ( BlockFace.UP );
		chest_block.setType ( Material.CHEST );
		chest_block.getState ( ).update ( true , true );
		chest_block.setMetadata ( AIR_SUPPLY_META_KEY , 
				new FixedMetadataValue ( BattleRoyale.getInstance ( ) , Material.AIR ) );
		chest_block.setMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , 
				new FixedMetadataValue ( BattleRoyale.getInstance ( ) , AirSupply.this ) );
		place [ place.length - 1 ] = chest_block;
		
		/* random loot */
		Inventory inventory = ((Chest) chest_block.getState ( )).getBlockInventory ( );
		int loot_array_length = 0; /* get loot items array length */
		for (LootItem li : LootContainer.AIR_SUPPLY.getLoadedLoot()) {
			loot_array_length += li.getProbabilityPercent();
		}
		
		int setted_slots = 0; /* get loot array */
		final LootItem[] loot_items = new LootItem[loot_array_length];
		for (LootItem li : LootContainer.AIR_SUPPLY.getLoadedLoot()) {
			for (int x = 0; x < li.getProbabilityPercent(); x++) {
				loot_items[setted_slots] = li;
				setted_slots ++;
			}
		}
		
		final int        min_lq = Math.min(LootContainer.AIR_SUPPLY.getLoadedLoot().size(), 3);
		final int        max_lq = Math.min(LootContainer.AIR_SUPPLY.getLoadedLoot().size(), RandomUtils.nextInt(6));
		final int loot_quantity = Math.max(max_lq, min_lq);
		List<LootItem> loot = new ArrayList<LootItem>();
		int           count = 0;
		while(count < loot_quantity) {
			LootItem random = loot_items[RandomUtils.nextInt(loot_items.length)];
			if (!loot.contains(random)) {
				loot.add(random); random.getParents().forEach(prt -> loot.add(prt));
				count ++;
			}
		}
		
		count = 0;
		while(count < loot.size()) {
			int random = RandomUtils.nextInt(ItemMenuSize.THREE_LINE.getSize());
			if (inventory.getItem(random) == null) {
				loot.get(count).set(inventory, random);
				count ++;
			}
		}
	}
	
	protected void despawn ( ) {
		ArmorStand chest_holder = this.chest_holder.get ( );
		if ( chest_holder != null ) {
			chest_holder.remove ( );
		}
		
		if ( parachute != null ) {
			for ( int i = 0 ; i < parachute.length ; i ++ ) {
				Chicken part = parachute [ i ].get ( );
				if ( part != null ) {
					part.setInvulnerable ( false );
					part.setAI ( true );
				}
			}
		}
	}
	
	@Override
	public void run ( ) {
		if ( !Bukkit.isPrimaryThread ( ) ) {
			throw new IllegalStateException ( "must run on server primary thread!" );
		}
		
		final ArmorStand chest_holder = this.chest_holder.get ( );
		if ( chest_holder == null ) {
			stop ( ); return; // the chest holder has die unexpectedly.
		}
		
		// we are using the eye location to check if has landed because of it represents
		// the location of the head of the armor stand, which holds the helmet, that is
		// the visible part of the armor stand for players.
		if ( chest_holder.getEyeLocation ( ).getBlock ( ).getType ( ).isSolid ( ) ) {
			// removing leashes before despawning to avoid leash items to be dropped.
			if ( parachute != null ) {
				for ( int i = 0 ; i < parachute.length ; i ++ ) {
					Chicken part = parachute [ i ].get ( );
					if ( part != null ) {
						part.setLeashHolder ( null );
					}
				}
			}
			
			// stop and place
			stop ( );
			placeLootChest ( );
			
			// land sound
			to.getWorld ( ).playSound ( to.getLocation ( ) , Sound.BLOCK_CLOTH_FALL , 4F , 1F );
			to.getWorld ( ).playSound ( to.getLocation ( ) , Sound.BLOCK_LADDER_PLACE , 4F , 1F );
			
			// land particles
			ParticleEffect.CLOUD.display ( 1F , 1F , 1F , 0.2F , 150 , 
					to.getLocation ( ).add ( 0.5D , 1.0D , 0.5D ) , 1000 );
			return; // it has landed!
		}
		
		// we are spawning the parachute if it has not been already spawned due to chunk usage.
		if ( parachute == null ) {
			Chunk chunk = chest_holder.getLocation ( ).getChunk ( );
			if ( chunk.getWorld ( ).isChunkInUse ( chunk.getX ( ) , chunk.getZ ( ) ) ) {
				spawnParachute ( );
				
//				// FIXME:
//				System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>> parachute spawned later!" );
			}
		} 
		// ensuring parachute are leashed.
		else {
			for ( int i = 0 ; i < parachute.length ; i ++ ) {
				Chicken part = parachute [ i ].get ( );
				if ( part != null && !part.isLeashed ( ) ) {
					part.setLeashHolder ( chest_holder );
				}
			}
		}
		
		// falling
		EntityReflection.setLocation ( chest_holder , 
				chest_holder.getLocation ( ).subtract ( 0D , FALL_SPEED , 0D ) );
	}
}