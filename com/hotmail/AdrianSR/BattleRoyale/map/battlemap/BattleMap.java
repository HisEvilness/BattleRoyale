package com.hotmail.AdrianSR.BattleRoyale.map.battlemap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.map.Map;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootContainer;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.MiniMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsWorldManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsYamlManager;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicle;
import com.hotmail.adriansr.core.menu.size.ItemMenuSize;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.server.Version;

/**
 * Represents the Battle Royale Game Map.
 * <p>
 * @author AdrianSR
 */
public final class BattleMap implements Map {
	
	/**
	 * Class values.
	 */
	private final File                     folder;
	private final String            	     name;
	private       World                     world;
	private       BRMapsYamlManager        config;
	private final BRMapsWorldManager worldManager;
	private boolean                 isBuildLoaded;
	private Location           currentBorderCenter = null;
	private double             currentBorderRadius = -1;
	private Location              nextBorderCenter = null;
	private double                nextBorderRadius = -1;
	
	/**
	 * Construct a new Battle Royale map.
	 * <p>
	 * @param folder the world folder.
	 * @param name the map name.
	 */
	public BattleMap ( File folder ) {
		this.folder       = folder;
		this.config       = new BRMapsYamlManager ( this );
		this.worldManager = new BRMapsWorldManager ( this );
		this.name         = config.getMapName ( );
	}
	
	/**
	 * Gets the map game-play area.
	 * <p>
	 * @return map game-play area.
	 */
	@Nullable
	public Area getArea ( ) {
		return config.getArea ( );
	}
	
	/**
	 * Sets the map game-play area.
	 * <p>
	 * @param area the map game-play area.
	 */
	@Nonnull
	public void setArea ( Area area ) {
		config.setArea ( area );
	}
	
	/**
	 * Gets the minimap for the map.
	 * <p>
	 * @return the minimap.
	 */
	public MiniMap getMiniMap ( ) {
		return config.getMiniMap ( );
	}
	
