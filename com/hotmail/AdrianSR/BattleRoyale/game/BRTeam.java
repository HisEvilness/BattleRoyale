package com.hotmail.AdrianSR.BattleRoyale.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.scoreboard.Scoreboard;

import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.AdrianSR.BattleRoyale.database.DTBPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MoneyManager;
import com.hotmail.AdrianSR.BattleRoyale.iterator.MemberIterator;

/**
 * Represents a Battle Royale team.
 * 
 * @author AdrianSR
 */
public class BRTeam implements Team {

	/**
	 * Global members teams.
	 */
	private static final List<Team> TEAMS = new ArrayList<Team>();
	
	/**
	 * Register a {@link Team}.
	 * 
	 * @param team the team to unregister.
	 */
	public static void registerTeam(final Team team) {
		// check is not already exists.
		if (!TEAMS.contains(team)) {
			// add team.
			TEAMS.add(team);
		}
	}
	
	/**
	 * Unregister {@link Team}.
	 * 
	 * @param team the team to unregister.
	 */
	public static void unregisterTeam(final Team team) {
		// remove team.
		TEAMS.remove(team);
	}
	
	// OLD....
//	public static List<Team> getTeams() {
//		return TEAMS;
//	}
	
	/**
	 * Get all teams in the game.
	 * <p>
	 * @return a team list.
	 */
	public static List<Team> getTeams() {
		// get valid list.
		final List<Team> valid_teams = new ArrayList<Team>();
		for (Team team : TEAMS) {
			if (team != null) {
				valid_teams.add(team);
			}
		}
		return Collections.unmodifiableList(valid_teams);
	}
//	public static List<Team> getTeams() {
//		// get valid list.
//		final List<Team> valid_teams = new ArrayList<Team>();
//		
//		// check valid teams.
//		for (Team team : TEAMS) {
//			if (team != null) {
//				valid_teams.add(team);
//			}
//		}
//		
//		// update teams list.
//		TEAMS.clear();
//		TEAMS.addAll(valid_teams);
//		return Collections.unmodifiableList(TEAMS);
//	}
	
	public static List<Team> getLivingTeams() {
		return new ArrayList<Team>(getTeams()).stream().filter(Team :: isLiving).collect(Collectors.toList());
	}
	
	/**
	 * Check team, and update team state.
	 * <p>
	 * @param member the member. (the member must to have a team).
	 */
	public static void checkTeam(final Member member) {
		final Team        team = member.getTeam();
		final List<Team> teams = getTeams();
		boolean valid = false;

		// if contains, update.
		if (teams.contains(team)) {
			TEAMS.remove(team);
			TEAMS.add(team);
			
			// set valid.
			valid = true;
		} else { // if does not contains, add if the owner, or remove.
			if (member.getUUID().equals(team.getOwnerID())) { // when is the team owner id.
				TEAMS.add(team);

				// set valid.
				valid = true;
			} else {
				member.setTeam(null);
			}
		}

		// check team members.
		if (valid) {
			for (Member mem : new ArrayList<Member>(team.getMembers())) {
				if (mem != null) {
					if (mem.hasTeam()) {
						if (!mem.getTeam().equals(team)) {
							team.removeMember(mem);
						}
					} else {
						team.removeMember(mem);
					}
				}
			}
		}
	}
	
	/**
	 * Class values.
	 */
	private final Map<UUID, Member> members;
	private Scoreboard                board;
	private UUID                    ownerID;
	private int                       kills;
	
	/**
	 * Construct a new Battle Royale Team.
	 */
	public BRTeam() {
		// construct.
		members = new HashMap<UUID, Member>(Math.max(GameManager.getBattleMode().getMaxPlayersPerTeam(), 1));
		
		// register team.
		registerTeam(this);
	}
	
	@Override
	public List<Member> getMembers() {
		return Collections.unmodifiableList(new ArrayList<Member>(members.values()));
	}

	@Override
	public List<Member> getOnlineMembers() {
		return getMembers().stream().filter(member -> ( member != null && member.isOnline() )).collect(Collectors.toList());
	}
	
	@Override
	public List<Member> getLivingMembers() {
		return getOnlineMembers().stream().filter(Member :: isLiving).collect(Collectors.toList());
	}
	
	@Override
	public List<Member> getKnockedMembers() {
		return getLivingMembers().stream().filter(Member :: isKnocked).collect(Collectors.toList());
	}
	
	@Override
	public int getMemberCount() {
		return members.size();
	}
	
	@Override
	public Member getOwner() {
		return ownerID != null ? BRPlayer.getBRPlayer(ownerID) : null;
	}

	@Override
	public UUID getOwnerID() {
		return ownerID;
	}

	@Override
	public boolean isDead() {
		return getLivingMembers().isEmpty() || ( getKnockedMembers().size() >= getLivingMembers().size() );
	}

	@Override
	public boolean isLiving() {
		return !isDead();
	}

	@Override
	public boolean isIndividual() {
		return getMembers().size() == 1;
	}
	
	@Override
	public boolean isFull() {
		return getMemberCount() >= Math.max(GameManager.getBattleMode().getMaxPlayersPerTeam(), 1);
	}
	
	@Override
	public boolean isEmpty() {
		return getMemberCount() < 1;
	}

	@Override
	public Scoreboard getScoreboard() {
		return board;
	}
	
	@Override
	public void setScoreboard(Scoreboard scoreboard) {
		 // set new scoreboard.
		board = scoreboard;
		
		// check if not null and update.
		if (scoreboard != null) {
			for (Member mem : getOnlineMembers()) {
				// check member.
				if (mem == null || mem.getPlayer() == null) {
					continue;
				}
				
				// set scoreboard.
				mem.getPlayer().setScoreboard(scoreboard);
			}
		}
	}

	@Override
	public Team addMember(final Member member) {
		// check is not null, if is not in another team.
		if (member != null && !member.hasTeam()) {
			// check is the owner.
			if (members.isEmpty()) {
				// set as the owner.
				ownerID = member.getUUID();
			}
			
			// put.
			members.put(member.getUUID(), member);
			member.setTeam(this);
		}
		return this;
	}

	@Override
	public Team removeMember(final Member member) {
		if (member != null && member.hasTeam() && member.getTeam().equals(this)) {
			member.setTeam(null);
			members.remove(member.getUUID());
		}
		return this;
	}

	@Override
	public Iterator<Member> iterator() {
		return new MemberIterator(new ArrayList<Member>(members.values()));
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof BRTeam)) {
			return false;
		}
		
		final BRTeam other = (BRTeam) obj;
		return ((ownerID != null) == (other.getOwnerID() != null)) 
				&& ownerID != null && ownerID.equals(other.getOwnerID());
	}

	@Override
	public int getKills() {
		return kills;
	}

	@Override
	public void addKill(int kills, Member responsible) {
		// add kill.
		this.kills += kills;
		
		// add kill to database player.
		if (responsible instanceof BRPlayer) {
			// get database player.
			final DTBPlayer data = ((BRPlayer) responsible).getDatabasePlayer();
			if (data != null) { // check database player
				// add kill.
				data.addKill(kills);
				
				// add money.
				MoneyManager.giveMoney(responsible.getPlayer(), Money.KILL_MONEY);
				
				// update database.
				data.save(true, false, false);
			}
		}
	}
}