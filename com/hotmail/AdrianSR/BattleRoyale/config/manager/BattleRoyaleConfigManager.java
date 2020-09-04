package com.hotmail.AdrianSR.BattleRoyale.config.manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.BattleRoyale.config.items.LobbyItem;
import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.AdrianSR.BattleRoyale.enums.file.BattleRoyaleConfigFileType;
import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game.GameScoreboardHandler;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby.LobbyScoreboardHandler;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;

/**
 * Represents the manager of the configurations of the Battle Royale plugin.
 * <p>
 * @author AdrianSR / Tuesday 05 November, 2019 / 08:39 AM
 */
public final class BattleRoyaleConfigManager extends PluginHandler {

	public BattleRoyaleConfigManager(BattleRoyale plugin) {
		super ( plugin );
		
		if (!loadMainConfiguration()) { return; }
		if (!loadLangConfiguration()) { return; }
		if (!loadMoneyConfiguration()) { return; }
		if (!loadLobbyItemsConfiguration()) { return; }
		if (!loadBattleItemsConfiguration()) { return; }
		// the loot configuration has its own manager.
		if (!loadLobbyScoreboardConfiguration()) { return; }
		if (!loadGameScoreboardConfiguration()) { return; }
	}
	
	/**
	 * Loading main configuration.
	 */
	public boolean loadMainConfiguration() {
		File file = BattleRoyaleConfigFileType.MAIN_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration(file);
		if ( Config.saveCommentedDefaultConfiguration(yaml) > 0 
				&& !safeSaveFile(yaml, file, "The file '%s' couldn't be loaded correctly: ") ) {
			return false;
		}
		
		Config.setConfiguration(yaml);
		return true;
	}
	
	/**
	 * Loading language configuration.
	 */
	public boolean loadLangConfiguration() {
		File file = BattleRoyaleConfigFileType.LANG_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration(file);
		if ( Lang.saveDefaultConfiguration(yaml) > 0 
				&& !safeSaveFile(yaml, file, "The file '%s' couldn't be loaded correctly: ") ) {
			return false;
		}
		
		Lang.setConfiguration(yaml);
		return true;
	}
	
	/**
	 * Loading money configuration.
	 */
	public boolean loadMoneyConfiguration() {
		File file = BattleRoyaleConfigFileType.MONEY_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration(file);
		if ( Money.saveDefaultConfiguration(yaml) > 0 
				&& !safeSaveFile(yaml, file, "The file '%s' couldn't be loaded correctly: ") ) {
			return false;
		}
		
		Money.setConfiguration(yaml);
		return true;
	}
	
	public boolean loadLobbyItemsConfiguration() {
		File file = BattleRoyaleConfigFileType.LOBBY_ITEMS_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration(file);
		if ( LobbyItem.saveDefaultConfiguration(yaml) > 0 
				&& !safeSaveFile(yaml, file, "The file '%s' couldn't be loaded correctly: ") ) {
			return false;
		}
		
		LobbyItem.setFile(yaml);
		return true;
	}
	
	public boolean loadBattleItemsConfiguration() {
		File file = BattleRoyaleConfigFileType.BATTLE_ITEMS_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}

		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration(file);
		if (BattleItems.saveDefaultConfiguration(yaml) > 0
				&& !safeSaveFile(yaml, file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}

		BattleItems.setFile(yaml);
		return true;
	}
	
	public boolean loadLobbyScoreboardConfiguration() {
		LobbyScoreboardHandler handler = new LobbyScoreboardHandler((BattleRoyale) plugin); // initializing game scoreboard handler.
		File                     file = BattleRoyaleConfigFileType.LOBBY_SCOREBOARD_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		handler.getConfiguration ( ).load ( yaml );
		
		int save_defaults = handler.getConfiguration ( ).saveDefaults ( yaml );
		if ( save_defaults > 0 ) {
			// as it seems to be incorrectly formatted, or never configured, we are saving some defaults.
			if ( !safeSaveFile ( yaml , file , "The file '%s' couldn't be loaded correctly: " ) ) {
				return false;
			}
			
			if ( save_defaults >= 2 ) {
				handler.getConfiguration ( ).insertDefaultsIntoElements ( );
			}
		}
		return true;
	}
	
	public boolean loadGameScoreboardConfiguration() {
		GameScoreboardHandler handler = new GameScoreboardHandler((BattleRoyale) plugin); // initializing game scoreboard handler.
		File                     file = BattleRoyaleConfigFileType.GAME_SCOREBOARD_CONFIG.getFile();
		if (!safeCreateFile(file, "The file '%s' couldn't be loaded correctly: ")) {
			return false;
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		handler.getConfiguration ( ).load ( yaml );
		
		int save_defaults = handler.getConfiguration ( ).saveDefaults ( yaml );
		if ( save_defaults > 0 ) {
			// as it seems to be incorrectly formatted, or never configured we are saving some defaults.
			if ( !safeSaveFile ( yaml , file , "The file '%s' couldn't be loaded correctly: ") ) {
				return false;
			}
			
			if ( save_defaults >= 2 ) {
				handler.getConfiguration ( ).insertDefaultsIntoElements ( );
			}
		}
		return true;
	}
	
	private boolean safeCreateFile(File file, String error_message) {
		if (!file.exists() && !file.isFile()) {
			try {
				return file.createNewFile();
			} catch (IOException e) {
				ConsoleUtil.sendPluginMessage(ChatColor.RED, String.format(error_message, file.getName()), plugin);
				e.printStackTrace();
				shutdown();
				return false;
			}
		}
		return true;
	}
	
	private boolean safeSaveFile(YamlConfigurationComments yaml, File file, String error_message) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, String.format(error_message, file.getName()), plugin);
			e.printStackTrace();
			shutdown();
			return false;
		}
		return true;
	}
	
	private void shutdown() {
		Bukkit.getPluginManager().disablePlugin(plugin);
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}