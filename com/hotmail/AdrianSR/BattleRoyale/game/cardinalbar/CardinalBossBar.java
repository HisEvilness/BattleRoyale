package com.hotmail.AdrianSR.BattleRoyale.game.cardinalbar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hotmail.adriansr.core.bossbar.BossBar;
import com.hotmail.adriansr.core.util.StringUtil;

/**
 * Represents a cardinal
 * bar for players.
 * <p>
 * @author AdrianSR
 */
public class CardinalBossBar {
	
	/**
	 * All Players Cardinal Boss Bars.
	 */
	private static final Map<UUID, CardinalBossBar> CACHE = new HashMap<UUID, CardinalBossBar>();
	
	/**
	 * Get cached {@link CardinalBossBar} 
	 * or a new, for a {@link Player}.
	 * <p>
	 * @param player the {@link Player}.
	 * @return a {@link CardinalBossBar}.
	 */
	public static CardinalBossBar getCardinalBar(final Player player) {
		return getCached(player.getUniqueId()) != null ? CACHE.get(player.getUniqueId()) : new CardinalBossBar(player);
	}
	
	/**
	 * Get cached {@link CardinalBossBar}
	 * from a {@link Player} Unique {@link UUID}.
	 * <p>
	 * @param id the Player Unique UUID.
	 * @return cached CardinalBossBar or null if dont have.
	 */
	public static CardinalBossBar getCached(final UUID id) {
		return CACHE.get(id);
	}
	
	/**
	 * Global class values.
	 */
	private static final int MAX_TITLE_LENGHT = 51;
	public  static        String        COLOR = ChatColor.WHITE.toString();
	// ENGLISH:
	public static String NORTH_NAME    = "N";
	public static String SOUTH_NAME    = "S";
	public static String WEST_NAME     = "W";
	public static String EAST_NAME     = "E";
	
	public static String SOUTH_EAST_NAME = "SE";
	public static String SOUTH_WEST_NAME = "SW";
	public static String NORTH_WEST_NAME = "NW";
	public static String NORTH_EAST_NAME = "NE";
	// ESPAÑOL:
//	private static final String NORTH_NAME    = "N";
//	private static final String SOUTH_NAME    = "S";
//	private static final String WEST_NAME     = "E";
//	private static final String EAST_NAME     = "O";
//	
//	private static final String SOUTH_EAST_NAME = "SE";
//	private static final String SOUTH_WEST_NAME = "SO";
//	private static final String NORTH_WEST_NAME = "NO";
//	private static final String NORTH_EAST_NAME = "NE";
	
	/**
	 * Between.
	 */
	public static String   BETWEEN_0_180 = "";
	public static String BETWEEN_180_360 = "";
	
	/**
	 * Class values.
	 */
	private final UUID       id;
	public final BossBar handle;

	/**
	 * Construct a new 
	 * Cardinal bar.
	 * <p>
	 * @param owner the owner {@link Player}.
	 */
	public CardinalBossBar(final Player owner) {
		id     = owner.getUniqueId();
		handle = BossBar.createBossBar(owner, "", 0.0D);
		
		/* updaet the text that goes between */
		BETWEEN_0_180 = "| . . . " + SOUTH_EAST_NAME + " . . . | . . . " + SOUTH_NAME + " . . . | . . . "
				+ SOUTH_WEST_NAME + " . . . | . . . " + WEST_NAME + " . . . | . . . " + NORTH_WEST_NAME
				+ " . . . | . . . " + NORTH_NAME + " . . . | . . . " + NORTH_EAST_NAME + " . . . | . . . " + EAST_NAME
				+ " . . . |";

		BETWEEN_180_360 = "| . . . " + NORTH_WEST_NAME + " . . . | . . . " + NORTH_NAME + " . . . | . . . "
				+ NORTH_EAST_NAME + " . . . | . . . " + EAST_NAME + " . . . | . . . " + SOUTH_EAST_NAME
				+ " . . . | . . . " + SOUTH_NAME + " . . . | . . . " + SOUTH_WEST_NAME + " . . . | . . . " + WEST_NAME
			     + " . . . |";
		
		// save in cache.
		CACHE.put(id, this);
	}
	
	/**
	 * Update displayed
	 * cardinal points.
	 */
	public void update() {
		final Player p = Bukkit.getPlayer(id);
		if (p == null || !p.isOnline()) {
			destroy(); // invalid player?, destroy!
			return;
		}

		// get degrees.
		int     degrees = (int) ((p.getLocation().getYaw() + 360) % 360);
		int real_degrees = ( degrees < 180 ) ? degrees : ( degrees - 180 );

		// between (text) to use.
		final String between_text = (degrees < 180) ? BETWEEN_0_180 : BETWEEN_180_360;

		// get between text length.
		final int text_length = between_text.length();

		// get substring number.
		int parcent   = ( ( real_degrees * 100 ) / 360 );    // get parcent of 360.
		int substring = ( ( parcent * text_length ) / 100 ); // get parcent of text length.

		// get shorten text.
//		final String text = StringUtil.limit ( between_text.substring(substring), MAX_TITLE_LENGHT);
		String text = StringUtil.limit ( between_text.substring ( substring ) , MAX_TITLE_LENGHT );

		// show coords.
		handle.setTitle(COLOR + text);
	}
	
	/**
	 * Get handle.
	 * <p>
	 * @return This {@link BossBar}.
	 */
	public BossBar getHandle() {
		return handle;
	}
	
	/**
	 * Destroy this bar.
	 */
	public void destroy ( ) {
		CACHE.remove ( id );
		
		// since AdrianSRCore 2.0.0, 'handle.destroy()' is not required any more.
//		handle.destroy ( );
	}
}