package com.hotmail.AdrianSR.BattleRoyale.game.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.BattleRoyale.game.ItemGiver;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.manager.MiniMapManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.material.MaterialUtils;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

/**
 * Represents a Battle Royale items on map.
 * <p>
 * @author AdrianSR
 */
public enum BattleItems {
	
	MINI_MAP(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Mini Map", Material.MAP, (byte) 0, new ItemGiver() {
		@SuppressWarnings("deprecation") @Override
		public ItemStack asItemStack(int amount) {
			BattleMap  map = MapsManager.BATTLE_MAP;
			World    world = ( map != null ? map.getWorld() : null );
		    ItemStack item = ItemStackUtil.setName(new ItemStack(Material.MAP, amount), 
		    		StringUtil.defaultString(BattleItems.MINI_MAP.getCustomName(), BattleItems.MINI_MAP.getName()));
		    if (world == null) {
		    	return item;
		    }
		    
		    /* make map view */
	        MapView view = Bukkit.createMap(world);
	        for (MapRenderer mapRenderer : view.getRenderers()) { // clear renderers of the view created by Bukkit
	        	view.removeRenderer(mapRenderer);
	        }
	        
	        /* add custom renderer */
	        view.addRenderer ( MiniMapManager.RENDERER );
	        
	        /* change map id */
	        item.setDurability(view.getId());
			return item;
		}

		@Override
		public void giveToPlayer(final Player player, int amount) {
		    if (LocUtils.isOnBattleMap(player)) { // only the players on the battle map can have this
		        player.getInventory().setItem(8, asItemStack(amount));
		        player.updateInventory();
		    }
		}
	}),
	
