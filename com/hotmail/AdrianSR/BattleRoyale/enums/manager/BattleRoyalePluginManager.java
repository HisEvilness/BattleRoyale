package com.hotmail.AdrianSR.BattleRoyale.enums.manager;

import com.hotmail.AdrianSR.BattleRoyale.config.manager.BattleRoyaleConfigManager;
import com.hotmail.AdrianSR.BattleRoyale.database.DatabaseManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MoneyManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MotdManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.manager.BattleModeManager;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.loot.managers.LootManager;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.manager.MiniMapManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border.BorderCreatorManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.chests.RandomChestSelectorManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.minimap.AreaSelectorManager;
import com.hotmail.AdrianSR.BattleRoyale.util.channel.BRPluginChannel;
import com.hotmail.adriansr.core.handler.PluginHandler;

public enum BattleRoyalePluginManager {

	CONFIG_MANAGER ( BattleRoyaleConfigManager.class ),
	// TODO: another config managers
	
	BUNGEECORD_CHANNEL_MANAGER ( BRPluginChannel.class ),
	DATABASE_MANAGER ( DatabaseManager.class ),
	BATTLE_MODE_MANAGER ( BattleModeManager.class ),
	GAME_MANAGER ( GameManager.class ),
	MONEY_MANAGER ( MoneyManager.class ),
	BORDER_CREATOR_MANAGER ( BorderCreatorManager.class ),
	RANDOM_CHEST_SELECTOR_MANAGER ( RandomChestSelectorManager.class ),
	AREA_SELECTOR_MANAGER ( AreaSelectorManager.class ),
	MINI_MAP_MANAGER ( MiniMapManager.class ),
	MOTD_MANAGER ( MotdManager.class ),
	LOOT_MANAGER ( LootManager.class ),
	;
	
	private final Class < ? extends PluginHandler > clazz;
	
	private BattleRoyalePluginManager ( Class < ? extends PluginHandler > clazz ) {
		this.clazz = clazz;
	}
	
	public Class < ? extends PluginHandler > getManagerClass ( ) {
		return clazz;
	}
}