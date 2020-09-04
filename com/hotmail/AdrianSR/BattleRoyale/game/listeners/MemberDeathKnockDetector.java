package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.hotmail.AdrianSR.BattleRoyale.events.DeathCause;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberDeathEvent;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberKnockedEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;

/**
 * Represents a class that
 * detects members death
 * causes, or when a player
 * is knocked.
 * <p>
 * @author AdrianSR.
 */
public final class MemberDeathKnockDetector implements Listener {
	
	/**
	 * Construct a new Death-Knock detector listener.
	 * 
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberDeathKnockDetector ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	/**
	 * When a member is knocked by not another member.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onKnockPlayer(final EntityDamageEvent event) {
		// check entity.
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		// check game state.
		if (GameManager.isNotRunning()) {
			return;
		}
		
		// check battle mode.
		BattleMode mode = GameManager.getBattleMode();
		if (mode.isSolo() || !mode.isReanimationEnabled() || mode.isRespawnEnabled()) {
			return;
		}
		
		// get and check member data.
		final Player     p = (Player) event.getEntity();
		final BRPlayer  bp = BRPlayer.getBRPlayer(p);
		final Location loc = p.getLocation();
		if (bp == null || !bp.hasTeam()) { // check data.
			return;
		}
		
		/* check living members */
		final List<Member> living = bp.getTeam().getLivingMembers(); living.remove(bp);
		if (living.isEmpty()) {
			return;
		}
		
		// check is not damaged by another player.
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			return;
		}
		
		// check next health before the damage.
		final double 	   health =  p.getHealth();
		final double final_health = (health - event.getFinalDamage());
		
		// check is will be knocked or will die.
		if (final_health <= 0.0D) {
			// set knocked if is not. (When is not 'Solo' mode).
			if (!bp.isKnocked()) {
				// set knocked.
				bp.setKnocked (true);
				bp.setKnocker ( null );
				
				// cancell.
				event.setCancelled(true);
				event.setDamage(0.0D);
				
				// change health.
				p.setHealth(20.0D);
				
				// call new Knocked event.
				final MemberKnockedEvent eve = new MemberKnockedEvent(bp, null, loc);
				Bukkit.getPluginManager().callEvent(eve);
			}
		}
	}
	
	/**
	 * When a member is knocked by another member.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onKnockByPlayer(final EntityDamageByEntityEvent event) {
		// check entity.
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		// check is damaged by another player.
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		
		// check game state.
		if (GameManager.isNotRunning()) {
			return;
		}
		
		// check battle mode.
		BattleMode mode = GameManager.getBattleMode();
		if (mode.isSolo() || !mode.isReanimationEnabled() || mode.isRespawnEnabled()) {
			return;
		}
		
		// get and check member data.
		final Player p     = (Player) event.getEntity();
		final BRPlayer bp  = BRPlayer.getBRPlayer(p);
		final Location loc = p.getLocation();
		if (bp == null || !bp.hasTeam()) { // check data.
			return;
		}
		
		/* check living members */
		final List<Member> living = bp.getTeam().getLivingMembers(); living.remove(bp);
		if (living.isEmpty()) {
			return;
		}
		
		// get damager data.
		final Player    d = (Player) event.getDamager();
		final BRPlayer dp = BRPlayer.getBRPlayer(d);
		if (dp == null || !dp.hasTeam()) { // check data.
			return;
		}
		
		// check next health before the damage.
		final double 	   health =  p.getHealth();
		final double final_health = (health - event.getFinalDamage());
		
		// check is will be knocked or will die.
		if (final_health <= 0.0D) {
			// set knocked if is not. (When is not 'Solo' mode).
			if (!bp.isKnocked()) {
				// set knocked.
				bp.setKnocked(true);
				bp.setKnocker ( dp );
				
				// cancell.
				event.setCancelled(true);
				event.setDamage(0.0D);
				
				// change health.
				p.setHealth(20.0D);
				
				// call new Knocked event.
				final MemberKnockedEvent eve = new MemberKnockedEvent(bp, dp, loc);
				Bukkit.getPluginManager().callEvent(eve);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMemberDie(final PlayerDeathEvent event) {
		if (GameManager.isNotRunning()) {
			return;
		}
		
		// get and check member data.
		final Player     p = event.getEntity();
		final BRPlayer  bp = BRPlayer.getBRPlayer(p);
		final Location loc = p.getLocation();
		if (bp == null || !bp.hasTeam()) { // check data.
			return;
		}
		
		// get killer.
		final Player killer = p.getKiller();
		final BRPlayer 	 kp = BRPlayer.getBRPlayer(killer);
		
		// get and check last damage.
		final EntityDamageEvent lastDamage = p.getLastDamageCause();
		if (lastDamage == null) {
			return;
		}
		
		// get last damage cause.
		final DamageCause lastCause = lastDamage.getCause();
		
		// get death cause to call a new MemberKillEvent.
		DeathCause cause = null;
		switch(lastCause) {
		case BLOCK_EXPLOSION:
			cause = DeathCause.KILLED_BY_EXPLOSION;
			break;
		case ENTITY_ATTACK:
			cause = DeathCause.KILLED_BY_PLAYER;
			break;
		case ENTITY_EXPLOSION:
			cause = DeathCause.KILLED_BY_EXPLOSION;
			break;
		case FALL:
			cause = DeathCause.FALL;
			break;
		case PROJECTILE:
			cause = DeathCause.KILLED_BY_PROJECTILE;
			break;
		case VOID:
			cause = DeathCause.VOID;
			break;
		case SUFFOCATION:
			// check is out of world border.
			if (!LocUtils.isInsideOfBorder(p, p.getWorld().getWorldBorder())) {
				cause = DeathCause.RADIATION;
			}
			break;
		case CONTACT:
		case CUSTOM:
		case DROWNING:
		case FALLING_BLOCK:
		case FIRE:
		case FIRE_TICK:
		case LAVA:
		case LIGHTNING:
		case MAGIC:
		case MELTING:
		case POISON:
		case STARVATION:
		case SUICIDE:
		case THORNS:
		case WITHER:
		default:
			cause = DeathCause.UNKNOWN;
			break;
		}
		
		// check cause and call event.
		if (cause != null) {
			/* call member death event */
			final MemberDeathEvent evemt = new MemberDeathEvent(bp, cause, kp, event.getDeathMessage(), loc, event.getDrops());
			Bukkit.getPluginManager().callEvent(evemt);
			
			/* update event modifiers */
//			event.setDeathMessage(evemt.getMessage());
			event.setDeathMessage ( "" );
			event.getDrops().clear();
			if (!evemt.getDrops().isEmpty()) {
				event.getDrops().addAll(evemt.getDrops());
			}
		}
	}
}
