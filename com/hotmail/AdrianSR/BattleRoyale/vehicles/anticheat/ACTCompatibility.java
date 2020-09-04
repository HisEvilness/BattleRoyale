package com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface ACTCompatibility extends Listener {
	
	public static ACTCompatibility of(Plugin act_plugin) {
		ACTCompatibility compt = null;
		if (act_plugin != null && act_plugin.isEnabled()) {
			switch (act_plugin.getName()) {
			case "AAC":
				compt = new AACCompatibility(act_plugin);
				break;
			case "NoCheatPlus":
				compt = new NCPCompatibility(act_plugin);
				break;
			}
		}
		
		if (compt != null) {
			compt.register();
		}
		return compt;
	}
	
	public static ACTCompatibility of(String act_pluginname) {
		return of(Bukkit.getPluginManager().getPlugin(act_pluginname));
	}
	
	public static ACTCompatibility of(ACTCompatibles compatible) {
		return of(compatible.getPluginName());
	}
	
	public boolean enabled(ACTHackType type);

	public void check(ACTHackType type);
	
	public void ignore(ACTHackType type);
	
//	public void ignore(ACTHackType type, long time);
	
	public boolean disabled(ACTHackType type);
	
	public Plugin getAnticheatPlugin();
	
	default void register() {
		Bukkit.getPluginManager().registerEvents(this, getAnticheatPlugin());
	}
	
	default void unregister() {
		HandlerList.unregisterAll(this);
	}
}