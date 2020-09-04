package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;

/**
 * Represents the classes
 * of loot for the Battle Royale.
 * <p>
 * @author AdrianSR
 */
public enum LootType {

	/**
	 * The items players
	 * will get at the start
	 * of the game.
	 */
	INITIAL,

	/**
	 * Ordinary Battle Royale items.
	 * ({@link BattleItems})
	 */
	CLASSIC,
	
	/**
	 * The type of loot item
	 * that allows the server owners
	 * to create custom items.
	 */
	CUSTOM,
	
	/**
	 * Quality Armor items.
	 */
	QUALITY_ARMORY,
	
	/**
	 * Crack shot plus items.
	 */
	CRACKSHOT_PLUS;
	
	/**
	 * Returns loot type with 
	 * the giving name.
	 * <p>
	 * @param name the name of the {@link LootType}.
	 * @return loot type with the giving name.
	 */
	public static LootType fromName(String name) {
		for (LootType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}