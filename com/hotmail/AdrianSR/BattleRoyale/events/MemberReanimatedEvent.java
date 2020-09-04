package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown whenever a {@link Member} is reanimated.
 * <p>
 * @author AdrianSR
 */
public class MemberReanimatedEvent extends CustomEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	/**
	 * Class values.
	 */
	private final Member     member; // reanimated member.
	private final Member reanimator; // his reanimator.
	
	/**
	 * Costruct a new Member Reanimated Event.
	 * <p>
	 * @param member the reanimated Member.
	 * @param reanimator the reanimator Member.
	 */
	public MemberReanimatedEvent(final Member member, final Member reanimator) {
		this.member     = member;
		this.reanimator = reanimator;
	}
	
	/**
	 * Get the member that
	 * has been reanimated.
	 * <p>
	 * @return the reanimated member.
	 */
	public Member getMember() {
		return member;
	}
	
	/**
	 * The reanimator member.
	 * <p>
	 * @return the reanimator.
	 */
	public Member getReanimator() {
		return reanimator;
	}
}
