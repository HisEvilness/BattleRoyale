package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootManager;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents the type of LootItem
 * that allows the server owners
 * to create custom items, changing
 * the ItemStack name, lore and adding
 * enchantments.
 * <p>
 * @author AdrianSR
 */
public class CustomLootItem implements LootItem {
	
	public static final String          ITEM_MATERIAL_KEY = "material";
	public static final String              ITEM_NAME_KEY = "name";
	public static final String              ITEM_LORE_KEY = "lore";
	public static final String  ITEM_ENCHANTMENTS_SECTION = "enchantments";
	public static final String       ITEM_ENCHANTMENT_KEY = "enchant";
	public static final String ITEM_ENCHANTMENT_LEVEL_KEY = "level";

	private final Material                  material;
	private final int            probability_percent;
	private       int                         amount;
	private final String                        name;
	private final List<String>                  lore;
	private final Map<Enchantment, Integer> enchants;
	private final List<LootItem>             parents;
	private final String                section_name;
	
	/**
	 * Construct loot item
	 * loading it from a {@link ConfigurationSection}
	 * <p>
	 * @param section the section to load from.
	 */
	public CustomLootItem(ConfigurationSection section) {
		if (section != null) {
			this.probability_percent = section.getInt(LootManager.PROBABILITY_PERCENT_KEY);
			this.material            = EnumReflection.getEnumConstant(Material.class, section.getString(ITEM_MATERIAL_KEY));
			this.amount              = section.getInt(LootManager.AMOUNT_KEY);
			this.name                = section.getString(ITEM_NAME_KEY);
			this.lore                = section.getStringList(ITEM_LORE_KEY);
			this.enchants            = loadEnchantments(section.getConfigurationSection(ITEM_ENCHANTMENTS_SECTION));
			this.parents             = new ArrayList<LootItem>();
			this.section_name        = section.getName();
		} else {
			this.probability_percent = -1;
			this.material            = null;
			this.amount              = -1;
			this.name                = null;
			this.lore                = null;
			this.enchants            = null;
			this.parents             = null;
			this.section_name        = null;
		}
	}
	
	/**
	 * Construct the Custom Item.
	 * <p>
	 * @param probability_percent 
	 * @param material the material for the ItemStack.
	 * @param amount the amount for the ItemStack.
	 * @param name the name for the ItemStack.
	 * @param lore the lore for the ItemStack.
	 * @param enchants the enchantments for the ItemStack.
	 * @param parents
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants,
			List<LootItem> parents, String section_name) {
		this.material            = material;
		this.probability_percent = probability_percent;
		this.amount              = amount;
		this.name                = name;
		this.lore                = ( lore     != null ? lore     : new ArrayList<String>() );
		this.enchants            = ( enchants != null ? enchants : new HashMap<Enchantment, Integer>() );
		this.parents             = ( parents  != null ? parents  : new ArrayList<LootItem>() );
		this.section_name 		 = section_name;
	}
	
	/**
	 * Construct the Custom Item,
	 * excluding parents.
	 * <p>
	 * @param probability_percent 
	 * @param material the material for the ItemStack.
	 * @param amount the amount for the ItemStack.
	 * @param name the name for the ItemStack.
	 * @param lore the lore for the ItemStack.
	 * @param enchants the enchantments for the ItemStack.
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, Material material, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants, String section_name) {
		this(probability_percent, material, amount, name, lore, enchants, null, section_name);
	}
	
	/**
	 * Construct the Custom Item,
	 * excluding parents and enchantments.
	 * <p>
	 * @param probability_percent 
	 * @param material the material for the ItemStack.
	 * @param amount the amount for the ItemStack.
	 * @param name the name for the ItemStack.
	 * @param lore the lore for the ItemStack.
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, Material material, int amount, String name, List<String> lore, String section_name) {
		this(probability_percent, material, amount, name, lore, null, section_name);
	}
	
	/**
	 * Construct the Custom Item,
	 * excluding parents, enchantments and lore.
	 * <p>
	 * @param probability_percent 
	 * @param material the material for the ItemStack.
	 * @param amount the amount for the ItemStack.
	 * @param name the name for the ItemStack.
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, Material material, int amount, String name, String section_name) {
		this(probability_percent, material, amount, name, null, section_name);
	}
	
	/**
	 * Construct the Custom Item,
	 * excluding parents, enchantments and lore,
	 * and using the default item name.
	 * <p>
	 * @param probability_percent 
	 * @param material the material for the ItemStack.
	 * @param amount the amount for the ItemStack.
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, Material material, int amount, String section_name) {
		this(probability_percent, material, amount, null, section_name);
	}

	/**
	 * Construct the Custom Item,
	 * using only an {@link ItemStack}.
	 * <p>
	 * @param probability_percent
	 * @param stack the item stack.
	 * @param section_name
	 */
	public CustomLootItem(int probability_percent, ItemStack stack, String section_name) {
		this(probability_percent, stack.getType(), stack.getAmount(), stack.getItemMeta().getDisplayName(),
				stack.getItemMeta().getLore(), stack.getItemMeta().getEnchants(), section_name);
	}
	
