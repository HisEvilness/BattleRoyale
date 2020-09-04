package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import com.hotmail.AdrianSR.BattleRoyale.enums.file.BattleRoyaleConfigFileType;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.ClassicLootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.CustomLootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.InitialLootItem;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.LootItem;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemMetaBuilder;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

public final class LootManager extends PluginHandler {
	
	public static LootManager getInstace ( ) {
		return (LootManager) HANDLER_INSTANCES.get ( LootManager.class );
	}
	
	public static final String INITIAL_LOOT_SECTION           = "InitialLoot";
	public static final String TYPE_KEY                       = "type";
	public static final String PROBABILITY_PERCENT_KEY        = "probability-percent";
	public static final String AMOUNT_KEY                     = "amount";
	public static final String PARENT_KEY                     = "parents";
	public static final String PARENT_CONFIG_START_ARG        = "[";
	public static final String PARENT_CONFIG_END_ARG          = "]";
	public static final String PARENT_CONFIG_SPLITER          = ";";
	public static final String PARENT_CONFIG_AMOUNT_START_ARG = "(";
	public static final String PARENT_CONFIG_AMOUNT_ARG       = "amount";
	public static final String PARENT_CONFIG_AMOUNT_END_ARG   = ")";
	public static final String PARENT_CONFIG_AMOUNT_SPLITER   = ":";
	
	public LootManager(BattleRoyale plugin) {
		super(plugin);
		
		/* save default configuration, if and only if the loot configuration file doens't exist */
		File            loot_yml_file = BattleRoyaleConfigFileType.LOOT_ITEMS_CONFIG.getFile();
		YamlConfiguration loot_config = null;
		if (!loot_yml_file.exists() || !loot_yml_file.isFile()) {
			try {
				loot_yml_file.createNewFile();
				loot_config = YamlConfiguration.loadConfiguration(loot_yml_file);
				saveDefaultLootConfiguration(loot_config);
				loot_config.save(loot_yml_file);
			} catch (Exception e) {
				ConsoleUtil.sendPluginMessage(ChatColor.RED,
						String.format("The file '%s' couldn't be loaded correctly: ", loot_yml_file.getName()), plugin);
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(plugin);
				return;
			}
		} else {
			loot_config = YamlConfiguration.loadConfiguration(loot_yml_file);
		}
		
		// after saving defaults, the loot configuration is ready to be loaded.
		for ( LootContainer container : LootContainer.values ( ) ) {
			container.loadLootConfiguration ( loot_config );
		}
		
		// it could looks unnecessary and redundant, but it's necessary, because all the
		// loot containers has to be already loaded before loading parents.
		for ( LootContainer container : LootContainer.values ( ) ) {
			container.loadParentConfiguration ( loot_config );
		}
	}
	
