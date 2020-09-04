package com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game.config;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import com.hotmail.AdrianSR.BattleRoyale.enums.file.BattleRoyaleConfigFileType;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.config.ScoreboardConfiguration;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.game.GameScoreboardHandler;

public final class GameScoreboardConfiguration extends ScoreboardConfiguration {

	public GameScoreboardConfiguration(String name, String[] elements, String[] default_elements) {
		super(name, elements, default_elements);
	}

	public GameScoreboardConfiguration(String name, String... elements) {
		super(name, elements);
	}
	
	@Override
	protected void header(ConfigurationSection section) {
		ConfigurationOptions options = section.getRoot().options();
		if (!(options instanceof YamlConfigurationOptions)) {
			return;
		}
		
		((YamlConfigurationOptions) options).header(
				"---------------------------- Battle Royale Game Scoreboard ---------------------------- #\r\n" + 
				"This is the configuration file of the game Scoreboard of the Battle Royale.\r\n" + 
				"This configuration is linked to the language configuration. (File: '" + BattleRoyaleConfigFileType.LANG_CONFIG.getName() + "')\r\n" + 
				GameScoreboardHandler.KEY_CURRENT_DATE       + " will be replaced with the current date.\r\n" + 
				GameScoreboardHandler.KEY_BORDER_STATUS      + " will be replaced with the current status of the border.\r\n" + 
				GameScoreboardHandler.KEY_PLAYERS_TEAMS_LEFT + " will be replaced with the number of players/teams left.\r\n" + 
				GameScoreboardHandler.KEY_KILLS              + " will be replaced with the number of kills of the team.\r\n" + 
				
				GameScoreboardHandler.KEY_FIRST_ARROWS_LINE  + " will be replaced with the first arrows line.\r\n" + 
				GameScoreboardHandler.KEY_SECOND_ARROWS_LINE + " will be replaced with the second arrows line.\r\n" + 
				GameScoreboardHandler.KEY_THIRD_ARROWS_LINE  + " will be replaced with the third arrows line.\r\n" + 
				
				GameScoreboardHandler.KEY_GAME_KILLS   + " will be replaced with max number of kills of the playing battle mode.\r\n" + 
				GameScoreboardHandler.KEY_SAFE_UNSAFE  + " will be replaced with the status of the player. (If is safe or unsafe).\r\n" + 
				GameScoreboardHandler.KEY_TEAM_MEMBERS + " will append the status of the team mates of the player.\r\n" + 
				"--------------------------------------------------------------------------------------- #");
		((YamlConfigurationOptions) options).copyHeader(true);
	}
}