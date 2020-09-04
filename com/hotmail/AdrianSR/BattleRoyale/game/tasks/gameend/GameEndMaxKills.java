package com.hotmail.AdrianSR.BattleRoyale.game.tasks.gameend;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.database.StatType;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.RespawnAndPositionSender;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.GameUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;

public class GameEndMaxKills extends GameEnd {
	
	@Override
	public GameEndMaxKills Initialize() {
		/* check battle mode */
		if (!BattleModeUtils.isDeterminatedByKills(GameManager.getBattleMode())) {
			return this;
		}
		
		/* send game end titles to winner team */
		Team team = GameManager.getWinningTeam();
		BRPlayer.getBRPlayers().stream().filter(BRPlayer :: isOnline).forEach(br -> {
			String title = (team == null 
					? Lang.TIE_GAME_TITLE.getValue(true)				      /* tie game */
					: Lang.MEMBER_TEAM_WINNER_POSITION_TITLE.getValue(true)); /* winner found */
			
			/* respawn player */
			if (br.isLiving()) {
				new RespawnAndPositionSender(br, br.getPlayer().getLocation(), 
						title,
						Lang.POSITIONS_SUBTITLE.getValue(true)
				).runTaskLater(BattleRoyale.getInstance(), 2L);
			}
			
			/* won stat to winning team member */
			GameUtils.addStat(br, StatType.WON_GAMES, true);
		});
		this.initialized = true;
		return this;
	}
}
