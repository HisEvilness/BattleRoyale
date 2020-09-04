package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.qa;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootType;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootManager;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.api.QualityArmory;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Sunday 09 August, 2020 / 12:01 PM
 */
public class QualityArmoryLootItem implements LootItem {
	
	/**
	 * Quality armor object yml key.
	 */
	public static final String QUALITY_ARMORY_OBJECT_KEY = "quality-armory-object-name";

	/**
	 * Class variables.
	 */
	protected final CustomBaseObject armory_object;
	protected final int        probability_percent;
	protected       int                     amount;
	protected final List<LootItem>         parents;
	protected final String            section_name;
	
	/**
	 * Construct new Quality Armory Loot Item.
	 * <p>
	 * @param probability_percent
	 * @param armory_object
	 * @param amount the amount the ItemStack will have.
	 * @param parents
	 * @param section_name
	 */
	public QualityArmoryLootItem(int probability_percent, CustomBaseObject armory_object, 
			int amount, List<LootItem> parents, String section_name) {
		this.probability_percent = probability_percent;
		this.armory_object       = armory_object;
		this.amount              = amount;
		this.parents             = parents;
		this.section_name        = section_name;
	}
	
	/**
	 * Construct new Quality Armory Loot Item.
	 * <p>
	 * @param probability_percent
	 * @param armory_object
	 * @param amount the amount the ItemStack will have.
	 * @param section_name
	 */
	public QualityArmoryLootItem(int probability_percent, CustomBaseObject armory_object, int amount, String section_name) {
		this(probability_percent, armory_object, amount, new ArrayList<LootItem>(), section_name);
	}
	
	/**
	 * Construct the {@link QualityArmoryLootItem} from a {@link ConfigurationSection}.
	 * <p>
	 * @param section the configuration section the item configuration is stored.
	 */
	public QualityArmoryLootItem ( ConfigurationSection section ) {
		this.probability_percent = section.getInt ( LootManager.PROBABILITY_PERCENT_KEY );
		this.amount              = section.getInt ( LootManager.AMOUNT_KEY );
		this.parents             = new ArrayList < LootItem > ( );
		this.section_name        = section.getName ( );
		
		// armory object loading
		String object_name = section.getString ( QUALITY_ARMORY_OBJECT_KEY );
		if ( object_name != null ) {
			this.armory_object = QualityArmory.getCustomItemByName ( object_name );
		} else {
			this.armory_object = null;
		}
	}
	
	public CustomBaseObject getArmoryObject ( ) {
		return armory_object;
	}
	
	@Override
	public ItemStack getItemStack ( ) {
		if ( armory_object != null || getItemAmount ( ) <= 0 ) {
			ItemStack stack = QualityArmory.getCustomItemAsItemStack ( armory_object );
			stack.setAmount ( getItemAmount ( ) );
			return stack;
		} else {
			return null;
		}
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
	public int getItemAmount() {
		return amount;
	}

	@Override
	public void setItemAmount(int amount) {
		this.amount = Math.max(amount, 1);
	}

	@Override
	public int getProbabilityPercent() {
		return probability_percent;
	}

	@Override
	public List<LootItem> getParents() {
		return parents;
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
			getParents().remove(parent); /* add parent */
		}
	}

	@Override
	public LootType getType() {
		return LootType.QUALITY_ARMORY;
	}

	@Override
	public String getSectionName() {
		return section_name;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = YamlUtil.setNotEqual ( section , LootManager.TYPE_KEY , getType ( ).name ( ) ) ? 1 : 0;
		
		if ( armory_object != null ) {
			save += YamlUtil.setNotEqual ( section , QUALITY_ARMORY_OBJECT_KEY , armory_object.getName ( ) ) ? 1 : 0;
		}
		
		save += YamlUtil.setNotEqual ( section , LootManager.PROBABILITY_PERCENT_KEY , getProbabilityPercent ( ) ) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , LootManager.AMOUNT_KEY , getItemAmount ( ) ) ? 1 : 0;
		
		// saving parents
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
			save += YamlUtil.setNotEqual ( section , LootManager.PARENT_KEY, parents_config ) ? 1 : 0;
		}
		return save;
	}

	@Override
	public boolean isValid() {
		return getItemStack() != null && getArmoryObject() != null && getProbabilityPercent() >= 0 && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}
	
	@Override
	public boolean isInitValid() {
		return getItemStack() != null && getArmoryObject() != null && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}

	@Override
	public QualityArmoryLootItem clone() {
		try {
			return (QualityArmoryLootItem) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}