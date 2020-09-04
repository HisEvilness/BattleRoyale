package com.hotmail.AdrianSR.BattleRoyale.util.scoreboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a Scoreboard Utilities class.
 * <p>
 * @author AdrianSR
 */
public final class ScoreboardUtils {
	
	/**
	 * Get fortmat date.
	 * <p>
	 * @return the current date.
	 */
	public static String getCurrentDate() {
		return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());
	}
}