package com.hotmail.AdrianSR.BattleRoyale.game;

/**
 * Represents the various type of
 * Battle Royale players mode.
 * <p>
 * @author AdrianSR
 */
public enum BRPlayerMode {

	/**
	 * The mode all the players
	 * have when they are in the lobby
	 * waiting for the game start.
	 */
	WAITING,
	
	/**
	 * The mode all the players
	 * have when they are playing.
	 */
	PLAYING,
	
	/**
	 * The mode all the players
	 * have after die or when join
	 * the server while the game is running.
	 */
	SPECTATOR;
}
