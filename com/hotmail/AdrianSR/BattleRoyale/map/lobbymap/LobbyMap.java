package com.hotmail.AdrianSR.BattleRoyale.map.lobbymap;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.config.items.LobbyItem;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Map;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the lobby map.
 * <p>
 * @author AdrianSR
 */
public final class LobbyMap implements Map, Listener {

	/**
	 * Class values.
	 */
	private final String                name;
	private       File                folder;
	private       ConfigurableLocation spawn;
	
	/**
	 * Construct a new lobby map.
	 * <p>
	 * @param name the name.
	 */
	public LobbyMap(final String name) {
		this.name = name;
		
		// register listeners.
		register();
	}
	
	/**
	 * Construct a new lobby map.
	 * <p>
	 * @param sc the section to load.
	 */
	public LobbyMap(final ConfigurationSection sc) {
		this.folder = getWorld().getWorldFolder();
		this.name   = sc.isString("Name") ? sc.getString("Name") : folder.getName();
		this.spawn  = sc.isConfigurationSection("Spawn") ? ConfigurableLocation.of ( sc.getConfigurationSection ( "Spawn" ) ) : null;
		
		// register listeners.
		register();
		
		// modify world.
		SchedulerUtil.runTaskLater ( ( ) -> modifyWorld ( ) , 2L , BattleRoyale.getInstance ( ) );
	}
	
	/**
	 * Modify lobby world.
	 */
	private void modifyWorld() {
		final World world = getWorld();
		
		/* clean entitites */
		for (LivingEntity ent : world.getLivingEntities()) {
			if (!(ent instanceof Player)) {
				ent.remove();
			}
		}
		
		/* avoid mob spawning */
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doFireTick", "false");
		
		/* always day */
		world.setTime(500L);
		world.setGameRuleValue("doDaylightCycle", "false");
	}
	
