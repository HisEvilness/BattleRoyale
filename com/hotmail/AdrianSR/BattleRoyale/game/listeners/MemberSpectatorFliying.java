package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a class that
 * allow spectators to fly,
 * and avoid players to be kicked
 * when fly.
 * <p>
 * @author AdrianSR.
 */
public final class MemberSpectatorFliying implements Listener {
	
	/**
	 * Construct listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberSpectatorFliying ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onJump(final PlayerMoveEvent event) {
		Player player = event.getPlayer();
		BRPlayer   bp = BRPlayer.getBRPlayer(player);
		if (bp.getPlayerMode() != BRPlayerMode.SPECTATOR) {
			return;
		}
		
		/* allow fly */
		if (!player.getAllowFlight() || !player.isFlying()) {
			player.setAllowFlight(true);
		}
	}
}