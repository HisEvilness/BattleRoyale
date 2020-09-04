package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.StringUtil;

/**
 * Represents a class that 
 * allow members to see 
 * the game status in the
 * server list.
 * <p>
 * @author AdrianSR.
 */
public final class MemberServerListPing implements Listener {
	
	/**
	 * Construct new server list ping
	 * listener.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberServerListPing ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onServerListPing(final ServerListPingEvent event) {
		/* change only when the status motd is enabled */
		if (!Config.USE_STATUS_MOTD.getAsBoolean()) {
			return;
		}
		
		// TODO: Enable if this generate problems with the BRSigns plugin:
//		if (Config.BUNGEECORD.toBoolean()) {
//			return;
//		}
		
		/* change motd */
		String motd = GameManager.isRunning() ? Lang.MOTD_IN_GAME.getValue(true) : Lang.MOTD_WAITING.getValue(true);
		if (!StringUtils.isBlank(motd)) {
			event.setMotd(motd.replace(Lang.NEW_LINE_INDICATOR_VARIABLE, String.valueOf(StringUtil.LINE_SEPARATOR)));
		}
	}
}