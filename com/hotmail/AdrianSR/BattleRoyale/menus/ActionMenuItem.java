package com.hotmail.AdrianSR.BattleRoyale.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ActionMenuItem extends MenuItem {
	
	private ItemClickHandler handler;

	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon, String... lore) {
		super(displayName, icon, lore);
		this.handler = handler;
	}
	
	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon, List<String> lore) {
		super(displayName, icon, lore.toArray(new String[lore.size()]));
		this.handler = handler;
	}
	
	public ActionMenuItem(String displayName, ItemClickHandler handler, ItemStack icon) {
		this(displayName, handler, icon, new String[] {});
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		handler.onItemClick(event);
	}

	public ItemClickHandler getHandler() {
		return handler;
	}

	public void setHandler(ItemClickHandler handler) {
		this.handler = handler;
	}
}
