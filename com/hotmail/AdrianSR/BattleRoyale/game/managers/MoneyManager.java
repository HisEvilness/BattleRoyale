package com.hotmail.AdrianSR.BattleRoyale.game.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class MoneyManager extends PluginHandler {

	private static Economy ECONOMY;
	
	/**
	 * Construct a new Money Manager.
	 * <p> 
	 * @param instance the BattleRoyale Plugin.
	 */
	public MoneyManager(final BattleRoyale plugin) {
		super(plugin); 
		
		// check vault.
		if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The money system could not start: !Vault not found!", plugin);
			return;
		}
		
		// setup economy.
		final RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			ECONOMY = economyProvider.getProvider();
		}
		
		// check economy plugin.
		if (ECONOMY == null) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The money system could not start: !Economy plugin not found!", plugin);
			return;
		}
		
		this.register();
	}
	
	public static boolean systemEnabled() {
		return ECONOMY != null;
	}
	
	public static EconomyResponse giveMoney(Player player, double amount) {
		if (systemEnabled()) {
			return ECONOMY.depositPlayer(player, amount);
		} else {
			return null;
		}
	}
	
	public static EconomyResponse giveMoney(Player player, Money money) {
		if (systemEnabled()) {
			return ECONOMY.depositPlayer(player, money.getAsNotNullInteger());
		} else {
			return null;
		}
	}
	
	public static EconomyResponse removeMoney(Player player, double amount) {
		if (systemEnabled()) {
			return ECONOMY.withdrawPlayer(player, amount);
		} else {
			return null;
		}
	}
	
	public static double getBalance(Player player) {
		if (systemEnabled()) {
			return ECONOMY.getBalance(player);
		} else {
			return 0;
		}
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}