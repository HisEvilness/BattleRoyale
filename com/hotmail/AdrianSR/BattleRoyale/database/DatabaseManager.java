package com.hotmail.AdrianSR.BattleRoyale.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleParticle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.ParachuteColor;
import com.hotmail.adriansr.core.database.mysql.MySQL;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;

/**
 * Represents the player data in database
 * manager.
 * <p> 
 * @author AdrianSR
 */
public final class DatabaseManager extends PluginHandler {
	
	/**
	 * Global Class values.
	 */
	private static final Map<UUID, DTBPlayer> PLAYERS = new HashMap<UUID, DTBPlayer>();
	private static final String           STATS_TABLE = "br_players_stats";
	private static final String        SETTINGS_TABLE = "br_players_settings";
	private static final String       COSMETICS_TABLE = "br_players_cosmetics";
	private static       MySQL                    SQL;
	 
	/**
	 * @return true if is connected to MySQL.
	 */
	public static boolean connected() {
		return SQL != null && SQL.isConnected();
	}
	
	/**
	 * Construct new data base
	 * manager.
	 * <p>
	 * @param plugin the BattleRoyale instance.
	 */
	public DatabaseManager(final BattleRoyale plugin) {
		super(plugin);
		
		// start connection.
		try {
			// check is valid.
//			if (SQL.isValid() && Config.MYSQL_USE.getAsBoolean()) {
			if ( Config.MYSQL_USE.getAsBoolean ( ) ) {
				// make sql.
				SQL = new MySQL(
						Config.MYSQL_HOST.getAsString(), 
						Config.MYSQL_PORT.getAsInteger(), 
						Config.MYSQL_DATABASE.getAsString(), 
						Config.MYSQL_USERNAME.getAsString(), 
						Config.MYSQL_PASSWORD.getAsString(), false);
				
				try {
					// print connecting message.
					ConsoleUtil.sendPluginMessage("Connecting to MySQL...", plugin);
					
					// connect.
					SQL.connect();
					
					// print connected message.
					ConsoleUtil.sendPluginMessage("Connected to MySQL!", plugin);
					
					// check table.
					checkTables();
				} catch (SQLException e) {
					ConsoleUtil.sendPluginMessage(ChatColor.RED, "Could not connect to MySQL: ", plugin);
					e.printStackTrace();
				}
			}
		} catch(Throwable t) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "The MySQL config could not be loaded: ", plugin);
			t.printStackTrace();
		}
	}
	
	/**
	 * Check table exists.
	 */
	private static void checkTables() {
		// check is connected.
		if (SQL == null || !SQL.isConnected()) {
			return;
		}
		
		try {
			// execute stats table creator update.
			SQL.update("CREATE TABLE IF NOT EXISTS " + STATS_TABLE
					+ " (UNIQUEID VARCHAR(40), PLAYER_NAME VARCHAR(40), STAT_TYPE VARCHAR(40), QUANTITY INT(8))");

			// execute settings table creator update.
			SQL.update("CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE
					+ " (UNIQUEID VARCHAR(40), PLAYER_NAME VARCHAR(40), SETTING_TYPE VARCHAR(40), SETTING_VAL VARCHAR(40))");
			
			// execute cometics table creator update.
			SQL.update("CREATE TABLE IF NOT EXISTS " + COSMETICS_TABLE
					+ " (UNIQUEID VARCHAR(40), PLAYER_NAME VARCHAR(40), COSMETIC_TYPE VARCHAR(40), COSMETIC_VAL VARCHAR(40))");
		} catch ( IllegalStateException | SQLException ex ) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Save a player stats 
	 * in database.
	 * <p>
	 * @param player the {@link DTBPlayer} player to save.
	 */
	public static void saveData(final DTBPlayer player, boolean save_stats, boolean save_settings, boolean save_cosmetics) {
		// check is connected.
		if (SQL == null || !SQL.isConnected()) {
			return;
		}
		
		// save stats data.
		if (save_stats) {
			for (StatType stat : StatType.values()) {
				// get stat amount.
				int amount = 0;
				switch(stat) {
				case KILLS:
					amount = player.getKills();
					break;
				case LOST_GAMES:
					amount = player.getLostGames();
					break;
				case WON_GAMES:
					amount = player.getWonGames();
					break;
				}
				
				// execute update.
				if (containsStatMap(player, stat)) {
					try {
						// update.
						SQL.update("UPDATE " + STATS_TABLE + " SET QUANTITY=" + amount + " WHERE UNIQUEID='"
								+ player.getMember().getUUID().toString() + "' AND STAT_TYPE='" + stat.name() + "';");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				} else {
					try {
						// insert.
						SQL.update("INSERT INTO " + STATS_TABLE + " (UNIQUEID, PLAYER_NAME, STAT_TYPE, QUANTITY) VALUES ('"
								+ player.getMember().getUUID().toString() + "', '" + player.getMember().getName() + "', '" + stat.name() + "', "
								+ amount + ");");
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		// save settings data.
		if (save_settings) {
			for (SettingType setting : SettingType.values()) {
				// get setting value.
				String setting_val = setting.getDefaultValue().name();
				switch(setting) {
				case VEHICLE_TYPE:
					setting_val = player.getUsingVehicleType().name();
					break;
				case VEHICLE_PARTICLES:
					setting_val = player.getUsingVehicleParticle().name();
					break;
				case PARACHUTE_COLOR:
					setting_val = player.getUsingParchuteColor().name();
					break;
				}
				
				// SETTING_TYPE, SETTING_VAL
				// execute update.
				if (containsSettingMap(player, setting)) {
					try {
						// update.
						SQL.update("UPDATE " + SETTINGS_TABLE + " SET SETTING_VAL='" + setting_val + "' WHERE UNIQUEID='"
								+ player.getMember().getUUID().toString() + "' AND SETTING_TYPE='" + setting.name() + "';");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				} else {
					try {
						// insert.
						SQL.update("INSERT INTO " + SETTINGS_TABLE
								+ " (UNIQUEID, PLAYER_NAME, SETTING_TYPE, SETTING_VAL) VALUES ('"
								+ player.getMember().getUUID().toString() + "', '" + player.getMember().getName() + "', '"
								+ setting.name() + "', '" + setting_val + "');");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// save cosmetics.
		if (save_cosmetics) {
			// load current cosmetics.
			final List<Cosmetic> current = loadCosmetics(player);
			
			// load cosmetics to save.
			final List<Cosmetic> save = new ArrayList<Cosmetic>();
			
			// get purchased vehicles to save.
			for (BRVehicleType vehicle : player.getPurchasedVehicles()) {
				// check is not already saved.
				for (Cosmetic cosmetic : current) {
					if (cosmetic != null && cosmetic.valid()) {
						if (cosmetic.getType().getEnumClass() == BRVehicleType.class
								&& vehicle.equals(cosmetic.getValue())) {
							break;
						}
					}
				}

				// register to save.
				save.add(new Cosmetic(CosmeticType.VEHICLE_TYPE, vehicle));
			}

			// get purchased vehicle particles to save.
			for (BRVehicleParticle particle : player.getPurchasedVehicleParticles()) {
				// check is not already saved.
				for (Cosmetic cosmetic : current) {
					if (cosmetic != null && cosmetic.valid()) {
						if (cosmetic.getType().getEnumClass() == BRVehicleParticle.class
								&& particle.equals(cosmetic.getValue())) {
							break;
						}
					}
				}

				// register to save.
				save.add(new Cosmetic(CosmeticType.VEHICLE_PARTICLES, particle));
			}

			// get purchased parachute colors to save.
			for (ParachuteColor color : player.getPurchasedParachuteColors()) {
				// check is not already saved.
				for (Cosmetic cosmetic : current) {
					if (cosmetic != null && cosmetic.valid()) {
						if (cosmetic.getType().getEnumClass() == ParachuteColor.class
								&& color.equals(cosmetic.getValue())) {
							break;
						}
					}
				}

				// register to save.
				save.add(new Cosmetic(CosmeticType.PARACHUTE_COLOR, color));
			}
			
			
			// save cosmetics.
			for (Cosmetic cosmetic : save) {
				// check is not already saved.
				boolean already_saved = false;
				for (Cosmetic purchased : loadCosmetics(player)) {
					if (purchased.getType().equals(cosmetic.getType())) {
						if (purchased.getValue().equals(cosmetic.getValue())) {
							already_saved = true;
							break;
						}
					}
				}
				
				// check is not already saved.
				if (!already_saved) {
					try {
						// update.
						SQL.update("INSERT INTO " + COSMETICS_TABLE + " (UNIQUEID, PLAYER_NAME, COSMETIC_TYPE, COSMETIC_VAL) VALUES ('"
								+ player.getMember().getUUID().toString() + "', '" + player.getMember().getName() + "', '" + cosmetic.getType().name() + "', '"
								+ cosmetic.getValue().name() + "');");
					} catch (IllegalStateException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Load a player stats 
	 * from database.
	 * <p>
	 * @param player the {@link DTBPlayer} player to load.
	 */
	public static void loadData(final DTBPlayer player) {
		// check is connected.
		if (SQL == null || !SQL.isConnected()) {
			return;
		}
		
		// check is not already loaded.
		if (PLAYERS.containsKey(player.getMember().getUUID())) {
			return;
		}
		
		// load player stats from database.
		for (StatType stat : StatType.values()) {
			// check contains stat map.
			if (!containsStatMap(player, stat)) {
				continue;
			}

			try {
				// execute query.
				ResultSet set = SQL.query("SELECT * FROM " + STATS_TABLE + " WHERE UNIQUEID='"
						+ player.getMember().getUUID().toString() + "' AND STAT_TYPE='" + stat.name() + "';");
				
				// check result.
				if (set != null) {
					// load stats to database player.
					try {
						// add to stats.
						while (set.next()) {
							// get quantity
							int quantity = set.getInt("QUANTITY");

							// add depending stat type.
							switch (stat) {
							case KILLS:
								player.addKill(quantity);
								break;
							case LOST_GAMES:
								player.addLostGames(quantity);
								break;
							case WON_GAMES:
								player.addWonGames(quantity);
								break;
							}
						}
					} catch (SQLException e) {
						ConsoleUtil.sendPluginMessage(ChatColor.RED,
								"Could not load stat '" + stat.name() + "' for player " + player.getMember().getName() + ":",
								BattleRoyale.getInstance());
						e.printStackTrace();
					}
				}
			} catch (IllegalStateException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		// load player settings from database.
		for (SettingType setting : SettingType.values()) {
			// check contains setting map.
			if (!containsSettingMap(player, setting)) {
				continue;
			}
			
			try {
				// SETTING_TYPE, SETTING_VAL
				// execute query.
				ResultSet set = SQL.query("SELECT * FROM " + SETTINGS_TABLE + " WHERE UNIQUEID='"
						+ player.getMember().getUUID().toString() + "' AND SETTING_TYPE='" + setting.name() + "';");
				
				// check result.
				if (set != null) {
					// load settings to database player.
					try {
						// load to DTBPlayer.
						while (set.next()) {
							// get quantity
							String value = set.getString("SETTING_VAL");

							// load depending setting type.
							try {
								switch (setting) {
								case VEHICLE_TYPE:
									player.setUsingVehicleType(BRVehicleType.valueOf(value));
									break;
								case VEHICLE_PARTICLES:
									player.setUsingVehicleParticle(BRVehicleParticle.valueOf(value));
									break;
								case PARACHUTE_COLOR:
									player.setUsingParachuteColor(ParachuteColor.valueOf(value));
									break;
								}
							} catch(Throwable t) {
								ConsoleUtil.sendPluginMessage(ChatColor.RED,
										"Could not load the setting '" + setting.name() + "' for player " + player.getMember().getName() + ":",
										BattleRoyale.getInstance());
								t.printStackTrace();
							}
						}
					} catch (SQLException e) {
						ConsoleUtil.sendPluginMessage(ChatColor.RED,
								"Could not load the setting '" + setting.name() + "' for player " + player.getMember().getName() + ":",
								BattleRoyale.getInstance());
						e.printStackTrace();
					}
				}
			} catch (IllegalStateException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		// load cosmetics.
		for (Cosmetic cosmetic : loadCosmetics(player)) {
			if (cosmetic != null && cosmetic.valid()) {
				switch(cosmetic.getType()) {
				case VEHICLE_TYPE:
					player.addPurchasedVehicle((BRVehicleType) cosmetic.getValue());
					break;
				case VEHICLE_PARTICLES:
					player.addPurchasedVehicleParticle((BRVehicleParticle) cosmetic.getValue());
					break;
				case PARACHUTE_COLOR:
					player.addPurchasedParachuteColor((ParachuteColor) cosmetic.getValue());
					break;
				}
			}
		}
	}
	
	/**
	 * Check player contains stat
	 * map in database.
	 * <p>
	 * @param player the {@link DTBPlayer} player to check.
	 * @param stat the {@link StatType} to check.
	 * @return true if contains.
	 */
	private static boolean containsStatMap(final DTBPlayer player, final StatType stat) {
		if (SQL != null && SQL.isConnected()) {
			try {
				// execute query.
				ResultSet set = SQL.query("SELECT * FROM " + STATS_TABLE + " WHERE UNIQUEID='"
						+ player.getMember().getUUID().toString() + "' AND STAT_TYPE='" + stat.name() + "';");
				
				// stat quantity.
				Integer quantity = null;

				// check result.
				if (set != null) {
					// get stat quantity.
					try {
						while (set.next()) {
							quantity = set.getInt("QUANTITY");
						}
					} catch (SQLException e) {
						return false;
					}

					// check contains.
					return quantity != null;
				}
			} catch (IllegalStateException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Check player contains setting
	 * map in database.
	 * <p>
	 * @param player the {@link DTBPlayer} player to check.
	 * @param setting the {@link SettingType} to check.
	 * @return true if contains.
	 */
	private static boolean containsSettingMap(final DTBPlayer player, final SettingType setting) {
		if (SQL != null && SQL.isConnected()) {
			// execute query.
			try {
				ResultSet set = SQL.query("SELECT * FROM " + SETTINGS_TABLE + " WHERE UNIQUEID='"
						+ player.getMember().getUUID().toString() + "' AND SETTING_TYPE='" + setting.name() + "';");
				
				// setting value.
				String setting_value = null;

				// check result.
				if (set != null) {
					// get setting value.
					try {
						while (set.next()) {
							setting_value = set.getString("SETTING_VAL");
						}
					} catch (SQLException e) {
						return false;
					}

					// check contains.
					return setting_value != null;
				}
			} catch (IllegalStateException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
	
	private static List<Cosmetic> loadCosmetics(final DTBPlayer player) {
		if (SQL != null && SQL.isConnected()) {
			try {
				// execute query.
				ResultSet set = SQL.query("SELECT * FROM " + COSMETICS_TABLE + " WHERE UNIQUEID='"
						+ player.getMember().getUUID().toString() + "';");
				
				// make cosmetics list.
				final List<Cosmetic> cosmetics = new ArrayList<Cosmetic>();

				// COSMETIC_TYPE, COSMETIC_VAL
				// load player cosmetics.
				if (set != null) {
					try {
						while (set.next()) {
							// load cosmetic data.
							CosmeticType type = CosmeticType.valueOf(set.getString("COSMETIC_TYPE"));
							Enum<?>     value = Enum.valueOf(type.getEnumClass(), set.getString("COSMETIC_VAL"));

							// make cosmetic.
							Cosmetic cosmetic = new Cosmetic(type, value);

							// register cosmetic.
							if (cosmetic.valid() && !cosmetics.contains(cosmetic)) {
								cosmetics.add(cosmetic);
							}
						}
					} catch (Throwable t) {
						// ignore.
					}
				}
				return cosmetics;
			} catch (IllegalStateException | SQLException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	@Override
	protected boolean isAllowMultipleInstances() {
		return false;
	}
}