package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.menus.ItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;

/**
 * Represents a map editor menu.
 * 
 * @author AdrianSR
 */
public abstract class MapEditor extends ItemMenu {

	/**
	 * Class values.
	 */
	private final List<UUID> viewers;
	
	/**
	 * Construct a Map Editor Menu.
	 * 
	 * @param name
	 * @param size
	 */
	public MapEditor(String name, Size size) {
		super(name, size);
		viewers = new ArrayList<UUID>();
	}
	
	/**
	 * Add edition option.
	 * 
	 * @param slot the slot in the menu.
	 * @param item the item.
	 * @return this.
	 */
	public ItemMenu addOption(final Integer slot, final MenuItem item) {
		this.setItem(slot, item);
		return this;
	}
	
	/**
	 * Remove edition option.
	 * 
	 * @param slot the slot in the menu.
	 * @return this.
	 */
	public ItemMenu removeOption(final Integer slot) {
		this.setItem(slot, null);
		return this;
	}
	
	@Override
	public void open(final Player p) {
		// add as visor.
		viewers.add(p.getUniqueId());
		
		// super void.
		super.open(p);
		
		// update.
		this.update(p);
	}
	
	public void update() {
		// check decoration.
		checkDecoration();
		
		// super void.
		for (UUID id : viewers) {
			// get player.
			Player p = Bukkit.getPlayer(id);
			
			// check is online.
			if (p != null && p.isOnline()) {
				// update.
				super.update(p);
			}
		}
	}
	
	protected void checkDecoration() {
		// adding decoration.
		for (int x = 0; x < getSize().getSize(); x++) {
			// check item in slot.
			if (getItems()[x] != null) {
				continue;
			}
			
			// add decoration glass.
			setItem(x, new MenuItem("._.", Global.THEME_GLASS_COLOR.getColoredPaneGlass()));
		}
	}
}