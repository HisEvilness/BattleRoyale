package com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.vehicle;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.database.DTBPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MoneyManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;

import net.milkbowl.vault.economy.EconomyResponse;

public final class VehicleShopMenu extends ItemMenu {
	
	private final DTBPlayer player;
	private       boolean can_open;

	public VehicleShopMenu(final DTBPlayer player) {
		// super implementation.
		super(Lang.VEHICLES_SHOP_MENU_NAME.getValue(true), Size.SIX_LINE);
		
		// load player.
		this.player = player;
		
		// add items of selection.
		try {
			for (BRVehicleType vehicle : BRVehicleType.values()) {
				// check is not already purchased.
				if (!player.getPurchasedVehicles().contains(vehicle) && !vehicle.hasPermission(player.getMember().getPlayer())) {
					// register item.
					this.addItem(new VehicleShopItem(vehicle));
					
					// set can open this menu.
					this.can_open = true;
				}
			}
		} catch(Throwable t) {
			// ignore.
		}
	}
	
	public void open() {
		// check member is online.
		if (player.getMember() != null && player.getMember().isOnline()) {
			// check can open this menu.
			if (this.can_open) {
				// open menu.
				this.open(player.getMember().getPlayer());
			} else {
				player.getMember().getPlayer().sendMessage(Lang.VEHICLES_SHOP_CANNOT_OPEN.getValue(true));
			}
		}
	}

	@Override
	public void open(Player player) {
		// check DTB player.
		if (this.player != null && this.player.getMember() != null) {
			// open menu.
			super.open(this.player.getMember().getPlayer());
		}
	}

	private class VehicleShopItem extends MenuItem {
		
		private final BRVehicleType vehicle;

		public VehicleShopItem(BRVehicleType vehicle) {
			// super implementation.
			super(Lang.VEHICLES_SHOP_ITEM_NAME.getValueReplacingWord(vehicle.getScreenName(), true), vehicle.getScreenIcon());
			
			// load vehicle type.
			this.vehicle = vehicle;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			// get and check battle royale player.
			final BRPlayer br_player = BRPlayer.getBRPlayer(event.getPlayer());
			if (br_player != null && br_player.getDatabasePlayer() != null) {
				// check money system.
				if (!MoneyManager.systemEnabled()) {
					// send not available money system message.
					event.getPlayer().sendMessage(Lang.SHOPS_PURCHASING_SYSTEM_NOT_AVAILABLE_MESSAGE.getValue(true));
					
					// print money system of message.
					ConsoleUtil.sendPluginMessage ( ChatColor.RED,
							"The money system is not enabled, your users cannot use cosmetics shops.",
							BattleRoyale.getInstance());
					return;
				}
				
				// check money.
				final int balance = (int) MoneyManager.getBalance(event.getPlayer());
				if (balance < vehicle.getCost()) {
					event.getPlayer().sendMessage(Lang.SHOPS_NOT_ENOUGH_MONEY_MESSAGE.getValue(true));
					return;
				}
				
				// PURCHASE:
				// discount money.
				final EconomyResponse transaction_reponse = MoneyManager.removeMoney(event.getPlayer(), vehicle.getCost());
				if (transaction_reponse == null || !transaction_reponse.transactionSuccess()) {
					return;
				}
				
				// set using vehicle.
				br_player.getDatabasePlayer().addPurchasedVehicle(vehicle);
				
				// send vehicle selected message.
				event.getPlayer().sendMessage(Lang.VEHICLES_PURCHASED_MESSAGE.getValue(true));
				
				// update database.
				br_player.getDatabasePlayer().save(false, false, true);
				
				// close menu.
				event.setWillClose(true);
			}
		}
	}
}
