package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.csp;

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
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

import me.DeeCaaD.CrackShotPlus.API;
import me.DeeCaaD.CrackShotPlus.CSPapi;

public class CrackShotPlusLootItem implements LootItem {
	
	/**
	 * Crack Shot Plus object yml key.
	 */
	public static final String CRACKSHOT_PLUS_WEAPON_KEY = "crackshot-plus-weapon-name";
	
	private final String         csp_weapon_name;
	private final int        probability_percent;
	private       int                     amount;
	private final List<LootItem>         parents;
	private final String            section_name;
	
	public CrackShotPlusLootItem(int probability_percent, String csp_weapon_name, int amount, List<LootItem> parents, String section_name) {
		this.probability_percent = probability_percent;
		this.csp_weapon_name     = csp_weapon_name;
		this.amount              = amount;
		this.parents             = parents;
		this.section_name        = section_name;
	}
	
	public CrackShotPlusLootItem(int probability_percent, String csp_weapon_name, int amount, String section_name) {
		this(probability_percent, csp_weapon_name, amount, new ArrayList<LootItem>(), section_name);
	}

	public CrackShotPlusLootItem  ( ConfigurationSection section ) {
		this.csp_weapon_name     = section.getString(CRACKSHOT_PLUS_WEAPON_KEY);
		this.probability_percent = section.getInt(LootManager.PROBABILITY_PERCENT_KEY);
		this.amount              = section.getInt(LootManager.AMOUNT_KEY);
		this.parents             = new ArrayList<LootItem>();
		this.section_name        = section.getName();
	}

	@Override
	public ItemStack getItemStack ( ) {
		if ( StringUtils.isBlank ( csp_weapon_name ) ) {
			return ItemStackUtil.getEmptyStack ( );
		} else {
			return CSPapi.updateItemStackFeaturesNonPlayer ( csp_weapon_name , API.getCSUtility ( ).generateWeapon ( csp_weapon_name ) );
		}
	}
	
	@Override
	public void give ( Player player ) {
		API.getCSUtility ( ).giveWeapon ( player , csp_weapon_name , getItemAmount ( ) );
		player.updateInventory ( );
//		ItemStack item = getItemStack();
//		if (item != null) {
//			API.dropOrGiveItemFromInv(player, CSPapi.updateItemStackFeatures(csp_weapon_name, item, player));
//			player.updateInventory();
//		}
	}
	
	@Override
	public void add(Inventory inventory) {
		ItemStack item = getItemStack();
		if (item != null) {
			inventory.addItem(CSPapi.updateItemStackFeaturesNonPlayer(csp_weapon_name, item));
		}
	}
	
	@Override
	public void set(Inventory inventory, int slot) {
		ItemStack item = getItemStack();
		if (item != null) {
			inventory.setItem(slot, CSPapi.updateItemStackFeaturesNonPlayer(csp_weapon_name, item));
		}
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
		return LootType.CRACKSHOT_PLUS;
	}

	@Override
	public String getSectionName() {
		return section_name;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = YamlUtil.setNotEqual ( section , LootManager.TYPE_KEY, getType().name()) ? 1 : 0;
		
		save += YamlUtil.setNotEqual ( section , CRACKSHOT_PLUS_WEAPON_KEY, csp_weapon_name) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , LootManager.PROBABILITY_PERCENT_KEY, getProbabilityPercent()) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , LootManager.AMOUNT_KEY, getItemAmount()) ? 1 : 0;
		
		/* check parents */
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
		return getItemStack() != null && getProbabilityPercent() >= 0 && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}
	
	@Override
	public boolean isInitValid() {
		return getItemStack() != null && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName());
	}
	
	@Override
	public CrackShotPlusLootItem clone() {
		try {
			return (CrackShotPlusLootItem) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}