	@Override
	public ItemStack getItemStack() {
		if (material == null) {
			return null;
		}
		
		/* build ItemStack */
		ItemStack stack = ItemStackUtil.setNameLore(new ItemStack(material, amount), name, lore);
		for (Enchantment ench : enchants.keySet()) {
			if (ench != null) {
				ItemStackUtil.addEnchantment(stack, ench, Math.max(enchants.get(ench), 0));
			}
		}
		return stack;
	}

	@Override
	public void give(Player player) {
		player.getInventory().addItem(getItemStack());
		player.updateInventory();
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
		if (amount > 0) {
			this.amount = amount;
		}
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
		if (parent != null && !parents.contains(parent)) {
			parents.add(parent);
		}
	}

	@Override
	public void removeParent(LootItem parent) {
		parents.remove(parent);
	}

	@Override
	public LootType getType() {
		return LootType.CUSTOM;
	}

	@Override
	public String getSectionName() {
		return section_name;
	}
	
	@Override
	public int save(ConfigurationSection section) {
		/* save primary values */
		int save = YamlUtil.setNotEqual( section , LootManager.TYPE_KEY, getType().name()) ? 1 : 0;
		save += YamlUtil.setNotEqual( section , ITEM_MATERIAL_KEY, material.name()) ? 1 : 0;
		save += YamlUtil.setNotEqual( section , ITEM_NAME_KEY, name) ? 1 : 0;
		save += YamlUtil.setNotEqual( section , ITEM_LORE_KEY, lore) ? 1 : 0;
		save += YamlUtil.setNotEqual( section , LootManager.PROBABILITY_PERCENT_KEY, getProbabilityPercent()) ? 1 : 0;
		save += YamlUtil.setNotEqual( section , LootManager.AMOUNT_KEY, getItemAmount()) ? 1 : 0;
		
		/* save enchantments */
		saveEnchantments(this, section.createSection(ITEM_ENCHANTMENTS_SECTION));
		
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
			save += YamlUtil.setNotEqual( section , LootManager.PARENT_KEY , parents_config ) ? 1 : 0;
		}
		return save;
	}
	
	private static Map<Enchantment, Integer> loadEnchantments(ConfigurationSection section) {
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		if (section == null) {
			return enchants;
		}
		
		/* load enchantments from the item enchantments section */
		for (String key : section.getKeys(false)) {
			ConfigurationSection ench_cs = section.getConfigurationSection(key);
			if (ench_cs == null) {
				continue;
			}
			
			Enchantment enchantment = Enchantment.getByName(ench_cs.getString(ITEM_ENCHANTMENT_KEY));
			int               level = ench_cs.getInt(ITEM_ENCHANTMENT_LEVEL_KEY);
			if (enchantment != null) {
				enchants.put(enchantment, Integer.valueOf(level));
			}
		}
		return enchants;
	}
	
	private static void saveEnchantments(CustomLootItem loot_item, ConfigurationSection section) {
		for (int count = 0; count < loot_item.enchants.size(); count++) {
			Enchantment ench = new ArrayList<Enchantment>(loot_item.enchants.keySet()).get(count);
			int        level = Math.max(new ArrayList<Integer>(loot_item.enchants.values()).get(count), 0);
			if (ench == null) {
				continue;
			}
			
			/* save enchantment in a new section inside the enchantments section */
			ConfigurationSection ench_cs = section.createSection( ( ITEM_ENCHANTMENT_KEY + "-" + count ) );
			ench_cs.set ( ITEM_ENCHANTMENT_KEY , StringUtil.translateAlternateColorCodes ( ench.getName ( ) ) );
			ench_cs.set ( ITEM_ENCHANTMENT_LEVEL_KEY , level );
		}
	}
	
	public static CustomLootItem getCustomLootItemExample() {
		Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
		enchants.put(Enchantment.DURABILITY,        1);
		enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 0);
		return new CustomLootItem(10, Material.WOOD_PICKAXE, 1, "CustomPickaxe", 
				Arrays.asList("", "&9This is an example of Custom loot item", "&athat you can use as guide"), 
				enchants, null, "CustomLootItemExample");
	}
	
	@Override
	public boolean isValid() {
		return getItemStack() != null && getProbabilityPercent() > 0 && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName()) && enchants != null && lore != null && parents != null;
	}

	@Override
	public boolean isInitValid() {
		return getItemStack() != null && getItemAmount() > 0
				&& !StringUtils.isBlank(getSectionName()) && enchants != null && lore != null && parents != null;
	}
	
	@Override
	public CustomLootItem clone() {
		try {
			return (CustomLootItem) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}