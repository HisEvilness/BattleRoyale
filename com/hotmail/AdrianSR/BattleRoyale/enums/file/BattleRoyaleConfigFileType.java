package com.hotmail.AdrianSR.BattleRoyale.enums.file;

import java.io.File;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

public enum BattleRoyaleConfigFileType {

	MAIN_CONFIG(null, "BattleRoyaleConfig.yml"),
	LANG_CONFIG(null, "BattleRoyaleLang.yml"),
	MONEY_CONFIG(null, "BattleRoyaleMoney.yml"),
	LOBBY_ITEMS_CONFIG(null, "BattleRoyaleLobbyItems.yml"),
	BATTLE_ITEMS_CONFIG(null, "BattleRoyaleItems.yml"),
	LOOT_ITEMS_CONFIG(null, "BattleRoyaleLoot.yml"),
	LOBBY_SCOREBOARD_CONFIG(null, "BattleRoyaleLobbyScoreboard.yml"),
	GAME_SCOREBOARD_CONFIG(null, "BattleRoyaleGameScoreboard.yml"),
	LOBBY_MAP_CONFIG(null, "BattleRoyaleLobby.yml"),
	;
	
	private final File      parent;
	private final String file_name;
	
	BattleRoyaleConfigFileType(File parent, String file_name) {
		this.parent    = ( parent == null ? BattleRoyale.getInstance().getDataFolder() : parent );
		this.file_name = file_name;
	}
	
	public File getParent() {
		return parent;
	}
	
	public String getName() {
		return file_name;
	}
	
	/**
	 * Gets the .yml file, or null if this doesn't have a valid parent.
	 * <p>
	 * @return the .yml file, or null if this doesn't have a valid parent.
	 */
	public File getFile() {
		return ( parent != null ) ? ( new File(getParent(), getName()) ) : null;
	}
}