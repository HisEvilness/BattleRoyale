package com.hotmail.AdrianSR.BattleRoyale.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.AdrianSR.BattleRoyale.database.DTBPlayer;
import com.hotmail.AdrianSR.BattleRoyale.database.StatType;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MoneyManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;

/**
 * Represents a Game Utilities Class.
 * <p>
 * @author AdrianSR
 */
public class GameUtils {
	
	public static final String DEATH_LOOT_CHEST_METADATA_KEY = "DEATH_LOOT_CHEST";
	
	/**
	 * Send message for
	 * online players.
	 * <p>
	 * @param message the message to send.
	 * @param toExclude the player to exclude.
	 */
	public static void sendGlobalMessage(final String message, Player... toExclude) {
		// for online players.
		for (Player p : Bukkit.getOnlinePlayers()) {
			// check is not excluded player.
			if (toExclude != null 
					&& Arrays.asList(toExclude).contains(p)) {
				continue;
			}
			
			if (message == null) {
				break;
			}
			
			/* send */
			if (message != null) {
				p.sendMessage(message);
			}
		}
	}
	
//	/**
//	 * Eject a Player knocked
//	 * seat.
//	 * <p>
//	 * @param p the Player target.
//	 */
//	public static void ejectKnockedSeat ( final Player p ) {
//		BRPlayer member = BRPlayer.getBRPlayer(p); member.setKnocked(false);
//		for (ArmorStand st : p.getWorld().getEntitiesByClass(ArmorStand.class)) {
//			// check is knocked seat.
//			if (st != null && st.hasMetadata(MemberKnockListener.KNOCKED_MEMBERS_SEATS_METADATA)) {
//				// check passenger
//				if (st.getPassenger() != null 
//						&& st.getPassenger().getUniqueId().equals(p.getUniqueId())) {
//					// eject and remove.
//					st.eject();
//					st.remove();
//				}
//			}
//		}
//		p.leaveVehicle();
//	}
	
	/**
	 * Spawn death loot chest.
	 * <p>
	 * @param contents the contents.
	 * @param location the spawn locaiton.
	 * @param excludeBattleItems exclude battle items in contents.
	 */
	public static void spawnDeathLootChest(final List<ItemStack> items, final Location location, boolean excludeBattleMap) {
		if (items.isEmpty()) {
			return;
		}
		
		try {
			// get real contents to add.
			final List<ItemStack> contents = new ArrayList<ItemStack>();
			for (ItemStack stack : items) {
				// check stack.
				if (stack == null || (excludeBattleMap && (BattleItems.MINI_MAP.isThis(stack)))) {
					continue;
				}

				// add stack.
				contents.add(stack);
			}

			// check no emtpy contents.
			if (contents.isEmpty()) {
				return;
			}

			// get block.
			final Block block = location.getBlock();

			// set block as chest.
			block.setType(Material.CHEST);
			block.getState().update(true);
			block.setMetadata ( DEATH_LOOT_CHEST_METADATA_KEY , new FixedMetadataValue ( BattleRoyale.getInstance ( ) , block.getLocation ( ) ) );

			// spawn chest.
			final Chest chest   = (Chest) block.getState();
			final Inventory inv = chest.getBlockInventory();

			// get double chest.
//			final Class<?> double_chest_inventory_class = ReflectionUtils.getCraftBukkitClass("inventory", "CraftInventoryDoubleChest");
//			final Class<?>        craft_inventory_class = ReflectionUtils.getCraftBukkitClass("inventory", "CraftInventory");
			Class<?>       double_chest_inventory_class = null;
			Class<?>              craft_inventory_class = null;
			Constructor<?>    dochest_class_constructor = null;
			try {
				double_chest_inventory_class = ClassReflection.getCraftClass ( "CraftInventoryDoubleChest" , "inventory" );
				craft_inventory_class        = ClassReflection.getCraftClass ( "CraftInventory" , "inventory" );
				dochest_class_constructor    = double_chest_inventory_class.getConstructor(craft_inventory_class, craft_inventory_class);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			Object dochest = null;

			// check contents size. whould be need spawn a double chest.
			if (contents.size() > inv.getSize()) {
				// check 90 block faces.
				for (BlockFace face : DirectionUtil.FACES_90) {
					// get and check relative block.
					Block relative = block.getRelative(face);
					if (!relative.getType().isSolid()) { // if is not solid, spawn there the double chest.
						// set as chest.
						relative.setType(Material.CHEST);
						relative.getState().update(true);

						// get new check inventory.
						Inventory second_inv = ((Chest) relative.getState()).getBlockInventory();

						// get double chest.
						try {
							dochest = dochest_class_constructor.newInstance(inv, second_inv);
						} catch (Throwable t) {
							t.printStackTrace();
						}
						break;
					}
				}
			}

			// get contents array.
			final ItemStack[] array = contents.toArray(new ItemStack[contents.size()]);

			// add items.
			if (dochest == null) {
				inv.addItem(array); // add items to normal inventory.
			} else {
				((Inventory) dochest).addItem(array); // add items to double inventory.
			}
		} catch (ClassCastException e) {
			// ignore.
		}
	}
	
	/**
	 * Respawn player.
	 * <p>
	 * @param player player to respawn.
	 */
	public static void respawnPlayer(final Player player) {
		try {
			// get packet class.
			final Class<?> packet_play_in_client_command_class = ClassReflection
					.getNmsClass("PacketPlayInClientCommand");

			// get enum class.
			final Class<?> enum_client_command_class = packet_play_in_client_command_class.getClasses()[0];

			// get packet class constructor.
			final Constructor<?> packet_constructor = packet_play_in_client_command_class
					.getConstructor(enum_client_command_class);

			// get packet constructor argument.
			final Object constructor_argument = enum_client_command_class.getMethod("valueOf", String.class)
					.invoke(enum_client_command_class, "PERFORM_RESPAWN");

			// make packet.
			final Object packet = packet_constructor.newInstance(constructor_argument);

			// get player handle.
			final Object handle = player.getClass().getMethod("getHandle").invoke(player);

			// get player connection.
			final Object player_connection = handle.getClass().getField("playerConnection").get(handle);

			// get and invoke method "a"
			final Method a = player_connection.getClass().getMethod("a", packet_play_in_client_command_class);

			// invoke method "a".
			a.invoke(player_connection, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Add stat to BRPlayer.
	 * <p>
	 * @param member the member.
	 * @param stat the stat type.
	 */
	public static void addStat(final Member member, final StatType stat, boolean solo) {
		member.getTeam().getMembers().stream().filter(Member :: isOnline).forEach(team_member -> {
			if (( solo ? ( team_member.getUUID().equals(member.getUUID()) ) : true )) {
				if (team_member instanceof BRPlayer) {
					// get database player.
					final DTBPlayer data = ((BRPlayer) team_member).getDatabasePlayer();
					if (data != null) { // check database player
						// add stat.
						data.addStat(stat, 1);

						// update database.
						data.save(true, false, false);
					}

					// deposit reward money.
					// get money to deposit.
					int deposit_amount = 0;
					switch (stat) {
					case WON_GAMES:
						deposit_amount = Money.GAME_WIN_MONEY.getAsNotNullInteger();
						break;
					default:
						break;
					}

					// deposit money.
					if (team_member.isOnline() && deposit_amount > 0) {
						MoneyManager.giveMoney(team_member.getPlayer(), deposit_amount);
					}
				}
			}
		});
	}
}