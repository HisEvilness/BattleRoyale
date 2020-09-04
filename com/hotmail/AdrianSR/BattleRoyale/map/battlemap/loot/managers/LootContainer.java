package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;

/**
 * An enum for all type of loot containers.
 * <p>
 * @author AdrianSR
 */
public enum LootContainer {
	
	/**
	 * The default loot the player
	 * will have when the game starts.
	 */
	INITIAL("InitialLoot"),
	
	/**
	 * The loot that can be found
	 * inside the chest around of the
	 * battle map.
	 */
	GAME,
	
	/**
	 * The loot that can be found
	 * iside the air supply boxes.
	 */
	AIR_SUPPLY("AirSupplyLoot");
	
	private final String                   section;
	private final List<LootItem>              loot;
	private final Map<String, String> parent_codes;
	
	/**
	 * Construct container, using
	 * a custom {@link ConfigurationSection}
	 * inside the configuration section to load.
	 * <p>
	 * @param section the custom {@link ConfigurationSection} name.
	 */
	LootContainer(String section) {
		this.loot         = new ArrayList<LootItem>();
		this.section      = section;
		this.parent_codes = new HashMap<String, String>();
	}
	
	/**
	 * Construct container.
	 */
	LootContainer() {
		this(null);
	}
	
	/**
	 * Returns the loaded loot
	 * from config for this loot container.
	 * <p>
	 * @return loaded loot.
	 */
	public List<LootItem> getLoadedLoot() {
		return Collections.unmodifiableList(loot);
	}
	
	public String getConfigurationSectionName() {
		return section;
	}
	
	/**
	 * Load loot configuration
	 * from the giving {@link ConfigurationSection}.
	 * <p>
	 * @param root the root configuration section.
	 */
	public void loadLootConfiguration(ConfigurationSection root) {
		// we're making sure it is not loaded multiple times.
		loot.clear ( );
		
		/* loading item from section */
		ConfigurationSection section = ( this.section != null ? root.getConfigurationSection(this.section) : root );
		if (section != null) {
			for (String key : section.getKeys(false)) {
				ConfigurationSection item_cs = section.getConfigurationSection(key);
				if (item_cs == null) {
					continue;
				}
				
				LootItem item = LootItem.of(item_cs);
				if (item == null || ( this == INITIAL ? !item.isInitValid() : !item.isValid() )) {
					continue;
				}
				
				loot.add(item);
				parent_codes.put(item.getSectionName(), item_cs.getString(LootManager.PARENT_KEY, ""));
			}
		}
	}
	
	public void loadParentConfiguration ( ConfigurationSection root ) {
		// parents loading
		for ( LootItem item : loot ) {
			// we're making sure the parents are not loaded multiple times.
			item.getParents ( ).clear ( );
			
			String parent_code = parent_codes.get ( item.getSectionName ( ) );
			if ( parent_code != null && !parent_code.isEmpty ( ) ) {
				for ( LootItem parent : LootManager.getInstace ( ).decodeParents ( parent_code ) ) {
					item.addParent ( parent );
				}
			}
		}					
	}
}