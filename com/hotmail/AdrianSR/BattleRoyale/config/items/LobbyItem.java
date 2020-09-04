package com.hotmail.AdrianSR.BattleRoyale.config.items;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.parachute.ParachuteColorSelectorMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.vehicle.VehicleSelectorMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.vehicle.particles.VehicleParticlesSelectorMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.team.TeamSelectorMenu;
import com.hotmail.AdrianSR.BattleRoyale.util.channel.BRPluginChannel;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;
import com.hotmail.adriansr.core.util.material.MaterialUtils;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;

/**
 * Represents a lobby items enum.
 * <p>
 * @author AdrianSR
 */
public enum LobbyItem {
	
	TEAM_SELECTOR(Material.DIAMOND_SWORD, ChatColor.BLUE + "Join a Team", new String[0], new LobbyItemCall() {
		@Override
		public void onUse(Player p, ItemStack used) {
			if (!GameManager.getBattleMode().isSolo()) {
				new TeamSelectorMenu().open(p);
			}
		}

		@Override
		public void onJoin(Player p) {
			if (!GameManager.getBattleMode().isSolo()) {
				p.getInventory().setItem(0, LobbyItem.TEAM_SELECTOR.getItemStack());
				p.updateInventory();
			}
		}
	}),
	
	GO_TO_LOBBY ( Material.WATCH , 
			ChatColor.RED + "Go back to lobby", new String[0], new LobbyItemCall() {
		@Override
		public void onUse(Player p, ItemStack used) {
			/* send player to server especified in config */
			SchedulerUtil.runTaskLater(() -> {
				BRPluginChannel.getInstance().sendPlayer(p, Config.LOBBY_ITEM_BUNGEE_SERVER_TARGET.toString());
			}, 20, BattleRoyale.getInstance());
		}

		@Override
		public void onJoin(Player p) {
			p.getInventory().setItem(8, LobbyItem.GO_TO_LOBBY.getItemStack());
			p.updateInventory();
		}}),
	
	VEHICLES_SELECTOR_SHOP_ITEM(Material.MINECART, ChatColor.BLUE + "Select/Buy a fliying vehicle", new String[] {}, new LobbyItemCall() {
		
		@Override
		public void onUse(Player p, ItemStack used) {
			// open vehicle selector/shop menu.
			new VehicleSelectorMenu(BRPlayer.getBRPlayer(p).getDatabasePlayer()).open();
		}

		@Override
		public void onJoin(Player p) {
			// add to inventory.
			p.getInventory().addItem(LobbyItem.VEHICLES_SELECTOR_SHOP_ITEM.getItemStack());
			p.updateInventory();
		}
	}),
	
	VEHICLE_PARTICLES_SELECTOR_SHOP_ITEM(Material.BLAZE_POWDER, ChatColor.BLUE + "Select/Buy a particles for your vehicle", new String[] {}, new LobbyItemCall() {
		
		@Override
		public void onUse(Player p, ItemStack used) {
			// open vehicle particles selector/shop menu.
			new VehicleParticlesSelectorMenu(BRPlayer.getBRPlayer(p).getDatabasePlayer()).open();
		}

		@Override
		public void onJoin(Player p) {
			// add to inventory.
			p.getInventory().addItem(LobbyItem.VEHICLE_PARTICLES_SELECTOR_SHOP_ITEM.getItemStack());
			p.updateInventory();
		}
	}),
	
	PARACHUTE_COLOR_SELECTOR_SHOP_ITEM(Material.WOOL, ChatColor.BLUE + "Select/Buy a parachute color", new String[] {}, new LobbyItemCall() {
		
		@Override
		public void onUse(Player p, ItemStack used) {
			// open parachute color selector/shop menu.
			new ParachuteColorSelectorMenu(BRPlayer.getBRPlayer(p).getDatabasePlayer()).open();
		}

		@Override
		public void onJoin(Player p) {
			p.getInventory().addItem(LobbyItem.PARACHUTE_COLOR_SELECTOR_SHOP_ITEM.getItemStack());
			p.updateInventory();
		}
	}),
	;
	
	public static int saveDefaultConfiguration(ConfigurationSection section) {
		Validate.notNull(section, "The configuration cannot be null!");
		Validate.notNull(section.getRoot(), "The root of the configuration section cannot be null!");
		
		List<LobbyItem> save = Arrays.asList(LobbyItem.values()).stream()
				.filter(item -> !item.existsInConfig(section))
				.collect(Collectors.toList());
		save.forEach(item -> item.setDefaults(section));
		return save.size();
	}
	
	private static YamlConfigurationComments FILE = null;
	
	/**
	 * Set the {@link YamlConfiguration} to use.
	 * 
	 * @param config The config to set.
	 */
	public static void setFile(YamlConfigurationComments config) {
		FILE = config;
	}

	private final Material material;
	private final String       name;
	private final String       path;
	private final String[]     lore;
	private final LobbyItemCall use;
	
	/**
	 * Construct a new Lobby Item.
	 * 
	 * @param mat the default item material.
	 * @param name the default item name.
	 * @param lore the default item lore.
	 */
	LobbyItem(Material material, String name, String[] lore, final LobbyItemCall use) {
		this.material = MaterialUtils.getRightMaterial(material);
		this.name     = name;
		this.path     = name().toLowerCase().replace("_", "-");
		this.lore     = lore;
		this.use      = use;
	}
	
