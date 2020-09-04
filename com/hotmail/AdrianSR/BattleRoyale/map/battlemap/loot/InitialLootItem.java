package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootManager;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the Initial Battle Royale
 * loot items using {@link BattleItems}.
 * <p>
 * @author AdrianSR
 */
public class InitialLootItem implements LootItem {
	
	/**
	 * Global class variables.
	 */
	public static final String BATTLE_ITEM_KEY = "battle-item";
	
	/**
	 * Class variables.
	 */
	private final BattleItems              item;
	private       int                    amount;
	private final String           section_name;
	
	/**
	 * Construct new Initial Loot Item.
	 * <p>
	 * @param item the {@link BattleItems} to add in the loot.
	 * @param amount the amount the ItemStack will have.
	 */
	public InitialLootItem(BattleItems item, int amount, String section_name) {
		this.item         = item;
		this.amount       = amount;
		this.section_name = section_name;
	}
	
	/**
	 * Construct new Initial Loot Item
	 * loading his configuration from
	 * a {@link ConfigurationSection}.
	 */
	public InitialLootItem(ConfigurationSection section) {
		if (section != null) {
			this.item 		  = BattleItems.fromName(section.getString(BATTLE_ITEM_KEY));
			this.amount 	  = section.getInt(LootManager.AMOUNT_KEY, 1);
			this.section_name = section.getName();
		} else { /* invalid loot item data */
			this.item 		  = null;
			this.amount 	  = -1;
			this.section_name = null;
		}
	}
	
	public BattleItems getBattleItem() {
		return item;
	}
	
	@Override
	public int getItemAmount() {
		return amount;
	}
	
	@Override
	public void setItemAmount(int amount) {
		if (amount > 0) {
			this.amount = amount;
		}
	}
	
	@Override
	public ItemStack getItemStack() {
		if (getBattleItem() == null || getItemAmount() <= 0) {
			return ItemStackUtil.getEmptyStack();
		}
		return getBattleItem().asItemStack(getItemAmount());
	}
	
	@Override
	public void give(Player player) {
		if (isValid()) {
			player.getInventory().addItem(getItemStack());
			player.updateInventory();
		}
	}
	
	@Override
	public void add(Inventory inventory) {
		inventory.addItem(getItemStack());
	}
	
	@Override
	public void set(Inventory inventory, int slot) {
		inventory.setItem(slot, getItemStack());
	}
	
	@Override
	public int getProbabilityPercent() {
		return -1;
	}
	
	@Override
	public List<LootItem> getParents() {
		return new ArrayList<>();
	}
	
	@Override
	public void addParent(LootItem parent) {
		throw new UnsupportedOperationException("The initial loot item cannot have parents!");
	}

	@Override
	public void removeParent(LootItem parent) {
		throw new UnsupportedOperationException("The initial loot item cannot have parents!");
	}
	
	@Override
	public LootType getType() {
		return LootType.INITIAL;
	}
	
	@Override
	public String getSectionName ( ) {
		return section_name;
	}

	@Override
	public int save ( ConfigurationSection section ) {
		return  ( YamlUtil.setNotSet ( section , BATTLE_ITEM_KEY, getBattleItem().name()) ? 1 : 0 ) +
				( YamlUtil.setNotSet ( section , LootManager.TYPE_KEY, getType ( ).name ( ) ) ? 1 : 0 ) +
				( YamlUtil.setNotSet ( section , LootManager.AMOUNT_KEY, getItemAmount ( ) ) ? 1 : 0 );
	}

	@Override
	public boolean isValid() {
		return getItemStack() != null && !StringUtils.isBlank(getSectionName());
	}
	
	@Override
	public boolean isInitValid() {
		return isValid();
	}

    @Override
    public InitialLootItem clone() {
        try {
            return (InitialLootItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}