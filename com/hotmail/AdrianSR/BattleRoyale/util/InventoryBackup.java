package com.hotmail.AdrianSR.BattleRoyale.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryBackup {
	
	/**
	 * Battle Royale backup of members inventories.
	 */
	private static final Map<UUID, InventoryBackup> INVENTORY_BACKUPS = new HashMap<UUID, InventoryBackup>();
	public static InventoryBackup backup(Inventory inventory) {
		return new InventoryBackup(inventory);
	}
	
	public static InventoryBackup backup(Player player) {
		InventoryBackup backup = new InventoryBackup(player.getInventory());
		INVENTORY_BACKUPS.put(player.getUniqueId(), backup);
		return backup;
	}
	
	public static InventoryBackup of(Player player) {
		return INVENTORY_BACKUPS.get(player.getUniqueId());
	}
	
	private final Inventory   handle;
	private final ItemStack[] backup;

	protected InventoryBackup(Inventory inventory) {
		this.handle = inventory;
		this.backup = inventory.getContents();
	}

	public void restore(Inventory inventory, boolean sorted_backup) {
		if (handle == null || inventory == null) {
			return;
		}
		
		if (sorted_backup) {
			for (int x = 0; x < (Math.min(inventory.getSize(), backup.length)); x++) {
				inventory.setItem(x, backup[x]);
			}
		} else {
			Arrays.stream(backup).filter(item -> item != null).forEach(item -> {
				inventory.addItem(item);
			});
		}
	}
}
