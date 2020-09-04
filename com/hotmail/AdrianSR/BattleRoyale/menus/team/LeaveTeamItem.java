package com.hotmail.AdrianSR.BattleRoyale.menus.team;

import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.adriansr.core.util.itemstack.wool.WoolColor;

public class LeaveTeamItem extends MenuItem {
	
	public LeaveTeamItem() {
		super(Lang.TEAM_SELECTOR_LEAVE_ITEM_NAME.getValue(true), WoolColor.BLACK.toItemStack());
	}

	@Override
	public void onItemClick(ItemClickEvent event) {
		Player player = event.getPlayer();
		BRPlayer   br = BRPlayer.getBRPlayer(player);
		if (br.hasTeam()) {
			br.getTeam().removeMember(br);
			player.sendMessage(Lang.TEAM_SELECTOR_TEAM_LEFT_MESSAGE.getValue(true));			
		}
		
		/* refresh team selector inventories */
		TeamSelectorMenu.refresh();
	}
}
