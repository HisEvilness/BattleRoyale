package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.events.DeathCause;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberDeathEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.StopRunnable;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.gameend.GameEndMaxKills;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.InventoryBackup;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;

/**
 * Represents a class that
 * is listening if a member 
 * death.
 * <p>
 * This class also checks if the
 * member was killed or not, spawns a
 * loot chest if the respawn is disabled,
 * and changes the death message.
 * <p>
 * @author AdrianSR.
 */
public final class MemberDeathListener implements Listener {
	
	/**
	 * Construct a new Death detector listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberDeathListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	/**
	 * Send custom message on player death.
	 */
	@EventHandler ( priority = EventPriority.HIGHEST , ignoreCancelled = true )
	public void onMemberDie ( MemberDeathEvent event) {
		final Member    member = event.getMember();
		final DeathCause cause = event.getCause();
		final Member    killer = event.getKiller();
		
		/* set not knocked */
		member.setKnocked ( false );
		
		/* get message to send */
		String message = Lang.MEMBER_KILLED_MESSAGE.getValue(true);
		if (killer != null) { /* if killed by other player */
			message = message.replace(Lang.KILLER_REPLACEMENT_KEY, killer.getName()); /* replace killer variable */
			
			/* count team kill */
			if (killer.hasTeam()) {
				killer.getTeam().addKill(1, killer); 
				checkMaxKills();
			}
		} else { /* when die for another cause */
			switch(cause) {
			case FALL:
				message = Lang.MEMBER_DEATH_BY_FALL_MESSAGE.getValue(true);
				break;
			case KILLED_BY_EXPLOSION:
				message = Lang.MEMBER_DEATH_BY_EXPLOSION_MESSAGE.getValue(true);
				break;
			case RADIATION:
				message = Lang.MEMBER_DEATH_BY_RADIATION_MESSAGE.getValue(true);
				break;
			case BLEEDING_OUT:
				message = Lang.MEMBER_DEATH_BLEEDING_OUT_MESSAGE.getValue(true);
				break;
			case VOID:
				message = Lang.MEMBER_DEATH_VOID_MESSAGE.getValue(true);
				break;
			case KILLED_BY_PLAYER:
			case KILLED_BY_PROJECTILE:
			case UNKNOWN:
			default:
				message = Lang.MEMBER_DEATH_UNKNOWN_MESSAGE.getValue(true);
				break;
			}
		}
		
		/* spawn loot chest or restore player inventory */
		if (GameManager.getBattleMode().isRespawnEnabled()) { /* restore inventory */
			if (member.getPlayer() != null) {
				InventoryBackup.backup(member.getPlayer());
			}
		} else { /* spawn loot chest */
			GameUtils.spawnDeathLootChest(event.getDrops(), event.getDeathLocation().clone().add(0.0D, 1.0D, 0.0D), true);
		}
		
		event.setMessage(message.replace("%PLAYER%", member.getName()));
		event.getDrops().clear();
		
		// we're prefer to send the death message manually.
		Bukkit.broadcastMessage ( event.getMessage ( ) );
	}
	
	/**
	 * Stops the game if the
	 * number of kills is equals
	 * or bigger than the max
	 * number of kills the battle
	 * mode has.
	 */
	private void checkMaxKills() {
		BattleMode mode = GameManager.getBattleMode();
		if (!BattleModeUtils.isDeterminatedByKills(mode)) {
			return;
		}
		
		if (GameManager.getGameKills() < mode.getMaxKills()) {
			return;
		}

		GameManager.stopGame(new StopRunnable() {
			@Override
			public void stop() { /* stop if the game end event is no cancelled */
				new GameEndMaxKills().Initialize().runTaskTimer(BattleRoyale.getInstance(), 20L, 20L);
			}
		});
	}
}