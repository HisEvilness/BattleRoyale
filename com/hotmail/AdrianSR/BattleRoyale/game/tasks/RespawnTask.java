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

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayerMode;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.game.listeners.ParachuteListener;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.InventoryBackup;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.sound.UniversalSound;
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
public class RespawnTask extends BukkitRunnable implements Listener {
	
	/**
	 * Class values.
	 */
	private final Member     member;
	private final Location location;
	private       boolean registered;
	
	/**
	 * Construct a new
	 * respwan and position
	 * sender task.
	 * <p>
	 * @param member the target member.
	 * @param location the respawn location.
	 */
	public RespawnTask(final Member member, final Location location) {
		this.member   = member;
		this.location = location;
	}
	
	@Override
	public void run() {
		if (registered) {
			return;
		}
		
		final Player player = member.getPlayer();
		if (player == null || !player.isOnline()) {
			unregisterListener();
			return;
		}
		
		if (location != null) {
			Bukkit.getPluginManager().registerEvents(this, BattleRoyale.getInstance());
			registered = true;
		}

		// task.
		SchedulerUtil.runTaskLater ( ( ) -> {
			// check craft player.
			if (player != null && player.isOnline()) {
				// send respawn packet.
				try {
					GameUtils.respawnPlayer(player);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}, 2, BattleRoyale.getInstance());
		
		new AutoRespawnTask().runTaskTimer(BattleRoyale.getInstance(), 20L, 20L);
		
		BRPlayer.getBRPlayer(player).setPlayerMode(BRPlayerMode.SPECTATOR);
		player.setGameMode(GameMode.ADVENTURE);
		
		member.setKnocked(false);
	}
	
	private class AutoRespawnTask extends BukkitRunnable {
		private int seconds = 0;
		
		private AutoRespawnTask() {
			this.seconds = GameManager.getBattleMode().getRespawnSeconds();
		}
		
		@Override
		public void run() {
			final Player player = member.getPlayer();
			if (player == null || !player.isOnline()) {
				cancel();
				unregisterListener();
				return;
			}
			
			if (seconds <= 0) {
				respawn(player, BRPlayer.getBRPlayer(player));
				cancel();
				unregisterListener();
				return;
			}
			
//			Titles.sendTitleMessages(player, "", Lang.RESPAWNING_SUBTITLE.getValueReplacingNumber(seconds, true), 8, 30, 8);
			TitlesUtil.send ( player, "", Lang.RESPAWNING_SUBTITLE.getValueReplacingNumber(seconds, true), 8, 30, 8 );
			player.playSound(location, UniversalSound.NOTE_STICKS.asBukkit(), 4.0F, 1.0F);
			seconds --;
		}
		
		private void respawn(Player player, BRPlayer br) {
			player.setGameMode(GameMode.SURVIVAL);
			br.setPlayerMode(BRPlayerMode.PLAYING);
			
			/* start skydiving! */
			player.teleport(location.clone().add(0, 50, 0)); 
			ParachuteListener.openParachute(player);
			
			member.setKnocked ( false );
			player.getInventory().clear();
			
			/* restore inventory */
			InventoryBackup backup = InventoryBackup.of(player);
			if (backup != null) {
				backup.restore(player.getInventory(), true);
			} else {
				BattleItems.MINI_MAP.giveToPlayer(player, 1);
			}
			player.updateInventory();
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(final PlayerRespawnEvent eve) {
		// check is this player.
		if (!eve.getPlayer().getUniqueId().equals(member.getUUID())) {
			return;
		}
		
		// set repawn location.
		eve.setRespawnLocation(location);
		BRPlayer.getBRPlayer ( eve.getPlayer ( ) ).setKnocked ( false );
		
		// unregister listener
		unregisterListener();
	}
	
	private void unregisterListener() {
		if (registered) {
			HandlerList.unregisterAll(this);
		}
	}
}
