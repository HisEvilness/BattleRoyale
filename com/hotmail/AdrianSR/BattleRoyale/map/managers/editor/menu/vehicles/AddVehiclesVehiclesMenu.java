package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.menu.vehicles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehicleType;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.VehiclesPlugin;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.vehicle.vehicles.VVehicle;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.BookItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

import es.pollitoyeye.vehicles.VehiclesMain;
import es.pollitoyeye.vehicles.interfaces.VehicleSubType;

public final class AddVehiclesVehiclesMenu extends BookItemMenu {

	private static List<MenuItem> menuIcons() {
		List<MenuItem> icons = new ArrayList<MenuItem>();
		for (VehicleType type : VehiclesPlugin.VEHICLES.getTypes()) {
			for (VehicleSubType sub_type : VehiclesMain.getPlugin().vehicleSubTypesMap
					.get(es.pollitoyeye.vehicles.enums.VehicleType.valueOf(type.name()))) {
				if (sub_type == null) {
					continue;
				}

				icons.add(new ActionMenuItem((ChatColor.LIGHT_PURPLE + "Add a spawn for the '" + type.name() + "' of the type '"
						+ sub_type.getName() + "'"), event -> {
							/* create and register vehicle */
							VVehicle vels_veh = new VVehicle(type, sub_type.getName());
							vels_veh.setSpawn(new ConfigurableLocation(event.getPlayer().getLocation()));
							MapsManager.BATTLE_MAP.getConfig().addVehicle(vels_veh);

							/* send done */
							event.getPlayer().sendMessage(ChatColor.GREEN + "Vehicle added here!");
							event.setWillClose(true);
						}, new ItemStack(Material.IRON_BARDING)));
			}
		}
		return icons;
	}

	public AddVehiclesVehiclesMenu() {
		super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Vehicles of the plugin 'Vehicles'", menuIcons(),
				false, false);
	}
}