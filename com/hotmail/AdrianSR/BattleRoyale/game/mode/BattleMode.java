package com.hotmail.AdrianSR.BattleRoyale.game.mode;

/**
 * Represents the battle royale 
 * modality that modify the game.
 * <p>
 * @author AdrianSR
 */
public interface BattleMode {
	
	/**
	 * Complex battle modes data. 
	 */
	public static final String COMPLEX_BATTLE_MODE_YML = "battlemode.yml";
	
	/**
	 * Yaml Battle Modes data.
	 */
	public static final String GAME_SETTINGS_SECTION = "game";
	public static final String 		START_HEALTH_KEY = "start-health";
	public static final String		  MAX_HEALTH_KEY = "max-health";
	public static final String 		   MAX_KILLS_KEY = "max-kills";
	public static final String		    REDEPLOY_KEY = "enable-parachute-redeploy";
	/* ------------------------------------------------------------------------- */
	public static final String     TEAMS_SETTINGS_SECTION = "teams"; 
	public static final String		        MAX_TEAMS_KEY = "max-teams";
	public static final String   MAX_PLAYERS_PER_TEAM_KEY = "max-players-per-team";
	public static final String 			RESUSCITATION_KEY = "enable-resuscitation";
	public static final String      RESUSCITATION_SECONDS = "resuscitation-seconds";
	public static final String HEALTH_AFTER_RESUSCITATION = "health-after-resuscitation";
	public static final String 			  	  RESPAWN_KEY = "enable-respawn";
	public static final String            RESPAWN_SECONDS = "respawn-seconds";
	
	/**
	 * Initialize this.
	 * <p>
	 * @return true if successfully.
	 */
	public void onInitialize();
	
	/**
	 * Returns the health
	 * players will have at
	 * the beginning of the game.
	 * <p>
	 * @return health at the beginning of the game.
	 */
	public double getStartHealth();
	
	/**
	 * Returns the max health
	 * players will have playing
	 * in this battle mode.
	 * <p>
	 * @return players max health in this mode.
	 */
	public double getMaxHealth();
	
	/**
	 * Returns the maximum number 
	 * of murders that can be executed 
	 * in the game.
	 * <p>
	 * @return max kills in this mode.
	 */
	public int getMaxKills();
	
	/**
	 * Returns true if the parachute
	 * redeploy is allowed in this
	 * battle mode.
	 * <p>
	 * @return true if the redeploy is allowed.
	 */
	public boolean isRedeployEnabled();
	
	/**
	 * Returns the maximum number 
	 * of teams this battle mode allows.
	 * <p>
	 * @return allowed number of teams.
	 */
	public int getMaxTeams();
	
	/**
	 * Returns the maximum number 
	 * of players this battle mode allows.
	 * <p>
	 * @return allowed number of players per team.
	 */
	public int getMaxPlayersPerTeam();
	
	/**
	 * Returns true if this
	 * allow only one player
	 * per team.
	 * <p>
	 * @return true if only one player per team is allowed.
	 */
	public boolean isSolo();
	
	/**
	 * Returns true if the re-animation
	 * is allowed in this battle mode.
	 * <p>
	 * @return true if the resuscitation is allowed.
	 */
	public boolean isReanimationEnabled();
	
	/**
	 * Time of the re-animation.
	 * <p>
	 * @return re-animation time.
	 */
	public int getReanimationSeconds ( );
	
	/**
	 * Returns health players will
	 * have after the re-animation.
	 * <p>
	 * @return health after animation
	 */
	public double getHealthAfterReanimation();
	
	/**
	 * Returns true if the respawn
	 * is allowed in this battle mode.
	 * <p>
	 * @return true if the respawn is allowed.
	 */
	public boolean isRespawnEnabled();
	
	/**
	 * Returns respawn delay
	 * in seconds.
	 * <p>
	 * @return respawn delay in seconds.
	 */
	public int getRespawnSeconds();
	
	/**
	 * Returns true if the Battle mode
	 * has a valid configuration.
	 * <p>
	 * @return has valid configuration.
	 */
	public boolean isValid();
}