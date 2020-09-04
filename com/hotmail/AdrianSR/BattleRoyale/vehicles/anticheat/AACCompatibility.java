package com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import me.konsolas.aac.api.AACAPI;
import me.konsolas.aac.api.AACAPIProvider;
import me.konsolas.aac.api.HackType;

public final class AACCompatibility implements ACTCompatibility {
	
	private static final Map<ACTHackType, Boolean> STATUS = new HashMap<ACTHackType, Boolean>();
	private static HackType of(ACTHackType type) {
		switch(type) {
		case FLY:
			return HackType.FLY;
		default:
			break;
		}
		return null;
	}
	
	private final Plugin aac_plugin;
	private final AACAPI        api;
	
	AACCompatibility(Plugin aac_plugin) {
		this.aac_plugin = aac_plugin;
		this.api        = AACAPIProvider.getAPI();
		
		/* load current status */
		for (ACTHackType type : ACTHackType.values()) {
			if (!STATUS.containsKey(type)) {
				check(type);
			}
		}
	}

	@Override
	public boolean enabled(ACTHackType type) {
		return STATUS.get(type);
	}

	@Override
	public void check(ACTHackType type) {
		STATUS.put(type, true);
		
		api.enableCheck(of(type));
	}

	@Override
	public void ignore(ACTHackType type) {
		STATUS.put(type, false);
		
		api.disableCheck(of(type));
	}

	@Override
	public boolean disabled(ACTHackType type) {
		return !enabled(type);
	}

	@Override
	public Plugin getAnticheatPlugin() {
		return aac_plugin;
	}
}