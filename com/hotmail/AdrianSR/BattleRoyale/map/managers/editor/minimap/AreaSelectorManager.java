package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.minimap;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

public final class AreaSelectorManager extends PluginHandler {
	
	public AreaSelectorManager(final BattleRoyale plugin) {
		super(plugin); this.register();
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onClick(final PlayerInteractEvent eve) {
		/* check is clicking block */
		final Block block = eve.getClickedBlock();
		if (block == null) {
			return;
		}
		
		/* get action performed */
		final Action action = eve.getAction();
		
		/* check item in hand */
		final ItemStack item = eve.getItem();
		if (!isSelectorItem(item)) {
			return;
		}
		
		/* get corner vector */
		final Vector3i vector = new Vector3i(block.getX(), block.getY(), block.getZ());
		
		/* get selection and set corners */
		final AreaSelection selection = AreaSelection.getSafeSelection(eve.getPlayer().getUniqueId());
		if (action.name().contains("LEFT")) {
			/* set corner A (using left click) */
			selection.setCornerA(vector);
			
			/* send 'A' set message */
			eve.getPlayer().sendMessage(ChatColor.GOLD + "Corner " + ChatColor.GREEN + "A" + " set!");
		} else {
			/* set corner B (using right click) */
			selection.setCornerB(vector);
			
			/* send 'B' set message */
			eve.getPlayer().sendMessage(ChatColor.GOLD + "Corner " + ChatColor.GREEN + "B" + " set!");
		}
		
		/* stop action */
		eve.setCancelled(true);
	}
	
	/**
	 * Selector item cretor and checker.
	 */ 
	private static final String   ITEM_NAME     = ChatColor.GOLD + ChatColor.BOLD.toString() + "Area Selector";
	private static final Material ITEM_MATERIAL = Material.DIAMOND_AXE;
	public static ItemStack getAreaSelectorItem() {
		return ItemStackUtil.addSoulbound(ItemStackUtil.setNameLore(new ItemStack(ITEM_MATERIAL), ITEM_NAME, 
				Arrays.asList(new String[] 
		{
			"",
			ChatColor.GRAY + "Right click on a block to",
			ChatColor.GRAY + "set the corner A.",
			ChatColor.GRAY + "---------------------------",
			ChatColor.GRAY + "Left click on a block to",
			ChatColor.GRAY + "set the corner B."
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
