package com.hotmail.AdrianSR.BattleRoyale.map.managers;

import java.io.File;

import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Map;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.lobbymap.LobbyMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsWorldManager;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.file.FileUtil;

/**
 * Represents the Battle 
 * Royale maps Manager.
 * <p>
 * @author AdrianSR
 */
public final class MapsManager {
	/**
	 * Battle Maps files.
	 */
	public static final String MAPS_FOLDER_NAME = "BattleRoyaleMaps";
	
	/**
	 * The Game lobby map.
	 */
	public static LobbyMap LOBBY_MAP;
	
	/**
	 * The Game Battle map.
	 */
	public static BattleMap BATTLE_MAP;
	
	/**
	 * Set the game lobby map.
	 * <p>
	 * @param map new lobby map.
	 */
	public static void setLobbyMap(final LobbyMap map) {
		// check is not game runnig.
		if (GameManager.isRunning()) {
			throw new UnsupportedOperationException("Cannot change the Lobby Map while the game is runnig!");
		}
		
		// set.
		LOBBY_MAP = map;
	}
	
	/**
	 * Set the game battle map.
	 * <p>
	 * @param map new battle map.
	 */
	public static void setBattleMap(final BattleMap map) {
		// check is not game runnig.
		if (GameManager.isRunning()) {
			throw new UnsupportedOperationException("Cannot change the Battle Map while the game is runnig!");
		}
		
		// set.
		BATTLE_MAP = map;
	}
	
	/**
	 * Check if the folder 
	 * plugins/BattleRoyale/BattleMaps, exists.
	 * <p>
	 * @return the battle maps folder file.
	 */
	public static File checkBattleMapsFolder() {
		// get folder file.
		final File folder = new File(BattleRoyale.getInstance().getDataFolder(), MAPS_FOLDER_NAME);
		
		// check is not exists.
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	/**
	 * Load battle map on server
	 * start.
	 */
	public static void loadBattleMapInstantly() {
		if (!Config.MAP_LOAD_ON_SEVER_START.getAsBoolean()) { /* load battle map on server start */
			return;
		}
		
		File map_folder = new File(MapsManager.checkBattleMapsFolder(), Config.MAP_TO_LOAD.toString());
		if (!map_folder.exists()) {
			return;
		}
		
		final long process_start = System.currentTimeMillis();
		ConsoleUtil.sendPluginMessage ( "Triying to load the BattleMap especified in config....", BattleRoyale.getInstance());
		BattleMap map = new BattleMap(map_folder);
		if (!map.getWorldManager().loadWorld(map_folder, false)) {
			ConsoleUtil.sendPluginMessage ( "Could not load the Battle Map especified in config!", BattleRoyale.getInstance());
			return;
		}
		
		final long process_end = ((System.currentTimeMillis() - process_start) / 1000);
		ConsoleUtil.sendPluginMessage ( "Battle Map especified in config loaded! (" + process_end + "s)", BattleRoyale.getInstance());
	}
	
	/**
	 * Unload old worlds.
	 */
	public static void unloadOldWorlds() {
		for (int x = 1; x < Bukkit.getWorlds().size(); x++) { // for starts at 1 to avoid unloading the lobby world
			World world = Bukkit.getWorlds().get(x);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getWorld().getName().equals(world.getName())) {
					p.kickPlayer("!Unloading this world!");
				}
			}
			
			Bukkit.unloadWorld(world.getName(), false);
		}
	}
	
	/**
	 * Remove cache worlds.
	 */
	public static void removeCacheWorlds(final BattleRoyale instance) {
		final File worldsFolder = new File(instance.getDataFolder(), MAPS_FOLDER_NAME);
		if (!worldsFolder.exists()) {
			return;
		}
		
		// remove cache world folders.
		for (File folder : worldsFolder.listFiles()) {
			if (!isCacheWorldFolder(folder)) {
				continue;
			}
			
			forceDelete(folder);
		}
	}
	
	/**
	 * Remove {@link Map} cache world folder.
	 * <p>
	 * @param map the Map.
	 */
	public static void removeCacheWorld(final Map map) {
		// get and check world.
		final World world = map.getWorld();
		if (world == null) {
			return;
		}
		
		// get world folder.
		final File worldFolder = world.getWorldFolder();
		
		// get cache folder.
		final File cacheFolder = isCacheWorldFolder(worldFolder) ? worldFolder
				: new File(worldFolder.getParent(),
						worldFolder.getName() + BRMapsWorldManager.MAPS_TEMP_COPY_FOLDER_SUFIX);
	
		// remove.
		forceDelete(cacheFolder);
	}
	
	/**
	 * Check if a World folder is cache world.
	 * <p>
	 * @param worldFolder the folder of the world to check
	 * @return true if is a cache world.
	 */
	private static boolean isCacheWorldFolder(final File worldFolder) {
		return worldFolder.isDirectory()
				&& worldFolder.getName().trim().endsWith(BRMapsWorldManager.MAPS_TEMP_COPY_FOLDER_SUFIX);
	}
	
	/**
	 * Force folder {@link File} delete.
	 * <p>
	 * @param folder the folder file to delete.
	 */
	private static void forceDelete(final File folder) {
		try {
			// remove
//			org.apache.commons.io.FileUtils.forceDelete(folder);
//			FileUtils.forcedRemoveDir(folder);
			
			FileUtil.forceDelete ( folder );
			FileDeleteStrategy.FORCE.delete ( folder );
			
			folder.delete();
		} catch (Throwable e) {
			// ignore.
		}
	}
}