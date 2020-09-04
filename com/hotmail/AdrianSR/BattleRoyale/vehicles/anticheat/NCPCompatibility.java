package com.hotmail.AdrianSR.BattleRoyale.vehicles.anticheat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public final class NCPCompatibility implements ACTCompatibility {
	
	private static final Map<ACTHackType, Boolean> STATUS = new HashMap<ACTHackType, Boolean>();
	private static CheckType of(ACTHackType type) {
		switch(type) {
		case FLY:
			return CheckType.MOVING_SURVIVALFLY;
		default:
			break;
		}
		return null;
	}
	
	private final Plugin  ncp_plugin;
//	private final NoCheatPlusAPI api;
	
	NCPCompatibility(Plugin ncp_plugin) {
		this.ncp_plugin = ncp_plugin;
//		this.api        = NCPAPIProvider.getNoCheatPlusAPI();
		
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
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			NCPExemptionManager.unexempt(player, of(type));
		}
	}

	@Override
	public void ignore(ACTHackType type) {
		STATUS.put(type, false);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			NCPExemptionManager.exemptPermanently(player, of(type));
		}
	}

	@Override
	public boolean disabled(ACTHackType type) {
		return !enabled(type);
	}
	
	@Override
	public Plugin getAnticheatPlugin() {
		return ncp_plugin;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
//		Player player = event.getPlayer();
		for (ACTHackType type : ACTHackType.values()) {
			if (enabled(type)) {
				check(type);
			} else {
				ignore(type);
			}
		}
	}
}