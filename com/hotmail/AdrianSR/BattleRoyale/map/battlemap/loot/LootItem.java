package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.csp.CrackShotPlusLootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootManager;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.qa.QualityArmoryLootItem;
import com.hotmail.adriansr.core.util.saveable.Saveable;

/**
 * Represents the Battle Royale
 * loot items.
 * <p>
 * @author AdrianSR
 */
public interface LootItem extends Saveable, Cloneable {
	
	/**
	 * Returns the a {@link LootItem} loaded
	 * from the giving {@link ConfigurationSection}.
	 * <p>
	 * This method detects the type of loot item
	 * and returns an instance of that type.
	 * <p>
	 * @param section the ConfigurationSection to load from.
	 * @return loaded loot item.
	 */
	public static LootItem of(ConfigurationSection section) {
		LootType type = LootType.fromName(section.getString(LootManager.TYPE_KEY, ""));
		if (type == null) { /* donnot load item if its type is unknown */
			return null;
		}
		
		/* load loot item depending his type */
		switch (type) {
		
		/**
		 * Load as InitialLootItem.
		 */
		case INITIAL: {
			return new InitialLootItem(section);
		}
		
		/**
		 * Load as ClassicLootItem.
		 */
		case CLASSIC: {
			return new ClassicLootItem(section);
		}
		
		/**
		 * Load as CustomLootItem.
		 */
		case CUSTOM: {
			return new CustomLootItem(section);
		}

		/**
		 * Load as QALootItem.
		 */
		case QUALITY_ARMORY: {
			return BattleRoyale.isQualitArmoryEnabled ( ) ? new QualityArmoryLootItem ( section ) : null;
		}
		
		/**
		 * Load as CSPLootItem.
		 */
		case CRACKSHOT_PLUS: {
			return ( BattleRoyale.isCrackshotPlusEnabled() ? new CrackShotPlusLootItem(section) : null );
		}
		
		default:
			break;
		}
		return null;
	}

	/**
	 * Returns this {@link LootItem}
	 * {@link ItemStack}
	 * <p>
	 * @return the {@link ItemStack of this Loot Item}
	 */
	public ItemStack getItemStack();
	
	/**
	 * Give item to a specific
	 * player.
	 * <p>
	 * @param player the player will get this.
	 */
	public void give(Player player);
	
	/**
	 * Add to a inventory.
	 * <p>
	 * @param inventory to add this.
	 */
	public void add(Inventory inventory);
	
	/**
	 * Set in a specific slot inside an inventory.
	 * <p>
	 * @param inventory
	 * @param slot
	 */
	public void set(Inventory inventory, int slot);
	
	public int getItemAmount();
	
	public void setItemAmount(int amount);
	
	/**
	 * Returns the percentage of probability 
	 * that this article has to appear in the chest.
	 * <p>
	 * @return probability percent.
	 */
	public int getProbabilityPercent();
	
	/**
	 * Returns a list of {@link LootItem}
	 * that will be generated with this
	 * {@link LootItem}.
	 * <p>
	 * @return parents list.
	 */
	public List<LootItem> getParents();
	
	public void addParent(LootItem parent);
	
	public void removeParent(LootItem parent);
	
	/**
	 * Returns the type of loot
	 * this item use.
	 * <p>
	 * @return loot type this item use.
	 */
	public LootType getType();
	
	public String getSectionName();
	
	public boolean isValid();
	
	public boolean isInitValid();
	
	public LootItem clone();
}