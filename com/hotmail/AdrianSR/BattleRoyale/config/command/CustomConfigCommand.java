package com.hotmail.AdrianSR.BattleRoyale.config.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.adriansr.core.util.Validable;

public class CustomConfigCommand implements Validable {
	
	private final String     argument;
	private final boolean for_players;

	public CustomConfigCommand(String argument, boolean for_players) {
		if (!StringUtils.isBlank(argument)) {
			this.argument = argument.replace("/", "").trim();
		} else {
			this.argument = argument;
		}
		
		this.for_players = for_players;
	}

	public String getArgument() {
		return argument;
	}
	
	public String getArgumentForPlayer(Player player) {
		if (isValid() && isForPlayers()) {
			return argument.replace(Lang.PLAYER_REPLACEMENT_KEY, player.getName());
		}
		return argument;
	}
	
	public boolean isForPlayers() {
		return for_players;
	}
	
	@Override
	public boolean isValid() {
		return argument != null && !StringUtils.isBlank(argument);
	}

	@Override
	public boolean isInvalid() {
		return !isValid();
	}
}