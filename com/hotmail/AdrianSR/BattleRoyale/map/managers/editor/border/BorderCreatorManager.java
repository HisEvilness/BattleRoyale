package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

/**
 * Represents a Border Creator manager.
 * 
 * @author AdrianSR
 */
public final class BorderCreatorManager extends PluginHandler {
	
	/**
	 * Construct a new Border Creator manager.
	 * 
	 * @param plugin Fornite instance.
	 */
	public BorderCreatorManager ( BattleRoyale plugin ) {
		super ( plugin ); this.register ( );
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onClick(final PlayerInteractEvent eve) {
		// get player.
		final Player       p = eve.getPlayer();
		final ItemStack item = eve.getItem();
		final Location point = eve.getPlayer().getLocation();
		
		// get is on battle map.
		if (!LocUtils.isOnBattleMap(p)) {
			return;
		}
		
		// check item.
		if (!isCreatorItem(item)) {
			return;
		}
		
		// do someting depeding of the action.
		if (eve.getAction().name().startsWith("LEFT_CLICK")) { // check is left or right click.
			BorderSelectorData.doLeftClick(p, point);
		} else {
			BorderSelectorData.doRightClick(p, point);
		}
	}
	
	/**
	 * Detects if the player is creating a border.
	 */
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent eve) {
		// get event vals.
		final Player player  = eve.getPlayer();
//		final UUID id        = player.getUniqueId();
		final ItemStack item = player.getItemInHand();
		
		// check item.
		if (!isCreatorItem(item)) {
			return;
		}
		
		// get is on battle map.
		if (!LocUtils.isOnBattleMap(player)) {
			return;
		}
		
		// cancell
		eve.setCancelled(true);
	}
	
	/**
	 * Selector item cretor and checker.
	 */
	private static final String ITEM_NAME       = Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Shrinking succession creator";
	private static final Material ITEM_MATERIAL = Material.BLAZE_ROD;
	public static ItemStack getBorderCreatorItem() {
		return ItemStackUtil.addSoulbound(ItemStackUtil.setNameLore(new ItemStack(ITEM_MATERIAL), ITEM_NAME, 
				Arrays.asList(new String[] 
		{
			"",
//			ChatColor.GRAY + "Haz click izquierdo para empezar",
//			ChatColor.GRAY + "una nueva sucesion de cerrado",
//			ChatColor.GRAY + "para el Borde.",
//			ChatColor.GRAY + "------------------------------------",
//			ChatColor.GRAY + "Haz click derecho para agregar el",
//			ChatColor.GRAY + "siguiente punto al que se dirigira",
//			ChatColor.GRAY + "el cerrado del Borde."
			
			Global.THEME_THIRD_COLOR + "Left click on a block to start a",
			Global.THEME_THIRD_COLOR + "new border shrinking succession.",
			Global.THEME_THIRD_COLOR + "------------------------------------",
			Global.THEME_THIRD_COLOR + "Right click on a block to add the",
			Global.THEME_THIRD_COLOR + "next border shrinking point."
		})));
	}

	/**
	 * @return true if is the border creator.
	 */
	protected static boolean isCreatorItem(final ItemStack item) {
		return ItemStackUtil.isSoulbound(item) && item.getType() == ITEM_MATERIAL
				&& ItemStackUtil.extractName(item, false).equalsIgnoreCase(ITEM_NAME);
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}