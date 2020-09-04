package com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.vehicle;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.database.DTBPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickHandler;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;

public final class VehicleSelectorMenu extends ItemMenu {
	
	private final DTBPlayer player;
	
	public VehicleSelectorMenu(final DTBPlayer player) {
		super(Lang.VEHICLES_SELECTOR_MENU_NAME.getValue(true), Size.SIX_LINE);
		
		// load player.
		this.player = player;
		
		// add items of selection.
		try {
			for (BRVehicleType vehicle : BRVehicleType.values()) {
				if (player.getPurchasedVehicles().contains(vehicle)
						|| vehicle.hasPermission(player.getMember().getPlayer())) {
					this.addItem(new VehicleSelectionItem(vehicle));
				}
			}
		} catch (Throwable t) {
			// ignore.
		}
		
		// add open shop item.
		this.setItem((this.getSize().getSize() - 1),
				new ActionMenuItem(Lang.VEHICLES_SHOP_ITEM_OPEN.getValue(true), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						new VehicleShopMenu(player).open();
					}
				}, new ItemStack(Material.GOLD_INGOT, 1)));
	}
	
	public void open() {
		if (player.getMember() != null) { /* check DTB player */
			if (player.getMember().isOnline()) {
				this.open(null);
			}
		}
	}

	@Override
	public void open(Player player) {
		if (this.player != null) {
			if (this.player.getMember() != null) {
				super.open(this.player.getMember().getPlayer());
			}
		}
	}
	
	private class VehicleSelectionItem extends MenuItem {
		
		private final BRVehicleType vehicle;

		public VehicleSelectionItem(BRVehicleType vehicle) {
			// super implementation.
			super(Lang.VEHICLES_SELECTOR_ITEM_NAME.getValueReplacingWord(vehicle.getScreenName(), true), vehicle.getScreenIcon());
			
			// load vehicle type.
			this.vehicle = vehicle;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			// get and check battle royale player.
			final BRPlayer br_player = BRPlayer.getBRPlayer(event.getPlayer());
			if (br_player != null && br_player.getDatabasePlayer() != null) {
				// set using vehicle.
				br_player.getDatabasePlayer().setUsingVehicleType(vehicle);
				
				// send vehicle selected message.
				event.getPlayer().sendMessage(Lang.VEHICLES_VEHICLE_SELECTED_MESSAGE.getValue(true));
				
				// update database.
				br_player.getDatabasePlayer().save(false, true, false);
				
				// close menu.
				event.setWillClose(true);
			}
		}
	}
}
