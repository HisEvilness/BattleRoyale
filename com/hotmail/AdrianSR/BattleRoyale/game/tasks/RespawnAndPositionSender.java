package com.hotmail.AdrianSR.BattleRoyale.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.BattleRoyale.config.items.LobbyItem;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.adriansr.core.util.titles.TitlesUtil;

/**
 * Represents a class that
 * respawn player from
 * his death, change his
 * gamemode to SPECTATOR,
 * teleports it to his death
 * location and send his game position.
 * <p>
 * @author AdrianSR
 */
public class RespawnAndPositionSender extends BukkitRunnable implements Listener {
	
	/**
	 * Class values.
	 */
	private final Member     member;
	private final Location location;
	private final String      title;
	private final String   subtitle;
	
	/**
	 * Construct a new respwan and position sender task.
	 * <p>
	 * @param member   the target member.
	 * @param location the re-spawn location.
	 * @param title    the position title to send.
	 */
	public RespawnAndPositionSender ( Member member , Location location , String title , String subtitle ) {
		this.member   = member;
		this.location = location;
		this.title    = title;
		this.subtitle = subtitle;
		
		Bukkit.getPluginManager ( ).registerEvents ( this , BattleRoyale.getInstance ( ) );
	}
	
	@Override
	public void run ( ) {
		if ( member.isOnline ( ) ) {
			if ( member.isLiving ( ) ) {
				onRespawn ( );
			} else {
				GameUtils.respawnPlayer ( member.getPlayer ( ) );
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn ( PlayerRespawnEvent event ) {
		if ( event.getPlayer ( ).getUniqueId ( ).equals ( member.getUUID ( ) ) ) {
			event.setRespawnLocation ( location );
			onRespawn ( );
		}
	}
	
	protected void onRespawn ( ) {
		Player player = member.getPlayer ( );
		
		member.setKnocked ( false );
		member.getPlayer ( ).setHealth ( member.getPlayer ( ).getMaxHealth ( ) );
		member.setPlayerMode ( BRPlayerMode.SPECTATOR );
		
		player.setGameMode ( GameMode.ADVENTURE );
		player.getInventory ( ).clear ( );
		LobbyItem.GO_TO_LOBBY.getUse ( ).onJoin ( player );
		player.updateInventory ( );
		player.teleport ( location );
		
		TitlesUtil.send ( player , title , subtitle , 8 , 100 , 8 );
		HandlerList.unregisterAll ( this );
	}
}