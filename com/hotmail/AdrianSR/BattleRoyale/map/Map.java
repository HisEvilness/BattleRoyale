package com.hotmail.AdrianSR.BattleRoyale.map;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a Battle Royale Map.
 * <p>
 * @author AdrianSR
 */
public interface Map {
	
	/**
	 * The map world name.
	 * <p>
	 * @return the map world name.
	 */
	public String getName();
	
	/**
	 * The folder address.
	 * <p>
	 * @return the folder address.
	 */
	public String getAddress();
	
	/**
	 * The map world.
	 * <p>
	 * @return the map world.
	 */
	public World getWorld();
	
	/**
	 * Save to section.
	 * <p>
	 * @param section the section to save.
	 * @return the total changes in section.
	 */
	public int saveToConfig(final ConfigurationSection section);
	
	/**
	 * Unload this map.
	 */
	public void unload();
}