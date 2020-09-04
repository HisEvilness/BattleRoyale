package com.hotmail.AdrianSR.BattleRoyale.game.mode.complex;

import java.io.File;

public class ComplexBattleModeLoader {
	
	private final ComplexBattleModeDescription description;
	private final File                                file;

	public ComplexBattleModeLoader(ComplexBattleModeDescription description, File file) {
		this.description = description;
		this.file        = file;
	}

	public void initialize(ComplexBattleMode complex) {
		complex.description = description;
		complex.file        = file;
		complex.onInitialize();
	}
}
