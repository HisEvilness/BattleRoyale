package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Movable;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.Parachute;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.server.Version;

public final class MemberKickedByFliying implements Listener {

	/**
	 * Construct listener.
	 * <p>
	 * @param plugin instance.
	 */
	public MemberKickedByFliying ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
		SchedulerUtil.runTaskTimer ( ( ) -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player == null || !player.isOnline()) {
					continue;
				}
				
				for (Movable mov : Movable.MOVABLES) {
					if (!(mov instanceof Parachute)) {
						continue;
					}
					
					Parachute parachute = (Parachute) mov;
					if ((parachute.isOwnerInside() && player.getUniqueId().equals(parachute.getOwner().getUniqueId()))
							|| player.getAllowFlight()
							|| Vehicle.hasVehicle(player)) {
						inject(player);
					}
				}
			}
		}, 0, 0, plugin);
	}
	
	
	/**
	 * Cancell kick.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onKick(final PlayerKickEvent event) {
		// check player is inside his battle royale vehicle.
		final Player player = event.getPlayer();
		if (Vehicle.hasVehicle(player) || BRPlayer.getBRPlayer(player).hasParachute()) {
			// cancell event.
			event.setCancelled(true);
		}
	}
	
	/**
	 * This void takes the player connection and then
	 * change the values of some variables, preventing the
	 * player from being kicked by "flying".
	 */
	private final void inject(Player player) {
		try {
			Object         conn = BukkitReflection.getHandle(player).getClass()
					.getField("playerConnection")
					.get(BukkitReflection.getHandle(player));
			String field_a_name = "B"; // boolean
			String field_b_name = "C"; // integer
			String field_c_name = "D"; // boolean
			String field_d_name = "E"; // integer
			switch(Version.getServerVersion()) {
			case v1_9_R1:
			case v1_9_R2:
			case v1_10_R1:
			case v1_11_R1:
			case v1_12_R1:
			case v1_13_R1:
			case v1_13_R2:
			case v1_14_R1:
				field_a_name = "B";
				field_b_name = "C";
				field_c_name = "D";
				field_d_name = "E";
				break;
			default:
				field_a_name = null;
				field_b_name = null;
				field_c_name = null;
				field_d_name = null;
				break;
			}
			
			for ( String name : new String[] { field_a_name, field_b_name, field_c_name, field_d_name } ) {
				if (name == null) { /* skip non existent variable */
					continue;
				}
				
				Field field = FieldReflection.get(conn.getClass(), name);
				if (field.getType().equals(boolean.class)) {
					field.set(conn, false);
				} else {
					field.set(conn, 0);
				}
			}
		} catch(Throwable t) {
			/* ignore */
		}
	}
}