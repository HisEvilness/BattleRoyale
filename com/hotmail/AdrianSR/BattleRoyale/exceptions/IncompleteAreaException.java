package com.hotmail.AdrianSR.BattleRoyale.exceptions;

public class IncompleteAreaException extends Exception {
	
	private static final long serialVersionUID = 2488493599526306882L;

	/**
	 * @param msg message to send.
	 */
	public IncompleteAreaException(String msg) {
        super(msg);
    }
}
