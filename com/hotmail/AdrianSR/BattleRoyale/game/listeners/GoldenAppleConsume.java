package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that make
 * custom effects for Golden
 * Apples.
 * 
 * @author AdrianSR.
 */
public final class GoldenAppleConsume implements Listener {
	
	/**
	 * Construct a custom Golden Apple 
	 * effects listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public GoldenAppleConsume ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.HIGHEST , ignoreCancelled = true )
	public void onChange(final PlayerItemConsumeEvent event) {
		// get player and check item.
		final Player p       = event.getPlayer();
		final ItemStack item = event.getItem();
		if (!BattleItems.GOLDEN_APPLE.isThis(item)) {
			return;
		}
		
		// cancell
		event.setCancelled(true);
		
		// consume item.
		if (p.getGameMode() != GameMode.CREATIVE) { // check is not in creative mode.
			// consume.
			if ((item.getAmount() - 1) > 0) {
				p.setItemInHand(BattleItems.GOLDEN_APPLE.asItemStack(item.getAmount() - 1));
			} else {
				p.setItemInHand(null);
			}
			p.updateInventory();
		}
		
		// check is will force.
		boolean force = false;
		if (p.hasPotionEffect(PotionEffectType.REGENERATION)) {
			for (PotionEffect eff : p.getActivePotionEffects()) {
				// check is regeneration.
				if (eff.getType().equals(PotionEffectType.REGENERATION)) {
					// check duration is < 5 seconds.
					if (eff.getDuration() < (20 * 5)) {
						// set will force
						force = true;
					}
				}
			}
		}
		
		// add Regeneration II for 2 seconds..
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (20 * 5), 1), force);
	}
}