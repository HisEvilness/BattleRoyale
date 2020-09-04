package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown when game ends.
 * <p>
 * @author AdrianSR / Sunday 16 August, 2020 / 01:11 AM
 */
public class GameEndEvent extends CustomEvent implements Cancellable {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	/**
	 * Variable that identifies whether 
	 * the event is cancelled
	 */
	private boolean cancelled;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Bungs can appear if
	 * this event is cancelled.
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
