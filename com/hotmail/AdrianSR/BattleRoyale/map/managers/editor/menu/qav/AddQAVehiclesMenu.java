package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.menu.qav;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.qav.QAVehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.BookItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

import me.zombie_striker.qg.exp.cars.ExpansionHandler;
import me.zombie_striker.qg.exp.cars.baseclasses.AbstractVehicle;

public final class AddQAVehiclesMenu extends BookItemMenu {

	private static List<MenuItem> menuIcons() {
		List<MenuItem> icons = new ArrayList<MenuItem>();
		for (AbstractVehicle vehicle : ExpansionHandler.abstractVehicleTypes) {
			icons.add(new ActionMenuItem(ChatColor.LIGHT_PURPLE + "Add a spawn for the vehicle '" + ChatColor.BLUE
					+ vehicle.getName() + ChatColor.LIGHT_PURPLE + "' here", event -> {
						/* create and register vehicle */
						QAVehicle qav_veh = new QAVehicle(vehicle.getName());
						qav_veh.setSpawn(new ConfigurableLocation(event.getPlayer().getLocation()));
						MapsManager.BATTLE_MAP.getConfig().addVehicle(qav_veh);
						
						/* send done */
						event.getPlayer().sendMessage(ChatColor.GREEN + "Vehicle added here!");
						event.setWillClose(true);
					}, new ItemStack(Material.IRON_BARDING)));
		}
		return icons;
	}

	public AddQAVehiclesMenu() {
		super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Vehicles of the plugin 'QualityArmoryVehicles'", menuIcons(), false, false);
	}
}