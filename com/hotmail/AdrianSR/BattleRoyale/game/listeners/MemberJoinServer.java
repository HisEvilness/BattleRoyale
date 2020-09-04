package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;

/**
 * Represents a class that 
 * disallow players to
 * join server while the
 * game is running.
 * <p>
 * @author AdrianSR.
 */
public final class MemberJoinServer implements Listener {
	
	/**
	 * Constrcut new Member 
	 * Join Server listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberJoinServer ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	/**
	 * Detects players
	 * joining the server
	 * while game is running.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoinServer(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		
		// check game is running.
		if (GameManager.isNotRunning()) {
			// check max players.
			if (Bukkit.getOnlinePlayers().size() >= Config.MAX_PLAYERS.getAsInteger()) {
				p.kickPlayer(Lang.SERVER_FULL.getValue(true));
			}
			return;
		}
		
		// check permission.
		if (!p.isOp()) {
			// kick
			p.kickPlayer(Lang.KICK_CANNOT_JOIN_SERVER_MESSAGE.getValue(true));
			return;
		}
		
		// send to random spwan.
		MapsManager.BATTLE_MAP.sendToSpawn(p, true, false);
		
		// set spectator.
		p.setGameMode(GameMode.SPECTATOR);
		
		// remove team and parachute.
		final BRPlayer bp = BRPlayer.getBRPlayer(p);
		if (bp.hasTeam()) {
			bp.getTeam().removeMember(bp);
		}
	}
}