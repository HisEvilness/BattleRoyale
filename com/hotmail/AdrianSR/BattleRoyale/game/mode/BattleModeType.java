package com.hotmail.AdrianSR.BattleRoyale.game.mode;

import java.io.File;

import com.hotmail.AdrianSR.BattleRoyale.game.mode.manager.BattleModeManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

public enum BattleModeType {

	SIMPLE("{modes_folder}"),
	COMPLEX("{modes_folder}|Complex");
	
	private final String directory;
	
	BattleModeType(String directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return new File(getPluginDataFolder(), directory
				.replace("{modes_folder}", BattleModeManager.BATTLE_MODES_FOLDER_NAME)
				.replace("|", File.separator));
	}
	
	public boolean mkdir() {
		return getDirectory().exists() ? false : getDirectory().mkdir();
	}
	
	private File getPluginDataFolder() {
		return BattleRoyale.getInstance().getDataFolder();
	}
}
