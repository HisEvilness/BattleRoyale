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
 * Represents the Classic Battle Royale
 * loot items using {@link BattleItems}.
 * <p>
 * @author AdrianSR
 */
public class ClassicLootItem implements LootItem {
	
	/**
	 * Global class variables.
	 */
	public static final String BATTLE_ITEM_KEY = "battle-item";
	
	/**
	 * Class variables.
	 */
	private final BattleItems              item;
	private final int       probability_percent;
	private       int                    amount;
	private final List<LootItem>        parents;
	private final String           section_name;
	
	/**
	 * Construct new Classic Loot Item.
	 * <p>
	 * @param probability_percent
	 * @param item the {@link BattleItems} to add in the loot.
	 * @param amount the amount the ItemStack will have.
	 * @param parents
	 * @param section_name
	 */
	public ClassicLootItem(int probability_percent, BattleItems item, 
			int amount, List<LootItem> parents, String section_name) {
		this.probability_percent = probability_percent;
		this.item                = item;
		this.amount              = amount;
		this.parents             = parents;
		this.section_name        = section_name;
	}
	
	/**
	 * Construct new Classic Loot Item.
	 * <p>
	 * @param probability_percent
	 * @param item the {@link BattleItems} to add in the loot.
	 * @param amount the amount the ItemStack will have.
	 */
	public ClassicLootItem(int probability_percent, BattleItems item, int amount, String section_name) {
		this(probability_percent, item, amount, new ArrayList<LootItem>(), section_name);
	}
	
	/**
	 * Construct new Classic Loot Item
	 * loading his configuration from
	 * a {@link ConfigurationSection}.
	 */
	public ClassicLootItem(ConfigurationSection section) {
		if (section != null) {
			this.probability_percent = section.getInt(LootManager.PROBABILITY_PERCENT_KEY);
			this.item                = BattleItems.fromName(section.getString(BATTLE_ITEM_KEY));
			this.amount              = section.getInt(LootManager.AMOUNT_KEY);
			this.parents             = new ArrayList<LootItem>(); // LootManager.decodeParents(section.getString(LootManager.PARENT_KEY));
			this.section_name        = section.getName();
		} else { /* invalid loot item data */
			this.probability_percent = -1;
			this.item                = null;
			this.amount              = -1;
			this.parents             = null;
			this.section_name        = null;
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
		return probability_percent;
	}
	
	@Override
	public List<LootItem> getParents() {
		return parents; // Collections.unmodifiableList(parents);
	}
	
	@Override
	public void addParent(LootItem parent) {
		if (parent == null || !parent.isValid()) {
			return;
		}
		
		if (!getParents().contains(parent)) {
			getParents().add(parent);
		}
	}

	@Override
	public void removeParent(LootItem parent) {
		if (parent != null) {
			getParents().remove(parent);
		}
	}
	
	@Override
	public LootType getType() {
		return LootType.CLASSIC;
	}
	
	@Override
	public String getSectionName() {
		return section_name;
	}

	@Override
	public int save(ConfigurationSection section) {
		int save = YamlUtil.setNotEqual ( section ,LootManager.TYPE_KEY, getType().name()) ? 1 : 0;
		
		save += YamlUtil.setNotEqual ( section , BATTLE_ITEM_KEY, getBattleItem().name()) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , LootManager.PROBABILITY_PERCENT_KEY, getProbabilityPercent()) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , LootManager.AMOUNT_KEY, getItemAmount()) ? 1 : 0;
		
		/* save parents */
		if (getParents() != null && !getParents().isEmpty()) {
			/* get parents config code */
			String parents_config = LootManager.PARENT_CONFIG_START_ARG;
			for (LootItem loot_item : getParents()) {
				/* get parent code */
				String parent_code = loot_item.getSectionName()
						+ LootManager.PARENT_CONFIG_AMOUNT_START_ARG 
						+ LootManager.PARENT_CONFIG_AMOUNT_ARG
						+ LootManager.PARENT_CONFIG_AMOUNT_SPLITER
						+ " "
						+ loot_item.getItemAmount()
						+ LootManager.PARENT_CONFIG_AMOUNT_END_ARG
						+ LootManager.PARENT_CONFIG_SPLITER;
				
				/* register parent code to config parents code */
				parents_config += parent_code;
			}
			
			/* finalize parents config code */
			parents_config += LootManager.PARENT_CONFIG_END_ARG;
			
			/* save in section */
			save += YamlUtil.setNotEqual ( section ,LootManager.PARENT_KEY, parents_config ) ? 1 : 0;
		}
		return save;
	}

	@Override
	public boolean isValid() {
		return getItemStack() != null && getBattleItem() != null && getProbabilityPercent() > 0 && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}
	
	@Override
	public boolean isInitValid() {
		return getItemStack() != null && getBattleItem() != null && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}
	
    @Override
    public ClassicLootItem clone() {
        try {
            return (ClassicLootItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}