	/**
	 * Register listeners in this class.
	 */
	private void register() {
		Bukkit.getPluginManager().registerEvents(this, BattleRoyale.getInstance());
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAddress() {
		return folder.getPath();
	}
	
	/**
	 * Get the players spawn location.
	 * 
	 * @return the spawn location.
	 */
	public ConfigurableLocation getSpawn() {
		return spawn;
	}
	
	/**
	 * Set the players spawn location.
	 * 
	 * @param loc the new spawn location.
	 */
	public void setSpawn ( ConfigurableLocation loc ) {
		spawn = loc;
	}

	@Override
	public World getWorld() { // the lobby world is the first bukkit world.
		return Bukkit.getWorlds().get(0);
	}
	
	public boolean saveToConfig() {
		// get save yml.
		final File datafolder = BattleRoyale.getInstance().getDataFolder();
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}
		
		// get and check file.
		final File ymlFile = new File(datafolder, "BattleRoyaleLobby.yml");
		if (!ymlFile.exists()) {
			try {
				ymlFile.createNewFile();
			} catch (IOException e) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "The lobby configuration file could not be created: " , 
						BattleRoyale.getInstance ( ) );
				e.printStackTrace();
				return false;
			}
		}
		
		// load and check Yaml configuration.
		final YamlConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);
		if (yml == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED , "!The lobby configuration could not be saved!" , 
					BattleRoyale.getInstance ( ) );
			return false;
		}
		
		// save.
		if (saveToConfig(yml) > 0) {
			try {
				yml.save(ymlFile);
			} catch (IOException e) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "The lobby configuration could not be saved: " , 
						BattleRoyale.getInstance ( ) );
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public int saveToConfig ( ConfigurationSection section ) {
		return ( YamlUtil.setNotEqual ( section , "Name" , name ) ? 1 : 0 )
			   + (spawn != null ? spawn.save ( section.createSection ( "Spawn" ) ) : 0 );
	}

	/**
	 * Send a player to the lobby spawn.
	 * <p>
	 * @param p the player to send.
	 */
	public void sendToSpawn(final Player p) {
		/* avoid bugged players by the vehicles after server restarting */
		p.leaveVehicle();
		p.eject();
		
		/* clear potion effects */
		p.getActivePotionEffects().forEach(effect -> {
			p.removePotionEffect(effect.getType());
		});
		
		p.setLevel(0);
		p.setExp(0.0F);
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setGameMode(GameMode.ADVENTURE);
		p.setFireTicks ( 0 );
		
		SchedulerUtil.runTaskLater ( new Runnable ( ) {
			@Override public void run ( ) {
				p.setFlying ( false );
				p.setAllowFlight ( false );
				
				/* avoid bugged players by the vehicles after server restarting */
				try {
					if (p.getVehicle() != null) {
						p.getVehicle().eject();
					}
					
					p.leaveVehicle();
					p.eject();
					
					// sneak.
					final boolean sn = p.isSneaking();
					p.setSneaking(!sn);
					p.setSneaking(sn);
				} catch(Throwable t) {
					// ignore.
				}
				
				// send to spawn.
				if ( getSpawn ( ) != null && getSpawn ( ).isValid ( ) ) {
					p.teleport ( getSpawn ( ) );
				} else {
					ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW ,
							"!The player could not be sent to the lobby: Invalid lobby configuration!"
							, BattleRoyale.getInstance ( ) );
				}
			}
		} , 15 , BattleRoyale.getInstance ( ) );
		
		p.getInventory().clear();
		for (LobbyItem item : LobbyItem.values()) {
			if (item.isEnabled()) {
				item.getUse().onJoin(p);
			}
		}
		p.updateInventory();
	}
	
	/**
	 * Send players to the lobby spawn.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoinLobby(final PlayerJoinEvent eve) {
		Player p = eve.getPlayer();
		
		// send player to the lobby spawn.
		sendToSpawn(p);
		
		// check player team.
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		if (bp != null && bp.hasTeam()) {
			BRTeam.checkTeam(bp);
			if (!bp.hasTeam()) {
				for (Team team : BRTeam.getTeams()) {
					team.removeMember(bp);
				}
			}
		}
	}
	
	/**
	 * Disallow break blocks on the lobby.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreakBlock(final BlockBreakEvent eve) {
		disallowPlaceBreakBlocks(eve, eve.getPlayer());
	}
	
	/**
	 * Disallow place blocks on the lobby.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreakBlock(final BlockPlaceEvent eve) {
		disallowPlaceBreakBlocks(eve, eve.getPlayer());
	}
	
	/**
	 * Disallow players to place, or break blocks on the lobby.
	 */
	private void disallowPlaceBreakBlocks(final BlockEvent eve, final Player p) {
		if (LocUtils.isTheLobbyWorld(eve.getBlock().getWorld())) {
			if (p.getGameMode() != GameMode.CREATIVE) {
				if (eve instanceof BlockBreakEvent) {
					((BlockBreakEvent) eve).setCancelled(true);
				} else if (eve instanceof BlockPlaceEvent) {
					((BlockPlaceEvent) eve).setCancelled(true);
				}
			}
		}
	}
	
	/**
	 * Disallow players to empty buckets on the lobby.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBucketEmpty(final PlayerBucketEmptyEvent eve) {
		final Player p = eve.getPlayer();
		if (LocUtils.isTheLobbyWorld(p.getWorld())) {
			if (p.getGameMode() != GameMode.CREATIVE) {
				eve.setCancelled(true);
			}
		}
	}
	
	/**
	 * Send players to the lobby spawn when respawn.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent eve) {
		if (LocUtils.isTheLobbyWorld(eve.getPlayer().getWorld())) {
			sendToSpawn(eve.getPlayer());
		}
	}
	
	/**
	 * Avoid hunger on the lobby.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGetHunger(FoodLevelChangeEvent eve) {
		if (eve.getEntity() instanceof Player) {
			if (LocUtils.isTheLobbyWorld(eve.getEntity().getWorld())) {
				eve.setCancelled(true);
				eve.setFoodLevel(20);
			}
		}
	}
	
	/**
	 * Disable drops on the lobby
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropItem(PlayerDropItemEvent eve) {
		if (LocUtils.isOnLobby(eve.getPlayer())) {
			if (eve.getPlayer().getGameMode() != GameMode.CREATIVE) {
				eve.setCancelled(true);
			}
		}
	}
	
	/**
	 * Detects if a player is using a Lobby item.
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onUseItem(PlayerInteractEvent eve) {
		if (!LobbyItem.isLobbyItem(eve.getItem())) {
			return;
		}
		
		if (!eve.getAction().name().startsWith("RIGHT_")) {
			return; // detects only the right click
		}
		
		LobbyItem.getLobbyItem(eve.getItem()).getUse().onUse(eve.getPlayer(), eve.getItem());
	}
	
	/**
	 * Disallow damage on the lobby.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent eve) {
		if (eve.getEntity() instanceof Player) {
			if (LocUtils.isTheLobbyWorld(eve.getEntity().getWorld())) {
				eve.setCancelled(true);
				
				/* send to spawn if the damage is caused by the void */
				if (eve.getCause() == DamageCause.VOID) {
					sendToSpawn((Player) eve.getEntity());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void stopClicking(InventoryClickEvent event) {
		/* disallow players to move the lobby items inside the inventory */
		final HumanEntity entity = event.getWhoClicked();
		final ItemStack    stack = event.getCurrentItem();
		if (stack != null && (entity instanceof Player)) {
			if (LobbyItem.isLobbyItem(stack)) {
				event.setCancelled(true);
			}
		}
	}
	
	@Override
	public void unload() {
		HandlerList.unregisterAll(this); /* unregister listener */
	}
}