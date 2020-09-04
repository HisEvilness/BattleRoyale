package com.hotmail.AdrianSR.BattleRoyale.menus.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.game.BRTeam;
import com.hotmail.AdrianSR.BattleRoyale.game.Team;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.menus.BookItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.menus.MenuItem;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.adriansr.core.util.itemstack.wool.WoolColor;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public class TeamSelectorMenu extends BookItemMenu {
	
	/**
	 * Item icon data.
	 */
	public static final WoolColor JOINABLE_TEAM = WoolColor.WHITE;
	public static final WoolColor     FULL_TEAM = WoolColor.RED;
	
	private static final Map<UUID, TeamSelectorMenu> TEAM_SELECTORS = new HashMap<UUID, TeamSelectorMenu>();
	private static List<JoinTeamItem> ICONS;
	static {
		build();
	}
	
	private static void build() {
		ICONS           = new ArrayList<JoinTeamItem>();
		BattleMode mode = GameManager.getBattleMode();
		
		/* make again */
		if (BattleModeUtils.isLimitedTeams(mode)) {
			/* fit teams */
			int max_teams = Math.max(mode.getMaxTeams(), 1);
			int to_create = Math.max( ( max_teams - BRTeam.getTeams().size() ), 0);
			for (int x = 0; x < to_create; x++) {
				new BRTeam();
			}
			
			/* make icons */
			int id = 0;
			for (Team team : BRTeam.getTeams()) {
				ICONS.add(new JoinTeamItem(team, id++));
			}
		} else {
			int id = 0;
			for (Team team : BRTeam.getTeams()) {
				ICONS.add(new JoinTeamItem(team, id++));
			}
			
			/* check empty teams */
			List<Team> empty_teams = BRTeam.getTeams().stream().filter(Team :: isEmpty).collect(Collectors.toList());
			if (empty_teams.size() > 1) {
				BRTeam.getTeams().stream().filter(Team :: isEmpty).forEach(team -> {
					for (JoinTeamItem icon : new ArrayList<JoinTeamItem>(ICONS)) {
						if (icon != null && team.equals(icon.team)) {
							ICONS.remove(icon);
						}
					}
				});
			}
			
			/* create team for players */
			List<Team> joinable_teams = BRTeam.getTeams().stream().filter(team -> !team.isFull()).collect(Collectors.toList());
			if (joinable_teams.isEmpty()) {
				ICONS.add(new JoinTeamItem(new BRTeam(), id++));
			}
			
			if (ICONS.isEmpty()) {
				/* refresh empty teams */
				empty_teams = BRTeam.getTeams().stream().filter(Team :: isEmpty).collect(Collectors.toList());
				id          = 0;
				
				/* make again */
				if (empty_teams.size() > 1) {
					ICONS.add(new JoinTeamItem(empty_teams.get(0), id));
				} else {
					for (Team team : BRTeam.getTeams()) {
						ICONS.add(new JoinTeamItem(team, id++));
					}
				}
			}
		}
	}
	
	public static void refresh() {
		/* build and make an updated selector */
		build();
		final TeamSelectorMenu updated = new TeamSelectorMenu();
		
		/* update views */
		for (UUID id : TEAM_SELECTORS.keySet()) {
			TeamSelectorMenu menu = TEAM_SELECTORS.get(id);
			if (id != null && menu != null) {
				Player player = Bukkit.getPlayer(id);
				if (player != null && player.isOnline()) {
					if (watchingTeamSelector(player)) {
						/* close current */
						player.getOpenInventory().close();
						
						/* open updated */
						SchedulerUtil.runTaskLater ( () -> {
							updated.open(player);
						}, 1, BattleRoyale.getInstance());
						TEAM_SELECTORS.put(id, updated);
					}
				}
			}
		}
	}
	
	private static boolean watchingTeamSelector(Player player) {
		InventoryView view = player.getOpenInventory();
		if (view != null) {
			Inventory inventory = view.getTopInventory();
			return inventory != null && Lang.TEAM_SELECTOR_MENU_NAME.getValue(true).equals(inventory.getTitle());
		}
		return false;
	}
	
	public TeamSelectorMenu() {
		super(Lang.TEAM_SELECTOR_MENU_NAME.getValue(true), new ArrayList<MenuItem>(ICONS),
				new MenuItem[] { new LeaveTeamItem() }, false, false, null, null);
	}
	
	@Override
	public void open(Player player) {
		TEAM_SELECTORS.put(player.getUniqueId(), this);
		super.open(player);
	}
}
