package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.adriansr.core.events.CustomEvent;

public final class MemberThrowTNT extends CustomEvent implements Cancellable {
	
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
	private final Member            	member;
	private final TNTPrimed      	   	   tnt;
	private       float                  yield = 8.0F;
	private       boolean consume_item_in_hand = true;
	private       boolean        	 cancelled;

	/**
	 * Construct event.
	 * <p>
	 * @param member the member that is throwing the tnt.
	 * @param tnt throwed tnt.
	 */
	public MemberThrowTNT(Member member, TNTPrimed tnt) {
		this.member = member;
		this.tnt    = tnt;
	}

	/**
	 * Get the member that
	 * throwed the tnt.
	 * <p>
	 * @return tnt throwed.
	 */
	public Member getMember() {
		return member;
	}
	
	public TNTPrimed getThrowedTNT() {
		return tnt;
	}
	
	public float getExplosionStrength() {
		return yield;
	}
	
	public void setExplosionStrength(float yield) {
		this.yield = yield;
	}
	
	public boolean isCosumeItemInHand() {
		return consume_item_in_hand;
	}
	
	public void setConsumeItemInHand(boolean consume) {
		this.consume_item_in_hand = consume;
	}

	@Override
	public MemberThrowTNT call ( ) {
		return (MemberThrowTNT) super.call ( );
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancell) {
		this.cancelled = cancell;
	}
}