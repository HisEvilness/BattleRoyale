package com.hotmail.AdrianSR.BattleRoyale.questions;

/**
 * Represents a Quenstion Answer acceptor.
 * 
 * @author AdrianSR
 */
public interface AcceptAnswer {
	
	/**
	 * Called when answer.
	 * 
	 * @param input the reponse.
	 */
	boolean onAnswer(String input);
}
