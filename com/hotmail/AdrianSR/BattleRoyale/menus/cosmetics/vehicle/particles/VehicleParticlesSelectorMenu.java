package com.hotmail.AdrianSR.BattleRoyale.menus.cosmetics.vehicle.particles;

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
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleParticle;

public final class VehicleParticlesSelectorMenu extends ItemMenu {
	
	private final DTBPlayer player;
	
	public VehicleParticlesSelectorMenu(final DTBPlayer player) {
		// super implementation.
		super(Lang.VEHICLE_PARTICLES_SELECTOR_MENU_NAME.getValue(true), Size.SIX_LINE);
		
		// load player.
		this.player = player;
		
		// add items of selection.
		try {
			for (BRVehicleParticle vehicle : BRVehicleParticle.values()) {
				if (player.getPurchasedVehicleParticles().contains(vehicle)
						|| vehicle.hasPermission(player.getMember().getPlayer())) {
					this.addItem(new VehicleParticleSelectionItem(vehicle));
				}
			}
		} catch (Throwable t) {
			// ignore.
		}
		
		// add open shop item.
		this.setItem((this.getSize().getSize() - 1),
				new ActionMenuItem(Lang.VEHICLE_PARTICLES_SHOP_ITEM_OPEN.getValue(true), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						new VehicleParticlesShopMenu(player).open();
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
	
	private class VehicleParticleSelectionItem extends MenuItem {
		
		private final BRVehicleParticle particle;

		public VehicleParticleSelectionItem(BRVehicleParticle particle) {
			// super implementation.
			super(Lang.VEHICLE_PARTICLES_SELECTOR_ITEM_NAME.getValueReplacingWord(particle.getScreenName(), true), particle.getScreenIcon());
			
			// load vehicle particle.
			this.particle = particle;
		}
		
		@Override
		public void onItemClick(ItemClickEvent event) {
			// get and check battle royale player.
			final BRPlayer br_player = BRPlayer.getBRPlayer(event.getPlayer());
			if (br_player != null && br_player.getDatabasePlayer() != null) {
				// set using vehicle particles.
				br_player.getDatabasePlayer().setUsingVehicleParticle(particle);
				
				// send vehicle selected message.
				event.getPlayer().sendMessage(Lang.VEHICLE_PARTICLES_SELECTED_MESSAGE.getValue(true));
				
				// update database.
				br_player.getDatabasePlayer().save(false, true, false);
				
				// close menu.
				event.setWillClose(true);
			}
		}
	}
}
