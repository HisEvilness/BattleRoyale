package com.hotmail.AdrianSR.BattleRoyale.exceptions;

public class InvalidPointException extends Exception {
	
	private static final long serialVersionUID = 3651080580012672196L;

	/**
	 * @param msg message to send.
	 */
	public InvalidPointException(String msg) {
        super(msg);
    }
}