//	/**
//	 * The Battle Royale Game Mini Map item.
//	 */
//	GAME_MINI_MAP(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Mini Map", Material.MAP, 1, (byte)0, new ItemGiver() {
//		@Override
//		public ItemStack asItemStack(int amount) {
//			// get battle map.
//			final BattleMap bm = MapsManager.BATTLE_MAP;
//			// get battle world.
//			final World bw = (bm != null ? bm.getWorld() : null);
//	    	// create item.
//		    final ItemStack item = ItemUtils.setName(new ItemStack(Material.MAP, amount), BattleItems.GAME_MINI_MAP.getName());
//		    if (bw != null) {
//		    	// create map view.
//		        MapView newMapView = Bukkit.createMap(bw);
//		        
//		        // remove renders.
//		        for (MapRenderer mapRenderer : newMapView.getRenderers()) {
//		            newMapView.removeRenderer(mapRenderer);
//		        }
//		        
//		        // add new render.
//		        newMapView.addRenderer(new BattleMapRenderer());
//		        
//		        // set durability
//		        item.setDurability(newMapView.getId());
//		    }
//			return item;
//		}
//
//		@Override
//		public void giveToPlayer(final Player player, int amount) {
//		    // check is on battle map.
//		    if (LocUtils.isOnBattleMap(player)) {
//		        // add item and update inventory.
//		        player.getInventory().setItem(8, asItemStack(amount));
//		        player.updateInventory();
//		    }
//		}
//
//		@Override
//		public ItemConfiguration getConfiguration() {
//			return new EmptyItemConfiguration();
//		}}),
//	
//	/**
//	 * The Battle Royale Mini Map item.
//	 */
//	CONFIG_MINI_MAP(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Config Mini Map", Material.MAP, 1, (byte)0, new ItemGiver() {
//		@Override
//		public ItemStack asItemStack(int amount) {
//			// get battle map.
//			final BattleMap bm = MapsManager.BATTLE_MAP;
//			// get battle world.
//			final World bw = (bm != null ? bm.getWorld() : null);
//	    	// create item.
//			final ItemStack item = ItemUtils.setLore(
//					ItemUtils.setName(new ItemStack(Material.MAP, amount), BattleItems.CONFIG_MINI_MAP.getName()),
//					Arrays.asList(
//							"", 
//							Global.THEME_THIRD_COLOR + "This minimap shows you your",
//							Global.THEME_THIRD_COLOR + "current configuration:",
//							"",
//							Global.THEME_THIRD_COLOR + "The" + ChatColor.RED + " red lines " + Global.THEME_THIRD_COLOR + "show the initial border.",
//							Global.THEME_THIRD_COLOR + "The" + ChatColor.WHITE + " white lines " + Global.THEME_THIRD_COLOR + "show all border shrinks.",
//							Global.THEME_THIRD_COLOR + "The" + ChatColor.GREEN + " green lines " + Global.THEME_THIRD_COLOR + "show the path of the players vehicles."));
//			
//		    if (bw != null) {
//		    	// create map view.
//		        MapView newMapView = Bukkit.createMap(bw);
//		        
//		        // remove renders.
//		        for (MapRenderer mapRenderer : newMapView.getRenderers()) {
//		            newMapView.removeRenderer(mapRenderer);
//		        }
//		        
//		        // add new render.
//		        newMapView.addRenderer(new BattleMapConfigRenderer());
//		        
//		        // set durability
//		        item.setDurability(newMapView.getId());
//		    }
//			return item;
//		}
//
//		@Override
//		public void giveToPlayer(final Player player, int amount) {
//		    // check is on battle map.
//		    if (LocUtils.isOnBattleMap(player)) {
//		        // add item and update inventory.
//		        player.getInventory().setItem(8, asItemStack(amount));
//		        player.updateInventory();
//		    }
//		}
//
//		@Override
//		public ItemConfiguration getConfiguration() {
//			return new EmptyItemConfiguration();
//		}}),
	
	/**
	 * Diamond Sword.
	 */
	DIAMOND_SWORD(null, Material.DIAMOND_SWORD, (byte)0, null),
	
	/**
	 * Iron Sword.
	 */
	IRON_SWORD(null, Material.IRON_SWORD, (byte)0, null),
	
	/**
	 * Stone Sword.
	 */
	STONE_SWORD(null, Material.STONE_SWORD, (byte)0, null),
	
	/**
	 * Iron Pickaxe.
	 */
	IRON_PICKAXE(null, Material.IRON_PICKAXE, (byte)0, null),
	
	/**
	 * Stone Pickaxe.
	 */
	STONE_PICKAXE(null, Material.STONE_PICKAXE, (byte)0, new ItemGiver() {
		@Override
		public ItemStack asItemStack(int amount) {
			// add enchantment.
			return ItemStackUtil.addEnchantment(new ItemStack(Material.STONE_PICKAXE, amount), 
					Enchantment.DIG_SPEED, 1);
		}

		@Override
		public void giveToPlayer(Player p, int amount) {
			p.getInventory().addItem(asItemStack(amount));
			p.updateInventory();
		}
	}),
	
	/**
	 * Bow.
	 */
	BOW(null, Material.BOW, (byte)0, null),
	
	/**
	 * Arrows.
	 */
	ARROWS(null, Material.ARROW, (byte)0, null),
	
	/**
	 * Lava Bucket.
	 */
	LAVA_BUCKET(null, Material.LAVA_BUCKET, (byte)0, null),
	
	/**
	 * Water Bucket.
	 */
	WATER_BUCKET(null, Material.WATER_BUCKET, (byte)0, null),
	
	/**
	 * Ender Pearl.
	 */
	ENDER_PEARL(null, Material.ENDER_PEARL, (byte)0, null),
	
	/**
	 * Golden Apple.
	 */
	GOLDEN_APPLE(ChatColor.GOLD + "Golden Apple", Material.GOLDEN_APPLE, (byte)0, null),
	
	/**
	 * Sandstone.
	 */
	SANDSTONE(null, Material.SANDSTONE, (byte)0, null),
	
	/**
	 * The Throwable eggs that create a bridge in his path.
	 */
	BRIDGE_EGG(ChatColor.DARK_GREEN + "Bridge Egg", Material.EGG, (byte)0, null, 
			new SimpleItemConfiguration(ChatColor.DARK_GREEN + "Bridge Egg", 
					ChatColor.YELLOW + "Throw the egg and create a",
					ChatColor.YELLOW + "bridge in its path!")
			.add(new ConfigItem<Integer>("max-path-blocks", 15))),
	
	/**
	 * Throwable Fireball.
	 */
	FIRE_BALL(ChatColor.RED + "Fireball", Material.FIREBALL, (byte)0, null),

	/**
	 * TNT Grende.
	 */
	TNT_GRENADE(ChatColor.WHITE + "TNT", Material.TNT, (byte)0, null),
	
	/**
	 * First Aid.
	 */
	FIRST_AID(ChatColor.DARK_GREEN + "First Aid", Material.POTION, (byte) 8229, new ItemGiver() {
		
		@Override
		public ItemStack asItemStack(int amount) {
			return new ItemStack(Material.POTION, amount, (byte) 8229);
		}

		@Override
		public void giveToPlayer(Player p, int amount) {
			p.getInventory().addItem(asItemStack(amount));
			p.updateInventory();
		}
	}),
	
	/**
	 * Splash Speed II, random duration (15 or 20).
	 */
	SPLASH_SPEED_POTION(null, Material.POTION, (byte) 16386, new ItemGiver() {
		@Override
		public ItemStack asItemStack(int amount) {
			// get random duration. ( 20s --> 40% probability | 15s --> 60% probability )
			final int duration = ( 20 * ( RandomUtils.nextInt(100) < 40 ? 20 : 15 ) );
			
			// get damage.
			short damage = (short) 0;
			damage      |= 0x4000;
			
			// get splash potion.
			final ItemStack potion = new ItemStack(Material.POTION, amount, damage);
			final PotionMeta meta  = (PotionMeta) potion.getItemMeta();
			
			// add custom effect.
			meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1), true);
			potion.setItemMeta(meta);
			return potion;
		}

		@Override
		public void giveToPlayer(Player p, int amount) {
			p.getInventory().addItem(asItemStack(amount));
			p.updateInventory();
		}
	}),
	
	/**
	 * The launch pad creator item.
	 */
	LAUNCH_PAD(ChatColor.DARK_GREEN + "Launch Pad", Material.SLIME_BLOCK, (byte) 0, null, 
			ChatColor.YELLOW + "Place the Launch Pad on the",
			ChatColor.YELLOW + "ground to re-deploy! Only stays",
			ChatColor.YELLOW + "a few seconds."),
	
	/**
	 * The wall creator.
	 */
	SPONGE_WALLS(ChatColor.DARK_GREEN + "Sponge Wall", Material.SPONGE, (byte) 0, null, 
			ChatColor.YELLOW + "Throw the Sponge to instantly",
			ChatColor.YELLOW + "create a wall!",
			ChatColor.YELLOW + "Left click to",
			ChatColor.YELLOW + "throw, right click to drop.");
	;
	
	private static YamlConfigurationComments FILE = null;
	
	public static int saveDefaultConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		int save = 0;
		for (BattleItems item : BattleItems.values()) {
			if (item.getConfiguration().isEmpty()) {
				continue;
			}
			
			for (ConfigItem<?> config_item : item.getConfiguration().getConfigItems()) {
				ConfigurationSection item_section = YamlUtil.createNotExisting(section, item.name());
				if (config_item.isSet(item_section)) {
					continue;
				}
				
				config_item.setDefaults(item_section); save ++;
			}
		}
		return save;
	}
	
	public static final String NAME_KEY = "name";
	public static final String LORE_KEY = "lore";
