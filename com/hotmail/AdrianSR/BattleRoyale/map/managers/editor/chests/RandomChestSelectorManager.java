package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.chests;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

/**
 * Represents a Random Chest 
 * Selector Manager class.
 * 
 * @author AdrianSR
 */
public class RandomChestSelectorManager extends PluginHandler {
	
	/**
	 * Construct a new Random chest selector.
	 * <p>
	 * @param plugin Fornite instance.
	 */
	public RandomChestSelectorManager(final BattleRoyale plugin) {
		super(plugin); this.register();
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onClick(final PlayerInteractEvent eve) {
		// get and check action.
		final Action action = eve.getAction();
		if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		// get event values.
		final Player       p = eve.getPlayer();
		final ItemStack item = eve.getItem();
		final Block lclicked = eve.getClickedBlock();
		final Block rclicked = lclicked.getRelative(eve.getBlockFace());
		
		// check item.
		if (!isSelectorItem(item)) {
			return;
		}
		
		// get is on battle map.
		if (!LocUtils.isOnBattleMap(p)) {
			return;
		}
		
		// get map.
		final BattleMap map = MapsManager.BATTLE_MAP;
		
		// check action.
		if (action == Action.RIGHT_CLICK_BLOCK) {
			// check is not solid de rigth clicked block.
			if (!rclicked.getType().isSolid()) {
				// check is not already added.
				if (!map.getConfig().isRandomChest(rclicked)) {
					// set to chest.
					rclicked.setType(Material.CHEST);
					
					// add.
					map.getConfig().addRandomChest(new ConfigurableLocation(rclicked.getLocation()));
					
					// send message.
					p.sendMessage(ChatColor.GOLD + "!Random chest added!");
				}
			}
		} else {
			// check is a random chest.
			if (map.getConfig().isRandomChest(lclicked)) {
				// set to air.
				lclicked.setType(Material.AIR);
				
				// remove.
				map.getConfig().removeRandomChest(new ConfigurableLocation(lclicked.getLocation()));
				
				// send message.
				p.sendMessage(ChatColor.GOLD + "!Random chest removed!");
			}
		}
	}
	
	/**
	 * Detects if the player is
	 * breaking a random chest.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent eve) {
		final Player player  = eve.getPlayer();
		if (!LocUtils.isOnBattleMap(player)) {
			return;
		}
		
		/* check is a random chest, and if it's then cancel */
		final BattleMap map = MapsManager.BATTLE_MAP;
		if (map.getConfig().isRandomChest(eve.getBlock().getLocation())) { 
			eve.setCancelled(true);
		}
	}
	
	/**
	 * Selector item cretor and checker.
	 */
	private static final String ITEM_NAME       = ChatColor.GOLD + ChatColor.BOLD.toString() + "Add/Remove Random Chest.";
	private static final Material ITEM_MATERIAL = Material.CHEST;
	public static ItemStack getChestsSelectorItem() {
		return ItemStackUtil.addSoulbound(ItemStackUtil.setNameLore(new ItemStack(ITEM_MATERIAL), ITEM_NAME, 
				Arrays.asList(new String[] 
		{
			"",
//			ChatColor.GRAY + "Haz click derecho sobre un",
//			ChatColor.GRAY + "bloque para agregar un",
//			ChatColor.GRAY + "spawn random de Cofre.",
//			ChatColor.GRAY + "---------------------------",
//			ChatColor.GRAY + "Haz click izquierdo en un",
//			ChatColor.GRAY + "cofre spawn de cofre random",
//			ChatColor.GRAY + "para eliminarlo."
			
			ChatColor.GRAY + "Right click on a block",
			ChatColor.GRAY + "to add a new spawn.",
			ChatColor.GRAY + "---------------------------",
			ChatColor.GRAY + "Left click on a chest to",
			ChatColor.GRAY + "remove."
		})));
	}

	/**
	 * @return true if is the border creator.
	 */
	protected static boolean isSelectorItem(final ItemStack item) {
		return ItemStackUtil.isSoulbound(item) && item.getType() == ITEM_MATERIAL
				&& ItemStackUtil.extractName(item, false).equalsIgnoreCase(ITEM_NAME);
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}