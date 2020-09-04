package com.hotmail.AdrianSR.BattleRoyale.game.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hotmail.AdrianSR.BattleRoyale.database.DatabaseManager;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

/**
 * Represents a Battle Royal Members Loader. 
 * 
 * @author AdrianSR
 */
public final class MembersLoader implements Listener
{
	/**
	 * Class values.
	 */
	private final BattleRoyale plugin;
	private final Map<UUID, Member> membersMap;
	
	/**
	 * Costruct a new Member Loader.
	 * 
	 * @param plugin
	 */
	public MembersLoader(final BattleRoyale plugin, final Map<UUID, Member> membersMap) {
		// get values.
		this.plugin     = plugin;
		this.membersMap = membersMap;
		
		// load connected players.
		for (Player p : Bukkit.getOnlinePlayers()) {
			// check is not null.
			if (p != null) {
				// load
				load(p);
			}
		}
	}
	
	/**
	 * Start member loading.
	 */
	public void register() {
		// register events in this class.
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Stop member loading.
	 */
	public void unregister() {
		// unregister events in this class.
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * Load player on join the server.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(final PlayerJoinEvent eve) {
		load(eve.getPlayer());
	}
	
	/**
	 * Load a member.
	 * 
	 * @param p the player.
	 * @param id the player Unique {@link UUID}.
	 */
	public void load(final Player p) {
		// make member.
		final BRPlayer member = new BRPlayer(p.getName(), p.getUniqueId());
		
		// load database member data.
		DatabaseManager.loadData(member.getDatabasePlayer());
		
		// save member in cache.
		membersMap.put(p.getUniqueId(), member);
	}
}