	public void saveDefaultLootConfiguration(ConfigurationSection root) throws IOException {
		/* default initial loot */
		new InitialLootItem(BattleItems.SANDSTONE, 30, "Sandstone")
				.save(root.createSection(LootManager.INITIAL_LOOT_SECTION).createSection("Sandstone"));
		
		/* get default game loot configuration */
		List<LootItem> default_loot = new ArrayList<LootItem>(Arrays.asList(new LootItem[] 
				{
					new ClassicLootItem(6, BattleItems.ARROWS, 10, "Arrows"),
					new ClassicLootItem(5, BattleItems.BOW, 1, Arrays.asList(new ClassicLootItem(0, BattleItems.ARROWS, 5, "Arrows")), "Bow"),
					new ClassicLootItem(8, BattleItems.BRIDGE_EGG, 1, "Bridge Egg"),
					new ClassicLootItem(4, BattleItems.DIAMOND_SWORD, 1, "Diamond Sword"),
					new ClassicLootItem(2, BattleItems.ENDER_PEARL, 1, "Ender Pearl"),
					new ClassicLootItem(5, BattleItems.FIRE_BALL, 1, "Fire Ball"),
					new ClassicLootItem(6, BattleItems.FIRST_AID, 1, "First Aid"),
					new ClassicLootItem(6, BattleItems.GOLDEN_APPLE, 1, "Golden_Apple"),
					new ClassicLootItem(4, BattleItems.IRON_PICKAXE, 1, "Iron Pickaxe"),
					new ClassicLootItem(6, BattleItems.IRON_SWORD, 1, "Iron Sword"),
					new ClassicLootItem(6, BattleItems.LAUNCH_PAD, 1, "Launch Pad"),
					new ClassicLootItem(3, BattleItems.LAVA_BUCKET, 1, "Lava Bucket"),
					new ClassicLootItem(10, BattleItems.SANDSTONE, 1, "Sandstone"),
					new ClassicLootItem(5, BattleItems.SPLASH_SPEED_POTION, 1, "Splash Speed Potion"),
					new ClassicLootItem(5, BattleItems.SPONGE_WALLS, 1, "Sponge Walls"),
					new ClassicLootItem(4, BattleItems.STONE_PICKAXE, 1, "Stone Pickaxe"),
					new ClassicLootItem(4, BattleItems.STONE_SWORD, 1, "Stone Sword"),
					new ClassicLootItem(4, BattleItems.TNT_GRENADE, 1, "TNT Grenade"),
					new ClassicLootItem(7, BattleItems.WATER_BUCKET, 1, "Water Bucket"),
				}));
		
		/* add an example of CustomLootItem */
		default_loot.add(CustomLootItem.getCustomLootItemExample());
		
		/* set default config in yaml */
		for (LootItem loot_item : default_loot) {
			loot_item.save(root.createSection(loot_item.getSectionName()));
		}
		
		/* default air supply loot */
		Arrays.asList(
				new ClassicLootItem(10, BattleItems.FIRST_AID, 5, "X5 First Aid"),
				new CustomLootItem(20, new ItemMetaBuilder(Material.DIAMOND_SWORD).withEnchantment(Enchantment.DAMAGE_ALL, 2).toItemStack(), "Super Diamond Sword"),
				new CustomLootItem(20, new ItemMetaBuilder(Material.BOW).withDisplayName("&6Super Bow").withLore("", "&8Example Super Bow", "&8Unbreakable!").withEnchantment(Enchantment.DURABILITY).toItemStack(), "Super Bow"),
				new CustomLootItem(10, new ItemMetaBuilder(Material.IRON_PICKAXE).withDisplayName("&6Super Pickaxe").withEnchantment(Enchantment.DIG_SPEED, 2).toItemStack(), "Super Pickaxe"),
				new ClassicLootItem(10, BattleItems.FIRST_AID, 3, "X3 Golden Apple"),
				new ClassicLootItem(10, BattleItems.SPLASH_SPEED_POTION, 5, "X5 Speed Potion"),
				new ClassicLootItem(10, BattleItems.FIRE_BALL, 6, "X6 Fire Ball"),
				new ClassicLootItem(10, BattleItems.LAUNCH_PAD, 2, "X2 Launch Pad"))
				.forEach(item -> item.save(
						YamlUtil.createNotExisting(root, LootContainer.AIR_SUPPLY.getConfigurationSectionName())
								.createSection(item.getSectionName())));
	}

	public List<LootItem> decodeParents(String parents_code) {
		final List<LootItem> parents = new ArrayList<LootItem>(); /* make parents list */
		
		/* check parents code start and end and length */
		if (parents_code.length() > 2 && parents_code.startsWith(PARENT_CONFIG_START_ARG)
				&& parents_code.endsWith(PARENT_CONFIG_END_ARG)) {
			/* exclude start and end arguments */
			final String joined_parent_config = excludeFirstLast(parents_code);

			/* read parents */
			final String[] parents_config = joined_parent_config.split(PARENT_CONFIG_SPLITER);
			for (String parent_arg : parents_config) {
				try {
					/* check argument */
					if (StringUtils.isBlank(parent_arg) || !parent_arg.contains(PARENT_CONFIG_AMOUNT_START_ARG)
							|| !parent_arg.contains(PARENT_CONFIG_AMOUNT_END_ARG)) {
						continue;
					}

					/* get parent item name and check */
					int amount_start_index = parent_arg.indexOf(PARENT_CONFIG_AMOUNT_START_ARG);
					String     parent_item = parent_arg.substring(0, amount_start_index).trim();
					if (StringUtils.isBlank(parent_item)) {
						continue;
					}

					/* get parent item amount and check */
					String       parent_item_amount_arg = excludeFirstLast(
							cleanSpaces(parent_arg.substring(amount_start_index, parent_arg.length())));
					int parent_item_amount_number_index = parent_item_amount_arg.indexOf(PARENT_CONFIG_AMOUNT_SPLITER)
							+ 1;
					String parent_item_amount_number = parent_item_amount_arg.substring(parent_item_amount_number_index,
							parent_item_amount_arg.length());
					Integer       parent_item_amount = Integer.valueOf(parent_item_amount_number);
					if (parent_item_amount == null) {
						continue;
					}
					
					/* check parent is already registered */
					for (LootContainer container : LootContainer.values()) {
						for (LootItem li : container.getLoadedLoot()) {
							if (parent_item.equals(li.getSectionName())) {
								/* clone and change item amount */
								LootItem clone = li.clone();
								clone.setItemAmount(parent_item_amount);
								
								/* check is valid */
								if (!clone.isValid()) {
									continue;
								}
								
								/* add to parents */
								parents.add(clone);
								break;
							}
						}
					}
				} catch (Throwable t) {
					/* ignore */
				}
			}
		}
		return parents;
	}
	
	private String excludeFirstLast(String string) {
		return ((string != null && string.length() > 2) ? string.substring(1, (string.length() - 1)) : string);
	}
	
	private String cleanSpaces(String to_clean) {
		return (to_clean != null ? (to_clean.replace(" ", "")) : "");
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}