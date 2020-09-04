package com.hotmail.AdrianSR.BattleRoyale.game.managers;

import com.hotmail.AdrianSR.BattleRoyale.events.GameEndEvent;

/**
 * Callable interface.
 * <p>
 * @author AdrianSR
 */
public interface StopRunnable {

	/**
	 * Called when the game is stopped.
	 * <p>
	 * This will be not executed if the
	 * {@link GameEndEvent} is cancelled.
	 */
	public abstract void stop();
}
