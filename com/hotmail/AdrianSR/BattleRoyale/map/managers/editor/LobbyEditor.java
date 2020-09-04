package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.map.lobbymap.LobbyMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickHandler;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

/**
 * Represents a lobby editor menu.
 * 
 * @author AdrianSR
 */
public class LobbyEditor extends MapEditor {

	/**
	 * Global Lobby Map editor menu.
	 */
	private static final LobbyEditor EDITOR = new LobbyEditor();
	
	/**
	 * Construct a lobby map editor.
	 */
	public LobbyEditor() {
		super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Edit lobby", Size.SIX_LINE);
	}
	
	/**
	 * Set option items.
	 */
	private void build() {
		// Set spawn option.
		EDITOR.addOption(21, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Set spawn for the players on the lobby.", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				// get location.
				final Player p     = event.getPlayer();
				final Location loc = p.getLocation();
				
				// check lobby.
				if (MapsManager.LOBBY_MAP == null) {
					MapsManager.setLobbyMap(new LobbyMap(p.getWorld().getWorldFolder().getName()));
				}
				
				// set spawn.
				MapsManager.LOBBY_MAP.setSpawn ( new ConfigurableLocation ( loc ) );
				
				// send setted messeg.
				p.sendMessage(ChatColor.GREEN + "Lobby Spawn set here!");
			}
		}, new ItemStack(Material.BED, 1)));
		
		// Save to config option.
		EDITOR.addOption(23, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Save Configuration.", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				// get player.
				final Player p = event.getPlayer();
				
				// check lobby.
				if (MapsManager.LOBBY_MAP == null) {
//					p.sendMessage(ChatColor.RED + "No hay ninguna configuration echa para guardar!");
					p.sendMessage(ChatColor.RED + "!There are not any lobby configurations to save!");
					return;
				}
				
				// save configuration.
				if (MapsManager.LOBBY_MAP.saveToConfig()) {
					// send saved message.
					p.sendMessage(ChatColor.GREEN + "Configuration saved!");
				} else {
					// send could not save message.
					p.sendMessage(ChatColor.RED + "!The configuration could not be saved. Check the console!");
				}
			}
		}, new ItemStack(Material.PAPER)));
		
		EDITOR.addOption(EDITOR.getSize().getSize() - 1, new ActionMenuItem(ChatColor.RED + "Go back", new ItemClickHandler() {
			@Override
			public void onItemClick(ItemClickEvent event) {
				// open main menu.
				EditorsManager.openTo(event.getPlayer());
			}
		}, new ItemStack(Material.ARROW, 1),
				Arrays.asList(new String[] {})));
		
		// update.
		update();
	}

	/**
	 * open the maps editor.
	 * 
	 * @param p the target.
	 */
	public static void openTo(final Player p) {
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
