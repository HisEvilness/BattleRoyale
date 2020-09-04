package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.util.scoreboard.ScoreboardBuilder;
import com.hotmail.adriansr.core.events.CustomEvent;

public class ScoreboardUpdateEvent extends CustomEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	private final Player             player;
	private final ScoreboardBuilder builder;

	public ScoreboardUpdateEvent(Player player, ScoreboardBuilder builder) {
		this.player  = player;
		this.builder = builder;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the builder
	 */
	public ScoreboardBuilder getBuilder() {
		return builder;
	}

	/**
	 * Call this.
	 */
	@Override
	public ScoreboardUpdateEvent call ( ) {
		return (ScoreboardUpdateEvent) super.call ( );
	}
}