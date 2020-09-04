package com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby.config;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import com.hotmail.AdrianSR.BattleRoyale.enums.file.BattleRoyaleConfigFileType;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.config.ScoreboardConfiguration;

public final class LobbyScoreboardConfiguration extends ScoreboardConfiguration {

	public LobbyScoreboardConfiguration(String name, String[] elements, String[] default_elements) {
		super(name, elements, default_elements);
	}
	
	public LobbyScoreboardConfiguration(String name, String... elements) {
		super(name, elements);
	}
	
	@Override
	protected void header(ConfigurationSection section) {
		ConfigurationOptions options = section.getRoot().options();
		if (!(options instanceof YamlConfigurationOptions)) {
			return;
		}
		
		((YamlConfigurationOptions) options).header(
				"---------------------------- Battle Royale Lobby Scoreboard ---------------------------- #\r\n" + 
				"This is the configuration file of the lobby Scoreboard of the Battle Royale.\r\n" + 
				"This configuration is linked to the language configuration. (File: '" + BattleRoyaleConfigFileType.LANG_CONFIG.getName() + "')\r\n" + 
				"%CURRENT_DATE% will be replaced with the current date.\r\n" + 
				"%ONLINE_PLAYERS% will be replaced with the number of online players.\r\n" + 
				"%MIN_PLAYERS% will be replaced with the minimum number of players to start the game.\r\n" + 
				"%GAME_STATUS% will be replaced with the current status of the game. (If is starting or waiting for players)\r\n" + 
				"---------------------------------------------------------------------------------------- #");
		((YamlConfigurationOptions) options).copyHeader(true);
	}
}