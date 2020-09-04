package com.hotmail.AdrianSR.BattleRoyale.vehicles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;

public enum BRVehicleType {
	
	CHICKEN(EntityType.CHICKEN, "EntityChicken", Material.EGG),
	BAT(EntityType.BAT, "EntityBat", Material.COAL),
	BLAZE(EntityType.BLAZE, "EntityBlaze", Material.BLAZE_POWDER),
//	DRAGON(EntityType.ENDER_DRAGON, "EntityEnderDragon", Material.COAL_BLOCK),
//	WITHER(EntityType.WITHER, "EntityWither", Material.OBSIDIAN),
	COW(EntityType.COW, "EntityCow", Material.MILK_BUCKET),
	SPIDER(EntityType.SPIDER, "EntitySpider", Material.STRING),
	ENDERMAN(EntityType.ENDERMAN, "EntityEnderman", Material.ENDER_PEARL),
	WOLF(EntityType.WOLF, "EntityWolf", Material.BONE),
	HORSE(EntityType.HORSE, "EntityHorse", Material.APPLE),
	PIG(EntityType.PIG, "EntityPig", Material.FISHING_ROD),
	IRON_GOLEM(EntityType.IRON_GOLEM, "EntityIronGolem", Material.IRON_BLOCK),
	SHEEP(EntityType.SHEEP, "EntitySheep", Material.WOOL),
	CREEPER(EntityType.CREEPER, "EntityCreeper", Material.SULPHUR);
	
	private EntityType        entity_type;
	private Class<?>            nms_class;
	private Material          screen_icon;
	private Permission         permission;
	private boolean permission_registered;
	
	BRVehicleType(EntityType entity_type, String nms_class_name, Material screen_icon) {
		this.entity_type = entity_type;
		this.screen_icon = screen_icon;
		this.permission  = new Permission("br.vehicle." + super.name());
		
		try {
			this.nms_class = ClassReflection.getNmsClass ( nms_class_name );
		} catch ( ClassNotFoundException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	public EntityType getEntityType() {
		return entity_type;
	}
	
	public Class<?> getNmsClass() {
		return nms_class;
	}
	
	public String getScreenName() {
		return ChatColor.stripColor(Lang.valueOf("VEHICLE_" + super.name() + "_SCREEN_NAME").getValue(true));
	}
	
	public int getCost() {
		return Money.valueOf(super.name() + "_VEHICLE_COST").getAsNotNullInteger();
	}
	
	public ItemStack getScreenIcon() {
		return new ItemStack(screen_icon, 1);
	}
	
	public Material getScreenIconMaterial() {
		return screen_icon;
	}
	
	private void registerPermission() {
		// check is not registered.
		if (!permission_registered) {
			// register permission.
			Bukkit.getPluginManager().addPermission(permission);
			
			// recalculate permissibles.
			permission.recalculatePermissibles();
			
			// set registered.
			permission_registered = true;
		}
	}
	
	public boolean hasPermission(final Player player) {
		// check permission registered.
		registerPermission();
		
		// check has permission.
		return player.hasPermission(permission);
	}
}