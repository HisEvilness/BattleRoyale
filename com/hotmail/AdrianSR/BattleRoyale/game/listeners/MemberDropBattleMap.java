package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

public final class MemberDropBattleMap implements Listener {
	
	public MemberDropBattleMap ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onDropMap(final PlayerDropItemEvent event) {
		if (BattleItems.MINI_MAP.isThis(event.getItemDrop().getItemStack())) {
			event.getItemDrop().remove();
			BattleItems.MINI_MAP.giveToPlayer(event.getPlayer());
		}
	}

//	@EventHandler (priority = EventPriority.LOWEST)
//	public void onDropMap(final PlayerDropItemEvent event) {
//		// get item stack.
//		final ItemStack item_drop = event.getItemDrop().getItemStack();
//		
//		// get drop minimap.
//		BattleItems drop_mini_map = null;
//		if (BattleItems.GAME_MINI_MAP.isThis(event.getItemDrop().getItemStack())) {
//			drop_mini_map = BattleItems.GAME_MINI_MAP;
//		} else if (BattleItems.CONFIG_MINI_MAP.isThis(event.getItemDrop().getItemStack())) {
//			drop_mini_map = BattleItems.CONFIG_MINI_MAP;
//		} else {
//			return;
//		}
//		
//		// remove item drop.
//		event.getItemDrop().remove();
//		
//		// give new mini map item stack.
//		event.getPlayer().getInventory().addItem(drop_mini_map.asItemStack());
//		event.getPlayer().updateInventory();
//	}
}