//	public static final String TYPE_KEY = "type";
	
	/* all the default values */
	private final String                     name;
	private final Material                   type;
	private final byte                       data;
	private final ItemConfiguration configuration;
	private final ItemGiver                 giver;
	
	/**
	 * @param name the Item name.
	 * @param type the Item Material.
	 * @param amount the Item default amount.
	 * @param data the Item data.
	 * @param lore the Item lore.
	 */
	BattleItems(String name, Material type, byte data, ItemGiver giver, ItemConfiguration configuration) {
		this.name          = name;
		this.type          = type;
		this.data          = data;
		this.giver         = giver;
		this.configuration = configuration;
	}
	
	BattleItems(String name, Material type, byte data, ItemGiver giver, String... lore) {
		this.name          = name;
		this.type          = type;
		this.data          = data;
		this.giver         = giver;
		this.configuration = new SimpleItemConfiguration(name, lore);
	}
	
	/**
	 * Set the {@link YamlConfiguration} to use.
	 * 
	 * @param config The config to set.
	 */
	public static void setFile(YamlConfigurationComments config) {
		FILE = config;
	}
	
	/**
	 * @return configuration
	 */
	public ItemConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the loaded name from the configuration.
	 * Note that null will be returned if there is not a valid {@link String} in the configuration.
	 * <p>
	 * @return the loaded name from the configuration, or null.
	 */
	public String getCustomName() {
		return (configuration.has(NAME_KEY) && getConfigurationSection().isString(NAME_KEY)
				? StringUtil.translateAlternateColorCodes(getConfigurationSection().getString(NAME_KEY))
				: null);
	}
	
	/**
	 * Gets the loaded lore from the configuration.
	 * Note that an empty list will be returned if there is not a valid {@link String} list in the configuration.
	 * <p>
	 * @return the loaded lore from the configuration, or an empty list.
	 */
	public List<String> getCustomLore() {
		return configuration.has(LORE_KEY)
				? StringUtil.translateAlternateColorCodes(getConfigurationSection().getStringList(LORE_KEY))
				: new ArrayList<>();
	}
	
	/**
	 * @return the type
	 */
	public Material getType() {
		return type;
	}
	
	/**
	 * Gets the loaded type from the configuration.
	 * Note that null will be returned if there is not a valid {@link Material} in the configuration.
	 * <p>
	 * @return the loaded type from the configuration, or null.
	 */
