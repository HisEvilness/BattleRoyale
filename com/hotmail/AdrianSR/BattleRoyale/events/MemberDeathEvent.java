package com.hotmail.AdrianSR.BattleRoyale.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown whenever a {@link Member} dies.
 * <p>
 * @author AdrianSR
 */
public final class MemberDeathEvent extends CustomEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	/**
	 * Event modifiers.
	 */
	private final Member          member;
	private final DeathCause       cause;
	private final Member          killer; /* null if was not killed by another player */
	private       String         message;
	private final Location      location; /* death location */
	private final List<ItemStack>  drops;
	
	/**
	 * Costruct a new Member Death Event.
	 * <p>
	 * @param member the death Member.
	 * @param cause the member death cause.
	 * @param killer the member killer, or null if don have killer.
	 * @param keep_inventory keep inventory
	 */
	public MemberDeathEvent(final Member member, final DeathCause cause, final Member killer, String message, 
			final Location location, final List<ItemStack> drops) {
		this.member         = member;
		this.cause          = cause;
		this.killer  	    = killer;
		this.message 	    = message;
		this.location	    = location;
		this.drops          = drops;
	}

	/**
	 * Get the member that
	 * died.
	 * <p>
	 * @return the death member.
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * Get the death cause.
	 * <p>
	 * @return the cause.
	 */
	public DeathCause getCause() {
		return cause;
	}
	
	/**
	 * The killer member.
	 * <p>
	 * @return the killer or null
	 * if was not killed by another
	 * member.
	 */
	public Member getKiller() {
		return killer;
	}
	
	/**
	 * Get the death message.
	 * <p>
	 * @return the death message.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Get member death location.
	 * <p>
	 * @return the death location.
	 */
	public Location getDeathLocation() {
		return location;
	}
	
    /**
     * Gets all the items which will
     * drop when the member dies.
     * <p>
     * @return Items to drop when 
     * the entity dies.
     */
    public List<ItemStack> getDrops() {
        return drops;
    }
    
	/**
	 * Set the death message.
	 * <p>
	 * @param newMessage the new message.
	 */
	public void setMessage(String newMessage) {
		this.message = newMessage;
	}
}