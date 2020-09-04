package com.hotmail.AdrianSR.BattleRoyale.game.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.permission.PrefixUtil;

/**
 * Represents a class that 
 * modify the chat messages.
 *<p>
 * @author AdrianSR.
 */
public final class MemberChatListener implements Listener {
	
	/**
	 * Construct new Members Chat modifier.
	 *<p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public MemberChatListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {
		final Player p       = event.getPlayer();
		final Member member  = BRPlayer.getBRPlayer(p);
		
		// check is team chat.
		boolean teamChat = true;
		
		// check message.
		boolean flag_a = event.getMessage ( ).startsWith ( "!" );
		boolean flag_b = GameManager.getBattleMode ( ).isSolo ( );
		if ( flag_a || flag_b ) {
			teamChat = false; 
			
			if ( flag_a ) {
				if ( event.getMessage ( ).length ( ) == 1 ) {
					event.setCancelled ( true ); // cancell because is an empty message.
					return;
				}
				
				// remove '!' from message.
				event.setMessage ( event.getMessage ( ).substring ( 1 ) );
			}
		}
		
		// get prefix.
		// TODO: Agregar prefix con los plugins de permisos.
		String prefix = StringUtil.defaultIfBlank ( PrefixUtil.getNextAvailablePrefix ( p ) , "" );
		
//		final PlayerPrefix player_prefix = new PlayerPrefix(p);
//		String prefix = ""; // new PlayerPrefix(p).get();
//		if (player_prefix.getPermissionsExPrefix() != null) {
//			prefix = player_prefix.getPermissionsExPrefix();
//		} else if (player_prefix.getPowerfulPermsPrefix() != null) {
//			prefix = player_prefix.getPowerfulPermsPrefix();
//		} else if (player_prefix.getUltraPermissionsPrefix() != null) {
//			prefix = player_prefix.getUltraPermissionsPrefix();
//		}
		
		// get format.
		String format = StringUtil.translateAlternateColorCodes ( Lang.CHAT_FORMAT.getValue(true)
				.replace(Lang.PLAYER_REPLACEMENT_KEY, "%s")
				.replace(Lang.CHAT_MODE_REPLACEMENT_KEY, ( teamChat ? Lang.CHAT_TEAM_MODE.getValue(true) : Lang.CHAT_GLOBAL_MODE.getValue(true) ))
				.replace(Lang.RANGE_REPLACEMENT_KEY, prefix)
				.replace(": " + Lang.WORD_REPLACEMENT_KEY, ": %s")
				.replace("  ", " ")); // translate colors.
		
		// set format
		event.setFormat(format);
		
		// set recipients.
		if (event.getRecipients() != null) { // check recipients.
			// clear.
			event.getRecipients().clear(); 
			// add new recipients.
			for (Member mem : BRPlayer.getBRPlayers()) { 
				// check member.
				if (mem == null || !mem.isOnline()) {
					continue;
				}
				
				// check is team chat, and add only team mates.
				if (member.hasTeam() && teamChat) {
					if (!member.getTeam().equals(mem.getTeam())) {
						continue;
					}
				}
				
				// add recipient.
				event.getRecipients().add(mem.getPlayer());
			}
		}
	}
}