	/**
	 * Sets the minimap for the map.
	 * <p>
	 * @param minimap the minimap.
	 */
	public void setMiniMap ( @Nullable MiniMap minimap ) {
		config.setMiniMap ( minimap );
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Get the map folder.
	 * 
	 * @return the map foler.
	 */
	public File getFolder() {
		return folder;
	}
	
	/**
	 * Get the map configuration.
	 * 
	 * @return the config.
	 */
	public BRMapsYamlManager getConfig() {
		return config;
	}
	
	/**
	 * @return the isBuildLoaded
	 */
	public boolean isBuildLoaded() {
		return isBuildLoaded;
	}

	/**
	 * @param isBuildLoaded the isBuildLoaded to set
	 */
	public void setBuildLoaded(boolean isBuildLoaded) {
		this.isBuildLoaded = isBuildLoaded;
	}

	/**
	 * @return the currentBorderCenter
	 */
	public Location getCurrentBorderCenter() {
		return currentBorderCenter;
	}

	/**
	 * @param currentBorderCenter the currentBorderCenter to set
	 */
	public void setCurrentBorderCenter(Location currentBorderCenter) {
		this.currentBorderCenter = currentBorderCenter;
	}

	/**
	 * @return the currentBorderRadius
	 */
	public double getCurrentBorderRadius() {
		return currentBorderRadius;
	}

	/**
	 * @param currentBorderRadius the currentBorderRadius to set
	 */
	public void setCurrentBorderRadius(double currentBorderRadius) {
		this.currentBorderRadius = currentBorderRadius;
	}

	/**
	 * @return the nextBorderCenter
	 */
	public Location getNextBorderCenter() {
		return nextBorderCenter;
	}

	/**
	 * @param nextBorderCenter the nextBorderCenter to set
	 */
	public void setNextBorderCenter(Location nextBorderCenter) {
		this.nextBorderCenter = nextBorderCenter;
	}

	/**
	 * @return the nextBorderRadius
	 */
	public double getNextBorderRadius() {
		return nextBorderRadius;
	}

	/**
	 * @param nextBorderRadius the nextBorderRadius to set
	 */
	public void setNextBorderRadius(double nextBorderRadius) {
		this.nextBorderRadius = nextBorderRadius;
	}
	
	/**
	 * Reload map configuration.
	 */
	public void reloadConfig() {
		config = new BRMapsYamlManager(this);
	}
	
	/**
	 * Get the world manager.
	 * 
	 * @return the world manager.
	 */
	public BRMapsWorldManager getWorldManager() {
		return worldManager;
	}
	
	@Override
	public String getAddress() {
		return folder.getPath();
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	/**
	 * Set the map world.
	 * 
	 * @param world the world.
	 */
	public void setWorld(final World world) {
		if (this.world == null) { // check if the world is not already set.
			this.world = world;
		}
	}
	
	@Override
	public int saveToConfig(ConfigurationSection section) {
		return config.saveToConfig(section);
	}
	
	/**
	 * Prepare This World for the Game.
	 */
	public void preareWorld() {
		// PREPARE RANDOM CHESTS WITH ITS RANDOM LOOTS.
		{
			// set chests to air.
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
				this.config.getValidChests().stream().filter ( location -> location.getWorld ( ) != null )
						.forEach(location -> {
							location.getBlock().setType(Material.AIR);
						});
			}
			
			/* get loot items array length */
			int loot_array_length = 0;
			for (LootItem li : LootContainer.GAME.getLoadedLoot()) {
				loot_array_length += li.getProbabilityPercent();
			}
			
			/* get loot array */
			int setted_slots = 0;
			final LootItem[] loot_items = new LootItem[loot_array_length];
			for (LootItem li : LootContainer.GAME.getLoadedLoot()) {
				for (int x = 0; x < li.getProbabilityPercent(); x++) {
					loot_items[setted_slots] = li;
					setted_slots ++;
				}
			}
			
			// set random chests to Material.CHEST
			for (Location loc : this.config.getValidChests()) {
				final Block block = loc.getBlock();
				final Chunk chunk = block.getChunk();
				if (!chunk.isLoaded()) {
					chunk.load();
				}
				
				block.setType(Material.CHEST);
				block.getState().setType(Material.CHEST);
				block.getState().update(true, true);
				if ( !(block.getState() instanceof Chest) ) {
					continue;
				}
				
				/* inventory to fill */
				final Chest      ch = (Chest) block.getState();
				final Inventory inv = ch.getBlockInventory();
				
				/* get min and max loot quantity */
				final int min_lq = Math.min(LootContainer.GAME.getLoadedLoot().size(), 3);
				final int max_lq = Math.min(LootContainer.GAME.getLoadedLoot().size(), RandomUtils.nextInt(6));
				
				/* get quantity of item to add*/
				final int loot_quantity = Math.max(max_lq, min_lq);
				
				/* generate loot */
				final List<LootItem> loot = new ArrayList<LootItem>();
				int count = 0;
				while(count < loot_quantity) {
					/* get random loot item */
					LootItem random = loot_items[RandomUtils.nextInt(loot_items.length)];
					
					/* check is not already added */
					if (loot.contains(random)) {
						continue;
					}
					
					/* add to loot */
					loot.add(random);
					count ++;
				}
				
				/* add loot items parents */
				for (LootItem li : new ArrayList<LootItem>(loot)) {
					for (LootItem parent : li.getParents()) {
						loot.add(parent);
					}
				}
				
				/* loot chest */
				count = 0; // reset counter.
				while(count < loot.size()) {
					/* get random slot */
					int random = RandomUtils.nextInt(ItemMenuSize.THREE_LINE.getSize());
					
					/* check is not busy slot */
					if (inv.getItem(random) != null) {
						continue;
					}
					
					/* add item to chest */
					loot.get(count).set(inv, random);
					count ++;
				}
			}
		}
		
		// show initiali border.
		if ( getConfig ( ).getMapCenter ( ) != null && getConfig ( ).getMapCenter ( ).isValid ( ) ) {
			final WorldBorder border = getWorld().getWorldBorder();
			border.setCenter(getConfig().getMapCenter());
			border.setSize(getArea().getRadius());
		}
	}
	
	public void prepareWorldToConfig ( ) {
		BukkitReflection.clearBorder ( getWorld ( ) );
//		ReflectionUtils.clearWorldBorder(getWorld()); /* clear world border */
	}

	/**
	 * Send a player to a random spawn.
	 * <p>
	 * @param p the player to send.
	 */
	public void sendToSpawn(final Player p, boolean teleport, boolean starting_game) {
		BattleMode mode = GameManager.getBattleMode();
		if (GameManager.isNotRunning()) {
			return;
		}
		
		// check player.
		if (p == null || !p.isOnline()) {
			return;
		}
		
		// get and check player.
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		if (bp == null || !bp.isOnline()) {
			return;
		}
		
		// check team.
		if (!bp.hasTeam()) {
			if (BRTeam.getTeams().size() < mode.getMaxTeams() || !BattleModeUtils.isLimitedTeams(mode)) {
				new BRTeam().addMember(bp); // create and join team.
			} else { /* set spectator */
				bp.setPlayerMode(BRPlayerMode.SPECTATOR);
				p.setGameMode(GameMode.ADVENTURE);
				teleport = true;
			}
		}
		
		// add parachute.
		bp.setHasParachute(true);
		
		// remove potion effects.
		p.getActivePotionEffects().forEach(effect -> {
			p.removePotionEffect(effect.getType());
		});
		
		// clear xp
		p.setLevel(0);
		p.setExp(0.0F);
		
		// set full food, and health.
		p.setHealth(Math.min(Math.max(mode.getStartHealth(), 2.0D), 2048.0D));
		p.setMaxHealth(Math.min(Math.max(mode.getMaxHealth(), 2.0D), 2048.0D));
		p.setFoodLevel(20);
		p.setInvulnerable ( false );
		p.setNoDamageTicks ( 0 );
		
		// change gamemode.
		bp.setPlayerMode(BRPlayerMode.PLAYING);
		p.setGameMode(GameMode.SURVIVAL);
		
		// clear player inventory.
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();
		
		// send to spawn.
		if (teleport) {
			final Location spawn = this.getConfig().getRandomSpawn();
			if (spawn != null) {
				p.teleport(spawn);
			} else {
				p.teleport(getWorld().getSpawnLocation());
				
				// send no random spawn found.
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, 
						"Could not found a random spawn for the player: '" + p.getName() + "'!"
						, BattleRoyale.getInstance());
			}
		}
		
		// add battle items
		SchedulerUtil.runTaskLater ( new Runnable ( ) {
			@Override public void run ( ) {
				// check game state.
				if (GameManager.isNotRunning()) {
					return;
				}
				
				// check player world.
				if (!LocUtils.isOnLobby(p) || starting_game) {
					/* add initial loot */
					BattleItems.MINI_MAP.giveToPlayer(p, 1); /* mini map */
					for (LootItem loot_item : LootContainer.INITIAL.getLoadedLoot()) {
						if (loot_item != null && loot_item.isValid()) {
							/* give to player */
							loot_item.give ( p );
							
							/* give to player the loot item parents */
							for ( LootItem parent : loot_item.getParents ( ) ) {
								parent.give ( p );
							}
						}
					}
				} else {
					p.getInventory().clear(); p.updateInventory();
					
					/* add mini map for this spectator */
					BattleItems.MINI_MAP.giveToPlayer(p, 1);
					
					// change gamemode.
					bp.setPlayerMode(BRPlayerMode.SPECTATOR);
					p.setGameMode(GameMode.ADVENTURE);
					
					// send to a random player.
					p.teleport(new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0));
				}
			}
		}, 10, BattleRoyale.getInstance());
	}
	
	/**
	 * Climb all team members to their vehicle.
	 */
	public void mountPlayersOnVehicles() {
		SchedulerUtil.runTaskLater ( new Runnable ( ) {
			@Override
			public void run() { 
//				FileLogger    logger = BattleRoyale.getInstance().getFileLogger();
				final Location spawn = getConfig().getRandomSpawn();
				if (spawn == null) {
//					logger.log("[MOUNT METHOD] No spanws find for the players.");
					return;
				}
				
				/* log */
//				logger.log("[MOUNT METHOD] mounting...");
				
				// get teams.
				final List<Team> validteams = BRTeam.getTeams();
				final BRTeam[]        teams = new BRTeam[validteams.size()];
				int                   count = 0;
				for (Team team : validteams) {
					if (team instanceof BRTeam) {
						teams[count] = (BRTeam) team;
						count ++;
					}
				}
				
				// climb all team members.
				for (Team team : validteams) { // logger.log("[MOUNT METHOD] mounting players of a team");
					for (Member mem : team.getOnlineMembers()) { // logger.log("[MOUNT METHOD] mounting member " + (mem != null ? mem.getName() : "unknown"));
						if (!(mem instanceof BRPlayer)) {
//							logger.log("[MOUNT METHOD] a member that is not an instanceo of 'BRPlayer' has been found!");
							continue;
						}
						
						// get BRPlayer from member.
						BRPlayer br_player = (BRPlayer) mem;
						
						// teleport player to the spawn.
						br_player.getPlayer().teleport(spawn);
						
						// climb.
						SchedulerUtil.runTaskLater ( ( ) -> {
							final BRVehicle vehicle = new BRVehicle ( br_player.getPlayer ( ),
									br_player.getDatabasePlayer ( ).getUsingVehicleType ( ),
									br_player.getDatabasePlayer ( ).getUsingVehicleParticle ( ), spawn );
							
							vehicle.join ( );
							vehicle.start ( );
						} , 4 , BattleRoyale.getInstance());
					}
				}				
			}
		}, 15 , BattleRoyale.getInstance ( ) );
	}

	@Override
	public void unload() {
		for (Player p : Bukkit.getOnlinePlayers()) { /* kick players */
			if (LocUtils.isOnBattleMap(p)) {
				p.kickPlayer(ChatColor.RED + "Restarting server!");
			}
		}
		
		// logg unload procces.
		ConsoleUtil.sendPluginMessage ( ChatColor.RESET,
				"Battle Royale map unload successfully: " + Bukkit.unloadWorld(getWorld().getName(), false),
				BattleRoyale.getInstance());
		
		// remove temp-copy.
		getWorldManager().removeTempWorld();
	}
}