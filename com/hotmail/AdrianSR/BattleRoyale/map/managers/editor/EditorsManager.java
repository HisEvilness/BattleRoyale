package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickHandler;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;

/**
 * Represents a Maps editor manager.
 * 
 * @author AdrianSR
 */
public class EditorsManager extends MapEditor {
	
	/**
	 * Global Map editor menu.
	 */
	private static final EditorsManager EDITOR = new EditorsManager();
	
	/**
	 * Construct a map editor menu.
	 */
	public EditorsManager() {
		super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Maps editor", Size.SIX_LINE);
	}
	
	/**
	 * Set option items.
	 */
	private void build() {
		// Lobby editor parent.
		EDITOR.addOption(21, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Edit lobby", 
				new ItemClickHandler() {
			@Override public void onItemClick(ItemClickEvent event) {
				LobbyEditor.openTo(event.getPlayer());
			}
		}, new ItemStack(Material.IRON_PICKAXE, 1)));
		
		// Battle Maps editor.
		EDITOR.addOption(23, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Load and edit a battle map", new ItemClickHandler ( ) {
			@Override public void onItemClick ( ItemClickEvent event ) {
				BattleMapEditor.openTo ( event.getPlayer ( ) );
			}
		}, new ItemStack ( Material.DIAMOND_PICKAXE , 1 ) ) );
		
		// update.
		update();
	}

	/**
	 * open the maps editor.
	 * 
	 * @param p the target.
	 */
	public static void openTo(final Player p) {
		// check player state.
		if (LocUtils.isOnBattleMap(p)) {
			BattleMapEditor.openTo(p);
			return;
		}
		
		// build.
		EDITOR.build();
		
		// open.
		EDITOR.open(p);
	}
	
	/**
	 * Update the maps editor menu.
	 * 
	 * @param p the target.
	 */
	public static void updateTo(final Player p) {
		// update.
		EDITOR.update(p);
	}
	
	/**
	 * Update menu.
	 */
	public static void refresh() {
		// build.
		EDITOR.build();
		
		// update.
		EDITOR.update();
	}
}