	public LobbyItemCall getUse() {
		return use;
	}
	
	/**
	 * Get material.
	 * 
	 * @return the item material.
	 */
	public Material getMaterial() {
		Material loaded = null;
		try { // get loaded material from config, and return loaded or default.
			loaded = MaterialUtils.getRightMaterial(Material.valueOf(FILE.getString(path + ".Material")));
		} catch(Throwable t) {
			loaded = material;
		}
		return loaded;
	}
	
	/**
	 * Get default {@link Material}.
	 * <p>
	 * @return the default material name.
	 */
	public String getDefaultMaterial() {
		return material.name();
	}
	
	/**
	 * Get name.
	 * <p>
	 * @return the item name.
	 */
	public String getName() {
		// get loaded name from config., and return loaded or default.
		final String loaded = FILE.getString(path + ".Name");
		return StringUtil.translateAlternateColorCodes ( StringUtil.defaultString ( loaded , name ) );
	}
	
	/**
	 * Get the default item name.
	 * <p>
	 * @return default item name untranslated.
	 */
	public String getDefaultName ( ) {
		return StringUtil.untranslateAlternateColorCodes ( name );
	}
	
	/**
	 * Get lore.
	 * <p>
	 * @return the item lore.
	 */
	public String[] getLore ( ) { /* load lore and add colors */
		List < String > loaded = StringUtil.translateAlternateColorCodes ( FILE.getStringList ( path + ".Lore" ) );
		return ( loaded.isEmpty ( ) ? ( lore != null ? lore : new String [ 0 ] ) 
				: loaded.toArray ( new String [ loaded.size ( ) ] ) );
	}
	
	public boolean isEnabled() {
		if (FILE.isBoolean(path + ".Enabled")) {
			return FILE.getBoolean(path + ".Enabled");
		}
		return true;
	}
	
	/**
	 * Get lore as list.
	 * <p>
	 * @return the item lore as list.
	 */
	public List<String> getLoreAsList() {
		return Arrays.asList(getLore());
	}
	
	/**
	 * Get lore as list untranslated.
	 * <p>
	 * @return the item lore as list, untranslated.
	 */
	public List < String > getDefaultLore ( ) {
		return StringUtil.untranslateAlternateColorCodes ( Arrays.asList ( lore != null ? lore : new String [ 0 ] ) );
//		final List < String > list = Arrays.asList ( lore != null ? lore : new String [ 0 ] );
//		
//		// untranslate lore lines.
//		for (int x = 0; x < list.size(); x++) {
//			// untranslate.
//			list.set(x, TextUtils.untranslateColors(list.get(x)));
//		}
//		return list;
	}
	
	/**
	 * Get a Item Lobby as {@link ItemStack}.
	 * <p>
	 * @return the Item Lobby ItemStack.
	 */
	public ItemStack getItemStack ( ) {
		return ItemStackUtil.setNameLore ( new ItemStack ( getMaterial ( ) , 1 ) , 
				getName ( ) , getLoreAsList ( ) );
	}
	
	/**
	 * Check if a {@link ItemStack} is equals this.
	 * <p>
	 * @param stack the ItemStack to check.
	 * @return true if is equals.
	 */
	public boolean equals(final ItemStack stack) {
		return stack != null
				? (MaterialUtils.getRightMaterial(stack) == getMaterial()
						&& ItemStackUtil.extractName(stack, false).equals(getName())
						&& ItemStackUtil.equalsLore(getItemStack(), stack))
				: false;
	}
	
	/**
	 * Check if this ItemLobby configuration, exists in the config file.
	 * <p>
	 * @param yml the Yaml Configuration to check if exists in.
	 * @return true if exists in.
	 */
	public boolean existsInConfig(final ConfigurationSection section) {
		return section.isConfigurationSection(path);
	}
	
	/**
	 * Set default in config.
	 * <p>
	 * @param sc the {@link ConfigurationSection} to set.
	 */
	public void setDefaults(final ConfigurationSection section) {
		final ConfigurationSection sc = section.createSection(path);
		
		// set defaults.
		sc.set("Material", getDefaultMaterial());
		sc.set("Name",     getDefaultName());
		sc.set("Lore",     getDefaultLore());
		sc.set("Enabled",  true);
	}
	
	/**
	 * Check if a {@link ItemStack} is equals to a LobbyItem stack.
	 * <p>
	 * @param stack the ItemStack to check.
	 * @return true if is a LobbyItem.
	 */
	public static boolean isLobbyItem(final ItemStack stack) {
		return getLobbyItem(stack) != null;
	}
	
	/**
	 * Get a lobby item from his {@link ItemStack}.
	 * <p>
	 * @param stack the LobbyItem ItemStack.
	 * @return the LobbyItem from his ItemStack.
	 */
	public static LobbyItem getLobbyItem(final ItemStack stack) {
		for (LobbyItem item : LobbyItem.values()) {
			if (item.equals(stack)) {
				return item;
			}
		}
		return null;
	}
}