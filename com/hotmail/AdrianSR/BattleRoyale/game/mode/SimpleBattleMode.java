package com.hotmail.AdrianSR.BattleRoyale.game.mode;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents the battle mode
 * that load its modifiers
 * from a {@link YamlConfiguration}
 * <p>
 * @author AdrianSR
 */
public class SimpleBattleMode implements BattleMode {
	
	public static final Double    	  		 DEFAULT_START_HEALTH = 20.0;
	public static final Double     			   DEFAULT_MAX_HEALTH = 20.0;
	public static final Integer        		    DEFAULT_MAX_KILLS = 0;
	public static final Boolean      	         DEFAULT_REDEPLOY = false;
	public static final Integer      	        DEFAULT_MAX_TEAMS = 0;
	public static final Integer		 DEFAULT_MAX_PLAYERS_PER_TEAM = 1;
	public static final Boolean 		    DEFAULT_RESUSCITATION = false;
	public static final Integer     DEFAULT_RESUSCITATION_SECONDS = 0;
	public static final Double DEFAULT_HEALTH_AFTER_RESUSCITATION = 6.0;
	public static final Boolean        	 	      DEFAULT_RESPAWN = false;
	public static final Integer             DEFAULT_RESPAWN_DELAY = 0;
	
	/**
	 * Battle Mode configuration yml.
	 */
	private final YamlConfiguration yml;
	
	/**
	 * Construct the Simple battle mode.
	 * <p>
	 * @param yml Yaml Configuration file.
	 */
	public SimpleBattleMode(YamlConfiguration yml) {
		this.yml = yml;
	}
	
	public SimpleBattleMode(File yml_file) {
		this.yml = YamlConfiguration.loadConfiguration(yml_file);
	}
	
	@Override
	public void onInitialize() {
		/* do nothing */
	}

	@Override
	public double getStartHealth() {
		return get(GAME_SETTINGS_SECTION + "." + START_HEALTH_KEY, DEFAULT_START_HEALTH);
	}

	@Override
	public double getMaxHealth() {
		return get(GAME_SETTINGS_SECTION + "." + MAX_HEALTH_KEY, DEFAULT_MAX_HEALTH);
	}

	@Override
	public int getMaxKills() {
		return get(GAME_SETTINGS_SECTION + "." + MAX_KILLS_KEY, DEFAULT_MAX_KILLS);
	}
	
	@Override
	public boolean isRedeployEnabled() {
		return get(GAME_SETTINGS_SECTION + "." + REDEPLOY_KEY, DEFAULT_REDEPLOY);
	}

	@Override
	public int getMaxTeams() {
		return get(TEAMS_SETTINGS_SECTION + "." + MAX_TEAMS_KEY, DEFAULT_MAX_TEAMS);
	}

	@Override
	public int getMaxPlayersPerTeam() {
		return get(TEAMS_SETTINGS_SECTION + "." + MAX_PLAYERS_PER_TEAM_KEY, DEFAULT_MAX_PLAYERS_PER_TEAM);
	}

	@Override
	public boolean isSolo() {
		return getMaxPlayersPerTeam() <= 1;
	}
	
	@Override
	public boolean isReanimationEnabled() {
		return get(TEAMS_SETTINGS_SECTION + "." + RESUSCITATION_KEY, DEFAULT_RESUSCITATION);
	}
	
	@Override
	public int getReanimationSeconds() {
		return get(TEAMS_SETTINGS_SECTION + "." + RESUSCITATION_SECONDS, DEFAULT_RESUSCITATION_SECONDS);
	}
	
	@Override
	public double getHealthAfterReanimation() {
		return get(TEAMS_SETTINGS_SECTION + "." + HEALTH_AFTER_RESUSCITATION, DEFAULT_HEALTH_AFTER_RESUSCITATION);
	}

	@Override
	public boolean isRespawnEnabled() {
		return get(TEAMS_SETTINGS_SECTION + "." + RESPAWN_KEY, DEFAULT_RESPAWN);
	}
	
	@Override
	public int getRespawnSeconds() {
		return get(TEAMS_SETTINGS_SECTION + "." + RESPAWN_SECONDS, DEFAULT_RESPAWN_DELAY);
	}
	
	@Override
	public boolean isValid() {
		return yml != null
				&& yml.isSet(TEAMS_SETTINGS_SECTION + "." + MAX_TEAMS_KEY)
				&& yml.isSet(TEAMS_SETTINGS_SECTION + "." + MAX_PLAYERS_PER_TEAM_KEY);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T get(String key, T def) {
		if (def instanceof Integer) {
			return (T) getInt(key, (Integer) def);
		} else if (def instanceof Double) {
			return (T) getDouble(key, (Double) def);
		} else if (def instanceof Boolean) {
			return (T) getBoolean(key, (Boolean) def);
		}
		return def;
	}

	private Integer getInt(String key, Integer def) {
		return yml.isInt(key) ? yml.getInt(key) : def;
	}
	
	private Double getDouble(String key, Double def) {
		return yml.isDouble(key) ? yml.getDouble(key) : def;
	}
	
	private Boolean getBoolean(String key, Boolean def) {
		return yml.isBoolean(key) ? yml.getBoolean(key) : def;
	}
}