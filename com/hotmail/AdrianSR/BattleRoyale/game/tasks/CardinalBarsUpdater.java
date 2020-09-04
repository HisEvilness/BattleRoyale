package com.hotmail.AdrianSR.BattleRoyale.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.cardinalbar.CardinalBossBar;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;

/**
 * Represents a players
 * {@link CardinalBossBar} updater.
 * <p>
 * @author AdrianSR
 */
public class CardinalBarsUpdater extends BukkitRunnable {
	
	/**
	 * Construct a new players
	 * {@link CardinalBossBar} updater.
	 * <p>
	 * @param plugin the Plugin instance.
	 */
	public CardinalBarsUpdater(final BattleRoyale plugin) {
		// run...!!!
		this.runTaskTimer(plugin, 4L, 0L);
		
		// load lang config.
		loadLangConfig();
	}
	
	/**
	 * Load cardinal lang configuration.
	 */
	private void loadLangConfig() {
		// booleans.
		boolean invalid_letters_config_found = false;
		boolean invalid_names_config_found   = false;
		
		// load letters.
		final String north_letter = Lang.CARDINAL_BAR_NORTH.getValue(true);
		final String south_letter = Lang.CARDINAL_BAR_SOUTH.getValue(true);
		final String west_letter  = Lang.CARDINAL_BAR_WEST.getValue(true);
		final String east_letter  = Lang.CARDINAL_BAR_EAST.getValue(true);
		
		// load names.
		final String south_east_name = Lang.CARDINAL_BAR_SOUTH_EAST_NAME.getValue(true);
		final String south_west_name = Lang.CARDINAL_BAR_SOUTH_WEST_NAME.getValue(true);
		final String north_east_name = Lang.CARDINAL_BAR_NORTH_EAST_NAME.getValue(true);
		final String north_west_name = Lang.CARDINAL_BAR_NORTH_WEST_NAME.getValue(true);
		
		// check letter lengths.
		if (south_letter.length() != 1 || north_letter.length() != 1 || west_letter.length() != 1
				|| east_letter.length() != 1) {
			invalid_letters_config_found = true;
		}

		// check names lengths.
		if (south_east_name.length() != 2 || south_west_name.length() != 2 || north_east_name.length() != 2
				|| north_west_name.length() != 2) {
			invalid_names_config_found = true;
		}
		
		// print instruccions.
		if (invalid_letters_config_found || invalid_names_config_found) {
			// print invalid cardinal bar message.
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Invalid cardinal bar config: ", BattleRoyale.getInstance());
			
			// print letters instruccions.
			if (invalid_letters_config_found) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "* Invalid letters config, The cardinal bar letters should not have these characteristics: ", BattleRoyale.getInstance());
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "  - More than one letter.", BattleRoyale.getInstance());
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "  - Colors.", BattleRoyale.getInstance());
			}
			
			// print names instruccions.
			if (invalid_names_config_found) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "* Invalid names config, The cardinal bar names should not have these characteristics: ", BattleRoyale.getInstance());
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "  - More than two letter.", BattleRoyale.getInstance());
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "  - Less than two letters.", BattleRoyale.getInstance());
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "  - Colors.", BattleRoyale.getInstance());
			}
		}
		
		// load letters if valid config.
		if (!invalid_letters_config_found) {
			CardinalBossBar.NORTH_NAME = north_letter;
			CardinalBossBar.SOUTH_NAME = south_letter;
			CardinalBossBar.WEST_NAME  = west_letter;
			CardinalBossBar.EAST_NAME  = east_letter;
		}
		
		// load names if valid config.
		if (!invalid_names_config_found) {
			CardinalBossBar.NORTH_EAST_NAME = north_east_name;
			CardinalBossBar.NORTH_WEST_NAME = north_west_name;
			CardinalBossBar.SOUTH_EAST_NAME = south_east_name;
			CardinalBossBar.SOUTH_WEST_NAME = south_west_name;
		}
		
		// load color.
		try {
			if (ChatColor.valueOf(Config.CARDINAL_BAR_COLOR.toString().toUpperCase()) != null) {
				CardinalBossBar.COLOR = ChatColor.valueOf(Config.CARDINAL_BAR_COLOR.toString().toUpperCase()).toString();
			}
		} catch(Throwable t) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Invalid cardinal bar color! Available colors: ", BattleRoyale.getInstance());
			for (ChatColor color : ChatColor.values()) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "- " + color.name(), BattleRoyale.getInstance());
			}
		}
	}
	
	@Override
	public void run() {
		// update online players cardinal boss bars.
		for (Player p : Bukkit.getOnlinePlayers()) {
			// get player cardinal bossbar.
			final CardinalBossBar bar = CardinalBossBar.getCardinalBar(p);
			
			// update.
			bar.update();
		}
	}
}