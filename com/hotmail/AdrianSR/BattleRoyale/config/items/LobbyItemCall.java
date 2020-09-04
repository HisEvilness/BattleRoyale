package com.hotmail.AdrianSR.BattleRoyale.config.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a Lobby Item use interface.
 * <p>
 * @author AdrianSR
 */
public interface LobbyItemCall {

	/**
	 * This void is called when a player use a LobbyItem.
	 * 
	 * @param p the player.
	 * @param used the used item.
	 */
	public void onUse(final Player p, final ItemStack used);
	
	/**
	 * This void is called when a player join the server.
	 * 
	 * @param p the player.
	 */
	public void onJoin(final Player p);
}
