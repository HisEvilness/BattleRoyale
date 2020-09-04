package com.hotmail.AdrianSR.BattleRoyale.game.mode;

/**
 * Represents battle mode 'Solo'
 * with a default configuration.
 * <p>
 * @author AdrianSR
 */
public final class DefaultBattleMode implements BattleMode {

	@Override
	public void onInitialize() {
		/* nothing by defualt */
	}
	
	@Override
	public double getStartHealth() {
		return 20;
	}

	@Override
	public double getMaxHealth() {
		return 20;
	}

	@Override
	public int getMaxKills() {
		return 0;
	}

	@Override
	public boolean isRedeployEnabled() {
		return false;
	}

	@Override
	public int getMaxTeams() {
		return 0;
	}

	@Override
	public int getMaxPlayersPerTeam() {
		return 1;
	}
	
	@Override
	public boolean isSolo() {
		return true;
	}

	@Override
	public boolean isReanimationEnabled() {
		return false;
	}
	
	@Override
	public int getReanimationSeconds() {
		return 0;
	}
	
	@Override
	public double getHealthAfterReanimation() {
		return 0;
	}

	@Override
	public boolean isRespawnEnabled() {
		return false;
	}
	
	@Override
	public int getRespawnSeconds() {
		return 0;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}