package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown whenever a {@link Member} is knocked.
 * <p>
 * @author AdrianSR
 */
public class MemberKnockedEvent extends CustomEvent {
	
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
	private final Member     member; // knocked member.
	private final Member    knocker; // his knocker (can be null).
	private final Location location; // knock location.
	private       String    message; /* knock message */
	
	/**
	 * Costruct a new Member Knocked Event.
	 * <p>
	 * @param member the knocked Member.
	 * @param knocker the knocker member, or null if don have knocker.
	 * @param location the knock location.
	 */
	public MemberKnockedEvent(final Member member, final Member knocker, final Location location) {
		this.member   = member;
		this.knocker  = knocker;
		this.location = location;
		message       = ((knocker != null) ? Lang.MEMBER_KNOCKED_BY_MESSAGE.getValue(true) : Lang.MEMBER_KNOCKED_MESSAGE.getValue(true))
				.replace(Lang.PLAYER_REPLACEMENT_KEY,   member.getName())                       // replace member name.
				.replace(Lang.KNOCKER_REPLACEMENT_KEY, ((knocker != null) ? knocker.getName() : "")); // replace knocker name.
	}
	
	/**
	 * Get the member that
	 * is knocked.
	 * <p>
	 * @return the knocked member.
	 */
	public Member getMember() {
		return member;
	}
	
	/**
	 * The knocker member.
	 * <p>
	 * @return the knocker or null
	 * if was not knocked by another
	 * member.
	 */
	public Member getKnocker() {
		return knocker;
	}
	
	/**
	 * Get knock location.
	 * <p>
	 * @return the knock location.
	 */
	public Location getKnockLocation() {
		return location;
	}
	
	/**
	 * Returns message players will
	 * see in the chat.
	 * <p>
	 * @return knock message.
	 */
	public String getKnockMessage() {
		return message;
	}
	
	/**
	 * Sets message players will
	 * se in the chat.
	 * <p>
	 * @param message new message.
	 */
	public void setKnockMessage(String message) {
		this.message = message;
	}
}