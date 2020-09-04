package com.hotmail.AdrianSR.BattleRoyale.command;

import com.hotmail.adriansr.core.command.CommandArgument;

/**
 * An convenience implementation of {@link CommandArgument} that is to be
 * implemented by BattleRoyale command arguments.
 * <p>
 * @author AdrianSR / Monday 17 August, 2020 / 02:09 PM
 */
public interface BattleRoyaleCommandArgument extends CommandArgument {

	/**
	 * Gets this argument description.
	 * <p>
	 * @return argument description.
	 */
	public String getDescription ( );
}
