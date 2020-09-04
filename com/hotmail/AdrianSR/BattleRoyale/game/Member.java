package com.hotmail.AdrianSR.BattleRoyale.game;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Represents a Battle Royale Member.
 * <p>
 * @author AdrianSR
 */
public abstract interface Member {
	
	/**
	 * Get the member name.
	 * <p>
	 * @return the name.
	 */
	public String getName();
	
	/**
	 * Get the member Unique {@link UUID}.
	 * <p>
	 * @return member UUID.
	 */
	public UUID getUUID();
	
	/**
	 * Get member team.
	 * <p>
	 * @return the member team.
	 */
	public Team getTeam();
	
	/**
	 * Set the member team.
	 * <p>
	 * @return this.
	 */
	public Member setTeam(final Team team);
	
	/**
	 * Get the bukkit player.
	 * <p>
	 * @return the bukkit player.
	 */
	public Player getPlayer();
	
	/**
	 * Returns the current {@link BRPlayerMode}
	 * the player have.
	 * <p>
	 * @return current mode.
	 */
	public BRPlayerMode getPlayerMode();
	
	/**
	 * Sets player mode.
	 * <p>
	 * @return this.
	 */
	public Member setPlayerMode(BRPlayerMode mode);
	
	/**
	 * Get the scoreboard of this member.
	 * <p>
	 * @return the scoreboard of this member.
	 */
	public Scoreboard getScoreboard();
	
	/**
	 * Sets the team scoreboard.
	 * <p>
	 * @param scoreboard new scoreboard.
	 */
	public void setScoreboard(final Scoreboard scoreboard);
	
	/**
	 * @return true if the member is online.
	 */
	public boolean isOnline();
	
	/**
	 * @return true if is dead.
	 */
	public boolean isDead();
	
	/**
	 * @return true if is living.
	 */
	public boolean isLiving();
	
	/**
	 * @return true if the member is knocked.
	 */
	public boolean isKnocked();
	
	/**
	 * Gets the {@link Member} that knocked down this.
	 * <p>
	 * @return the knocker or <code>null</code> if this member was knocked for any
	 *         other damage source.
	 */
	public Member getKnocker ( );
	
	/**
	 * @return true if the current mode is spectator.
	 */
	public boolean isSpectator();
	
	/**
	 * Set is knocked.
	 * <p>
	 * @param knocked is kanocked?
	 */
	public void setKnocked(boolean knocked);
	
	/**
	 * Sets the {@link Member} that knocked down this.
	 * <p>
	 * @param knocker the knocker, or <code>null</code> if it was not knocked by a {@link Member}.
	 */
	public void setKnocker ( Member knocker );
	
	/**
	 * @return true if the member is being re-animated.
	 */
	public boolean isBeingReanimated ( );
	
	/**
	 * Sets whether this {@link Member} is being re-animated or not.
	 * <p>
	 * @param reanimating true if re-animating.
	 */
	public void setReanimating ( boolean reanimating );
	
	/**
	 * @return true if the member has a team.
	 */
	public boolean hasTeam();
}
