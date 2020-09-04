package com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.adriansr.core.util.itemstack.wool.WoolColor;

public enum ParachuteColor {
	
	BLACK(0),
	
	RED (1),
	
	BLUE(4),
	
	GREEN(10),
	
	YELLOW(11),
	
	WHITE(15);
	
	private final short             color;
	private Permission         permission;
	private boolean permission_registered;
	
	ParachuteColor(int color) {
		this.color      = (short) color;
		this.permission = new Permission("br.parachute.color." + super.name());
	}
	
	public String getScreenName() {
		return Lang.valueOf("PARACHUTE_COLOR_" + super.name() + "_NAME").getValue(true);
	}
	
	public ItemStack getScreenIcon() {
		return WoolColor.valueOf(name()).toItemStack(1);
//		switch(this) {
//		case WHITE:
//			return new ItemStack(Material.WOOL, 1, (short) 0);
//		case BLACK:
//			return new ItemStack(Material.WOOL, 1, (short) 15);
//		case RED:
//			return new ItemStack(Material.WOOL, 1, (short) 14);
//		case BLUE:
//			return new ItemStack(Material.WOOL, 1, (short) 11);
//		case YELLOW:
//			return new ItemStack(Material.WOOL, 1, (short) 4);
//		case GREEN:
//			return new ItemStack(Material.WOOL, 1, (short) 13);
//		default:
//			return new ItemStack(Material.WOOL, 1, color);
//		}
	}
	
	public int getCost() {
		return Money.valueOf(super.name() + "_PARACHUTE_COLOR_COST").getAsNotNullInteger();
	}
	
	public short getValue() {
		return color;
	}
	
	private void registerPermission() {
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
