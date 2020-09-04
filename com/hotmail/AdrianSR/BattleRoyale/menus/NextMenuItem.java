package com.hotmail.AdrianSR.BattleRoyale.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NextMenuItem extends StaticMenuItem {
	
	private ItemMenu nextMenu;
	
	public NextMenuItem(ItemMenu nextMenu, String name, Material icon) {
		super(name, new ItemStack(icon != null ? icon : Material.ARROW), new String[] {});
		this.nextMenu = nextMenu;
	}

	public NextMenuItem(ItemMenu nextMenu, Material icon) {
		super("§a§lNext ->", new ItemStack(icon != null ? icon : Material.ARROW), new String[] {});
		this.nextMenu = nextMenu;
	}

	public NextMenuItem(ItemMenu nextMenu, String name) {
		super(name, new ItemStack(Material.ARROW), new String[] {});
		this.nextMenu = nextMenu;
	}

	public NextMenuItem(ItemMenu nextMenu) {
		super("§a§lNext ->", new ItemStack(Material.ARROW), new String[] {});
		this.nextMenu = nextMenu;
	}
	
	/**
	 * @return the nextMenu
	 */
	public ItemMenu getNextMenu() {
		return nextMenu;
	}

	/**
	 * @param nextMenu the nextMenu to set
	 */
	public void setNextMenu(ItemMenu nextMenu) {
		this.nextMenu = nextMenu;
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		if (nextMenu != null)
			nextMenu.open(event.getPlayer());
		else
			event.setWillClose(true);
	}
}
