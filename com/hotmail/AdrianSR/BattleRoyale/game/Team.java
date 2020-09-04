package com.hotmail.AdrianSR.BattleRoyale.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.scoreboard.Scoreboard;

/**
 * Represents a Battle Royale team.
 * 
 * @author AdrianSR
 */
public abstract interface Team extends Iterable<Member> {
	
	/**
	 * Get Members in this team.
	 * 
	 * @return a members list.
	 */
	public List<Member> getMembers();
	
	/**
	 * Get Online Members in this team.
	 * 
	 * @return a online members list.
	 */
	public List<Member> getOnlineMembers();
	
	/**
	 * Get Living Members.
	 * 
	 * @return a living members list.
	 */
	public List<Member> getLivingMembers();
	
	/**
	 * Returns knocked members.
	 * <p>
	 * @return knocked members.
	 */
	public List<Member> getKnockedMembers();
	
	/**
	 * Get the number of total member in the team.
	 * 
	 * @return the total members in this team.
	 */
	public int getMemberCount();
	
	/**
	 * Get the team owner.
	 * 
	 * @return the owner.
	 */
	public Member getOwner();
	
	/**
	 * Get the team owner {@link UUID}.
	 * 
	 * @return the owner UUID.
	 */
	public UUID getOwnerID();
	
	/**
	 * @return true if the team is dead.
	 */
	public boolean isDead();
	
	/**
	 * @return false if the team living.
	 */
	public boolean isLiving();
	
	/**
	 * @return true if the team is individual.
	 */
	public boolean isIndividual();
	
	/**
	 * @return true if the team is full.
	 */
	public boolean isFull();
	
	/**
	 * @return true if the team is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Get the scoreboard of this team .
	 * 
	 * @return the scoreboard of this team.
	 */
	public Scoreboard getScoreboard();
	
	/**
	 * Sets the team scoreboard.
	 * 
	 * @param scoreboard new scoreboard.
	 */
	public void setScoreboard(final Scoreboard scoreboard);
	
	/**
	 * Add a member this team.
	 * 
	 * @return this.
	 */
	public Team addMember(final Member member);
	
	/**
	 * Remove a member from this team.
	 * 
	 * @return this.
	 */
	public Team removeMember(final Member member);
	
	/**
	 * Get current team kills.
	 * 
	 * @return current team kills.
	 */
	public int getKills();
	
	/**
	 * Register new kill.
	 * <p>
	 * @param amount the amount of kills to add.
	 * @param responsible the member that kill another member.
	 */
	public void addKill(int amount, final Member responsible);
}