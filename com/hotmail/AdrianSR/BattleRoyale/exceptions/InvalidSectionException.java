package com.hotmail.AdrianSR.BattleRoyale.exceptions;

public class InvalidSectionException extends Exception {
	
	private static final long serialVersionUID = -6856732497369974195L;

	/**
	 * @param msg message to send.
	 */
	public InvalidSectionException(String msg) {
        super(msg);
    }
}
