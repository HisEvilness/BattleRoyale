package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.database.StatType;
import com.hotmail.AdrianSR.BattleRoyale.events.DeathCause;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberDeathEvent;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberKnockedEvent;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.RespawnAndPositionSender;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.RespawnTask;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.itemstack.ItemStackUtil;

/**
 * Represents a class that 
 * Count the members position,
 * when death or when them leaves the server.
 * <p>
 * @author AdrianSR.
 */
public final class MemberPositionCounter implements Listener {

	/**
	 * Construct a new Member
	 * position counter.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberPositionCounter ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onDeath ( final MemberDeathEvent eve ) {
		final Member member = eve.getMember();
		final Member killer = eve.getKiller();
		final Location loc  = eve.getDeathLocation();
		if (member == null || !member.isOnline() || !member.hasTeam()) {
			return;
		}
		
		Team	 			      team = member.getTeam();
		Team 			   killer_team = ( killer != null ? killer.getTeam() : null );
		final int 		 	  position = ( nextPosition ( ) + 1 );
		final int killer_team_position = nextTeamPosition ( );
		
//		System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> position = " + position );
//		System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> killer_team_position = " + position );
		
		boolean       			  solo = GameManager.getBattleMode().isSolo();
		if (!GameManager.getBattleMode().isRespawnEnabled()) {
			if (Bukkit.getOnlinePlayers().size() == 1) { /* when there is only one player online */
				ConsoleUtil.sendPluginMessage(ChatColor.RED,
						"[WARNING] There is only one player connected, the winner/loser could not be determined correctly!",
						BattleRoyale.getInstance());
				GameManager.stopGame();
			}

			/* when the member was killed by another member, and the killer/killer team is the last living then the killer is the winner */
			if ( (killer_team_position == 1) && ( (killer != null && killer.hasTeam()) || team.isDead() ) ) {
				
				/* game won title */
				for (Player online_player : Bukkit.getOnlinePlayers()) {
					Member team_member = BRPlayer.getBRPlayer(online_player);
					if ( !( killer_team != null ? killer_team : BRTeam.getLivingTeams().get(0) ).equals(team_member.getTeam()) ) {
						continue;
					}
					
					new RespawnAndPositionSender ( team_member , loc ,
							( solo ? Lang.MEMBER_SOLO_WINNER_POSITION_TITLE : Lang.MEMBER_TEAM_WINNER_POSITION_TITLE )
									.getValueReplacingNumber ( 1 /* we are making sure #1 is displayed */ , true ),
							Lang.POSITIONS_SUBTITLE.getValue ( true ) ).runTaskLater ( BattleRoyale.getInstance ( ) , 2L );
					
					/* add game won stat */
					GameUtils.addStat(team_member, StatType.WON_GAMES, true);
				}
				GameManager.stopGame();
			}
			
			/* game over title */
			new RespawnAndPositionSender(
					member, 
					loc, 
					Lang.MEMBER_SOLO_TEAM_POSITION_TITLE.getValueReplacingNumber(position, true),
					Lang.POSITIONS_SUBTITLE.getValue(true)).runTaskLater(BattleRoyale.getInstance(), 2L);
			
			/* add game lost stat if the member team is dead */
			if (team.isDead()) {
				GameUtils.addStat(member, StatType.LOST_GAMES, solo);
			}
		} else { /* respawn member */
			new RespawnTask(member, loc).runTaskLater(BattleRoyale.getInstance(), 2L);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDisconnect(final PlayerQuitEvent eve) {
		if (GameManager.isNotRunning()) {
			return;
		}
		
		List<Team> online_teams = onlineTeams(eve.getPlayer());
		if (online_teams.size() > 1) {
			return;
		}
		
		GameManager.stopGame();
		
		/* game won title */
		if (online_teams.size() == 1) {
			Team last = online_teams.get(0);
			for (Player online_player : Bukkit.getOnlinePlayers()) {
				Member team_member = BRPlayer.getBRPlayer(online_player);
				if (!last.equals(team_member.getTeam())) {
					continue;
				}
				
				new RespawnAndPositionSender(
						team_member, 
						team_member.getPlayer().getLocation(), 
						( GameManager.getBattleMode().isSolo() ? Lang.MEMBER_SOLO_WINNER_POSITION_TITLE 
								: Lang.MEMBER_TEAM_WINNER_POSITION_TITLE ).getValueReplacingNumber(1, true),
						Lang.POSITIONS_SUBTITLE.getValue(true)).runTaskLater(BattleRoyale.getInstance(), 2L);
				
				/* add game won stat */
				GameUtils.addStat(team_member, StatType.WON_GAMES, true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onKnock(final MemberKnockedEvent eve) {
		Member member = eve.getMember();
		Member killer = eve.getKnocker();
		if (member == null || !member.isOnline() || !member.hasTeam()) {
			return;
		}
		
		Team team = member.getTeam();
		if (!team.isDead()) {
			return;
		}
		
		team.getLivingMembers().stream().forEach(team_mate -> {
			Player mate = team_mate.getPlayer();
			
			/* call death event */
			boolean same_member = team_mate.getUUID().equals(member.getUUID());
			MemberDeathEvent event = new MemberDeathEvent(team_mate,
					same_member ? DeathCause.KILLED_BY_PLAYER : DeathCause.BLEEDING_OUT, 
					same_member ? killer : null,
					same_member ? (mate.getName() + (killer != null ? " was killed by " + killer.getName() : " died bleeding out!"))
								: mate.getName() + " died bleeding out!",
					same_member ? eve.getKnockLocation() : mate.getLocation(),
					ItemStackUtil.getAllContents(mate.getInventory(), true));
			event.call();
			
			/* kill mate */
			BRPlayer.getBRPlayer ( mate ).setKnocked ( false );
			mate.setHealth ( 0.0D );
		});
		
		/* clear knock message */
		eve.setKnockMessage(null);
	}
	
	/**
	 * Calculate the next member position.
	 */
	private static int nextPosition ( ) {
		int position = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			position += BRPlayer.getBRPlayer(player).isLiving() ? 1 : 0;
		}
		return position;
	}
	
	/**
	 * Calculate the next team position.
	 */
	private static int nextTeamPosition() {
		return BRTeam.getLivingTeams().size();
	}
	
	/**
	 * Returns the teams that still online.
	 */
	private static List<Team> onlineTeams(Player exclude) {
		List<Team> online = new ArrayList<Team>();
		List<Team> teams = new ArrayList<Team>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			BRPlayer bp = BRPlayer.getBRPlayer(player);
			if (!bp.hasTeam() || teams.contains(bp.getTeam())) {
				continue;
			}
			
			BRPlayer ex = BRPlayer.getBRPlayer(exclude);
			if (ex.hasTeam() && ex.getTeam().equals(bp.getTeam()) && bp.getTeam().getLivingMembers().size() < 2) {
				continue;
			}
			
			if (!online.contains(bp.getTeam())) {
				online.add(bp.getTeam());
			}
			
			teams.add(bp.getTeam());
		}
		return online;
	}
}