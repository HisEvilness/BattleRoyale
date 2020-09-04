package com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.parachute;

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
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.ParachuteColor;

public final class ParachuteColorSelectorMenu extends ItemMenu {
	
	private final DTBPlayer player;
	
	public ParachuteColorSelectorMenu(final DTBPlayer player) {
		// super implementation.
		super(Lang.PARACHUTE_COLOR_SELECTOR_MENU_NAME.getValue(true), Size.SIX_LINE);
		
		// load player.
		this.player = player;
		
		// add items of selection.
		try {
			for (ParachuteColor vehicle : ParachuteColor.values()) {
				if (player.getPurchasedParachuteColors().contains(vehicle)
						|| vehicle.hasPermission(player.getMember().getPlayer())) {
					this.addItem(new ParachuteColorSelectionItem(vehicle));
				}
			}
		} catch (Throwable t) {
			// ignore.
		}
		
		// add open shop item.
		this.setItem((this.getSize().getSize() - 1),
				new ActionMenuItem(Lang.PARACHUTE_COLOR_SHOP_ITEM_OPEN.getValue(true), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						new ParachuteColorShopMenu(player).open();
					}
				}, new ItemStack(Material.GOLD_INGOT, 1)));
	}
	
	public void open() {
		// check member is online.
		if (player.getMember() != null && player.getMember().isOnline()) {
			// open menu.
			this.open(player.getMember().getPlayer());
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
	
	private class ParachuteColorSelectionItem extends MenuItem {
		
		private final ParachuteColor color;

		public ParachuteColorSelectionItem(ParachuteColor color) {
			super(Lang.PARACHUTE_COLOR_SELECTOR_ITEM_NAME.getValueReplacingWord(color.getScreenName(), true), color.getScreenIcon());
			
			// load color.
			this.color = color;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			// get and check battle royale player.
			final BRPlayer br_player = BRPlayer.getBRPlayer(event.getPlayer());
			if (br_player != null && br_player.getDatabasePlayer() != null) {
				// set using parachute color.
				br_player.getDatabasePlayer().setUsingParachuteColor(color);
				
				// send parachute color selected message.
				event.getPlayer().sendMessage(Lang.PARACHUTE_COLOR_SELECTED_MESSAGE.getValue(true));
				
				// update database.
				br_player.getDatabasePlayer().save(false, true, false);
				
				// close menu.
				event.setWillClose(true);
			}
		}
	}
}
