package com.hotmail.AdrianSR.BattleRoyale.menus.team;

import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;

public class JoinTeamItem extends MenuItem {
	
	/**
	 * Team players will join
	 * clicking this item.
	 */
	public final Team team;
	
	/**
	 * Construct new Team Selector
	 * Item.
	 * <p>
	 * @param team the {@link Team} player will join.
	 * @param id the Team id.
	 */
	public JoinTeamItem(Team team, int id) {
		super(Lang.TEAM_SELECTOR_JOIN_ITEM_NAME.getValueReplacingNumber(id, true),
				team.isFull() ? TeamSelectorMenu.FULL_TEAM.toItemStack()
							: TeamSelectorMenu.JOINABLE_TEAM.toItemStack(),
				new String[] 
				{ 
					"", 
					Lang.TEAM_SELECTOR_ITEM_MEMBERS.getValueReplacingNumber(team.getMemberCount(), true) 
				});
		this.team = team;
	}
	
	@Override
	public void onItemClick(ItemClickEvent event) {
		Player player = event.getPlayer();
		BRPlayer   br = BRPlayer.getBRPlayer(player);
		if (team != null && !team.isFull() && !br.hasTeam()) {
			team.addMember(br);
			player.sendMessage(Lang.TEAM_SELECTOR_TEAM_JOINED_MESSAGE.getValue(true));
		}
		
		/* refresh team selector inventories */
		TeamSelectorMenu.refresh();
	}
}
