package com.hotmail.AdrianSR.BattleRoyale.database;

/**
 * Represents the Battle Royale
 * cosmetic.
 * <p>
 * @author AdrianSR
 */
public final class Cosmetic {
	
	private final CosmeticType type;
	private final Enum<?>     value;

	/**
	 * Construct cosmetic.
	 * <p>
	 * @param type type of cosmetic.
	 * @param value the cosmetic.
	 */
	public Cosmetic(CosmeticType type, Enum<?> value) {
		this.type  = type;
		this.value = value;
	}

	/**
	 * Returns the type of
	 * cosmetic.
	 * <p>
	 * @return type of cosmetic.
	 */
	public CosmeticType getType() {
		return type;
	}
	
	/**
	 * Returns the cosmetic.
	 * <p>
	 * @return cosmetic.
	 */
	public Enum<?> getValue() {
		return value;
	}
	
	/**
	 * @return true if is valid.
	 */
	public boolean valid() {
		return type != null && value != null;
	}
}