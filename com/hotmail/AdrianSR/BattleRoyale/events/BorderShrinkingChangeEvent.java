package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown whenever the border
 * change his size.
 * <p>
 * @author AdrianSR
 */
public class BorderShrinkingChangeEvent extends CustomEvent {
	
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
	private final BorderShrink from;
	private final BorderShrink   to;
	
	/**
	 * Construct a new border
	 * shrinking event.
	 * <p>
	 * @param from the shrink from.
	 * @param to the shrink to.
	 */
	public BorderShrinkingChangeEvent(final BorderShrink from, final BorderShrink to) {
		this.from = from;
		this.to   = to;
	}
	
	/**
	 * Get shrink from.
	 * <p>
	 * @return from shrink.
	 */
	public BorderShrink getFrom() {
		return from;
	}
	
	/**
	 * Get shrink to.
	 * <p>
	 * @return to shrink.
	 */
	public BorderShrink getTo() {
		return to;
	}
}
