package com.hotmail.AdrianSR.BattleRoyale.exceptions;

import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;

public class InvalidDescriptionException extends Exception {

	private static final long serialVersionUID = 5798199298138344855L;

	public InvalidDescriptionException(String message) {
		super(message);
	}

    public InvalidDescriptionException(final Throwable cause, final String message) {
        super(message, cause);
    }
    
	public InvalidDescriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDescriptionException(Throwable cause) {
		super("Invalid " + BattleMode.COMPLEX_BATTLE_MODE_YML, cause);
	}
}
