package com.hotmail.AdrianSR.BattleRoyale.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a Battle Royale Items giver.
 * 
 * @author AdrianSR
 */
public interface ItemGiver {
	
	/**
	 * Get as {@link ItemStack}.
	 * 
	 * @param amount the item amount.
	 * @return a ItemStack.
	 */
	public ItemStack asItemStack(int amount);
	
	/**
	 * Give to player void.
	 * 
	 * @param p the target player.
	 * @param amount the item amount.
	 */
	public void giveToPlayer(final Player p, int amount);
	
	/**
	 * Get item configuration.
	 * <p>
	 * @return the item configuration.
	 */
//	public ItemConfiguration getConfiguration();
}