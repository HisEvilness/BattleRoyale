package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.event.HandlerList;

import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown when BattleRoyale game starts.
 * <p>
 * @author AdrianSR / Sunday 16 August, 2020 / 01:09 AM
 */
public class GameStartEvent extends CustomEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );

	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
}