package com.hotmail.AdrianSR.BattleRoyale.game.mode.complex;

import java.io.File;

import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleModeType;

/**
 * Represents the Complex
 * Battle Mode that allows
 * Developers to create complex
 * battle modes like FoodWars.
 * <p>
 * @author AdrianSR
 */
public abstract class ComplexBattleMode implements BattleMode {

	/**
	 * Mode description.
	 */
	protected ComplexBattleModeDescription description;
	protected File                                file;
	
	/**
	 * Returns mode description.
	 * <p>
	 * @return description.
	 */
	public final ComplexBattleModeDescription getDescription() {
		return description;
	}
	
	/**
	 * Returns true if the conditions 
	 * to start are fulfilled.
	 * <p>
	 * @param battlemap_loaded true if the battle map is already loaded.
	 * @return true if the game can start.
	 */
	public abstract boolean canStart(boolean battlemap_loaded);
	
	/**
	 * Returns battle mode
	 * jar file.
	 * <p>
	 * @return jar file.
	 */
	public final File getFile() {
		return file;
	}
	
	/**
	 * Returns battle mode
	 * data folder.
	 * <p>
	 * @return mode data folder.
	 */
	public final File getDataFolder() {
		return new File(BattleModeType.COMPLEX.getDirectory(), description.getName());
	}
	
	/**
	 * Mkdir battle mode data folder
	 * if does not exists.
	 */
	public final void checkDataFolder() {
		File folder = getDataFolder();
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdir();
		}
	}
}