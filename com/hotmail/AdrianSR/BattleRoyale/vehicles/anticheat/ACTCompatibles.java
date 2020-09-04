package com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum ACTCompatibles {
	
	AAC("AAC"),
	NO_CHEAT_PLUS("NoCheatPlus"),
	;
	
	private final String plugin_name;
	
	ACTCompatibles(String plugin_name) {
		this.plugin_name = plugin_name;
	}
	
	public String getPluginName() {
		return plugin_name;
	}
	
	public Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin(plugin_name);
	}
	
	public static ACTCompatibles of(String name) {
		return Arrays.stream(values()).filter(comp -> comp.plugin_name.equals(name)).findFirst().orElse(null);
	}
}