//	public Material getCustomType() {
//		if (configuration.has(TYPE_KEY)) {
//			try {
//				Material custom = Material.valueOf(getConfigurationSection().getString(TYPE_KEY, ""));
//				if (custom != null) {
//					return MaterialUtils.getRightMaterial(custom);
//				}
//			} catch (Throwable t) {
//				/* ignore */
//			}
//		}
//		return null;
//	}

	/**
	 * @return the amount
	 */
//	public int getAmount() {
//		return amount;
//	}
	
	/**
	 * @return the data
	 */
	public byte getData() {
		return data;
	}
	
	public ConfigurationSection getConfigurationSection() {
		if (FILE != null) {
			if (FILE.isConfigurationSection(name())) {
				return FILE.getConfigurationSection(name());
			} else {
				return FILE.createSection(name());
			}
		}
		return null;
	}
	
	/**
	 * Get as {@link ItemStack} with custom amount.
	 * <p>
	 * @return a ItemStack.
	 */
	public ItemStack asItemStack(int amount) {
		return  giver != null ? giver.asItemStack(amount) // check is with giver.
				: // else return normal.
				ItemStackUtil.setNameLore(new ItemStack(type, amount, data), 
						StringUtil.defaultString(getCustomName(), getName()), 
						getCustomLore()); 
	}
	
	/**
	 * Get as {@link ItemStack}.
	 * <p>
	 * @return a ItemStack.
	 */
	public ItemStack asItemStack() {
		return asItemStack(1);
	}
	
	/**
	 * Give this to a {@link Player}.
	 * <p>
	 * @param p the target player.
	 * @param amount the item amount
	 */
	public void giveToPlayer(final Player p, int amount) {
		if (p == null) {
			return;
		}
		
		if (giver != null) { // call giver.
			giver.giveToPlayer(p, amount); 
		} else { // add directly
			p.getInventory().addItem(asItemStack(amount)); 
			p.updateInventory();
		}
	}
	
	/**
	 * Give this to a {@link Player}.
	 * <p>
	 * @param p the target player.
	 */
	public void giveToPlayer(final Player p) {
		this.giveToPlayer(p, 1);
	}
	
	/**
	 * Check if a {@link ItemStack} is equals this.
	 * <p>
	 * @param stack the ItemStack to check.
	 * @return true if is equals.
	 */
	public boolean isThis(final ItemStack stack) {
		if (stack == null || !MaterialUtils.equals(stack.getType(), type)
				|| !ItemStackUtil.equalsLore(stack, asItemStack())) {
			return false;
		}
		return ItemStackUtil.extractName(asItemStack(), false).equals(ItemStackUtil.extractName(stack, false));
	}

	/**
	 * Returns battle item with 
	 * the giving name.
	 * <p>
	 * @param name the name of the {@link BattleItems}.
	 * @return battle item with the giving name.
	 */
	public static BattleItems fromName(String name) {
		return Arrays.stream(BattleItems.values())
				.filter(item -> item.name().equalsIgnoreCase(name))
				.findAny().orElse(null);
	}
	
	/**
	 * Check if a {@link ItemStack} 
	 * is equals to any BattleItem.
	 * <p>
	 * @param stack the ItemStack to check.
	 * @return true if is equals.
	 */
	public static boolean isBattleItem(final ItemStack stack) {
		return Arrays.stream(BattleItems.values())
				.filter(item -> item.isThis(stack))
				.count() > 0;
	}
}