package com.hotmail.AdrianSR.BattleRoyale.util;

import org.bukkit.ChatColor;

import com.hotmail.adriansr.core.util.itemstack.stainedglass.StainedGlassColor;

/**
 * Represents a global plugin variables data container.
 * 
 * @author AdrianSR
 */
public final class Global {
	
	/**
	 * PLUGIN THEME COLORS.
	 */
	public static final String            THEME_FIRST_COLOR = ChatColor.GOLD.toString();
	public static final StainedGlassColor THEME_GLASS_COLOR = StainedGlassColor.YELLOW;
	public static final String           THEME_SECOND_COLOR = ChatColor.YELLOW.toString();
	public static final String            THEME_THIRD_COLOR = ChatColor.GRAY.toString();
	
	/**
	 * SCOREBOARD TEXT. SCOREBOARD:
	 * ↓
	 */
	public static final String    A1 = new String(new char[] { (char) 8598 });
	public static final String    A2 = new String(new char[] { (char) 8673 }); // OLD: new String(new char[] { (char) 8593 });
	public static final String    A3 = new String(new char[] { (char) 8599 });
	public static final String    A4 = new String(new char[] { (char) 8601 });
	public static final String    A5 = new String(new char[] { (char) 8675 }); // OLD: new String(new char[] { (char) 8595 });
	public static final String    A6 = new String(new char[] { (char) 8600 });
	public static final String    A7 = new String(new char[] { (char) 8672 }); // OLD: new String(new char[] { (char) 8592 });
	public static final String    A8 = new String(new char[] { (char) 8674 }); // OLD: new String(new char[] { (char) 8594 });
	public static final String HEART = new String(new char[] { (char) 10084 });
	public static final String FIRST_LINE_ARROWS  = A1 + " " + A2 + " " + A3;
	public static final String SECOND_LINE_ARROWS = A7 + "   " + A8;
	public static final String THIRD_LINE_ARROWS  = A4 + " " + A5 + " " + A6;
//	public static final String SCOREBOARD_FIRST_ARROWS_LINE  = ChatColor.GREEN + FIRST_LINE_ARROWS;
//	public static final String SCOREBOARD_SECOND_ARROWS_LINE = ChatColor.GREEN + SECOND_LINE_ARROWS;
//	public static final String SCOREBOARD_THIRD_ARROWS_LINE  = ChatColor.GREEN + Global.THIRD_LINE_ARROWS + ChatColor.RESET + ChatColor.GREEN;
//	public static final String SCOREBOARD_FIRST_ARROWS_LINE  = ChatColor.GREEN + ChatColor.BOLD.toString() + FIRST_LINE_ARROWS;
//	public static final String SCOREBOARD_SECOND_ARROWS_LINE = ChatColor.GREEN + ChatColor.BOLD.toString() + SECOND_LINE_ARROWS;
//	public static final String SCOREBOARD_THIRD_ARROWS_LINE  = ChatColor.GREEN + ChatColor.BOLD.toString() + Global.THIRD_LINE_ARROWS + ChatColor.RESET + ChatColor.GREEN;
//	public static final String SCOREBOARD_A_MATE_GOOD_TEXT   = ChatColor.GREEN  + ScoreboardKeys.KEY_A_MATE_NAME_LINE.replace(Lang.NUMBER_REPLACEMENT_KEY, (Lang.NUMBER_REPLACEMENT_KEY + HEART));
//	public static final String SCOREBOARD_A_MATE_NORMAL_TEXT = ChatColor.YELLOW + ScoreboardKeys.KEY_A_MATE_NAME_LINE.replace(Lang.NUMBER_REPLACEMENT_KEY, (Lang.NUMBER_REPLACEMENT_KEY + HEART));
//	public static final String SCOREBOARD_A_MATE_BAD_TEXT    = ChatColor.RED    + ScoreboardKeys.KEY_A_MATE_NAME_LINE.replace(Lang.NUMBER_REPLACEMENT_KEY, (Lang.NUMBER_REPLACEMENT_KEY + HEART));
//	public static final String SCOREBOARD_A_MATE_KILLED_TEXT = ChatColor.GRAY   + (ScoreboardKeys.KEY_A_MATE_NAME_LINE.replace(Lang.NUMBER_REPLACEMENT_KEY, "")).trim();
	
	/**
	 * TEAM SELECTOR MENU DATA:
	 */
//	public static final Size      TEAM_SELECTOR_MENU_SIZE = Size.SIX_LINE;	
//	public static final ItemStack TEAM_SELECTOR_ITEM      = new ItemStack(Material.PAPER, 1);
//	public static final ItemStack TEAM_SELECTOR_COOP_ITEM = new ItemStack(Material.SKULL_ITEM, 1, (byte)5);
//	public static final ItemStack TEAM_SELECTOR_MAKE_ITEM = new ItemStack(Material.IRON_PICKAXE, 1);
//	public static final Size JOIN_TEAM_REQUEST_SIZE       = Size.SIX_LINE;
	
	/**
	 * TEAM CREATOR MENU DATA:
	 */
	// crear
//	public static final Size TEAM_CREATOR_MENU_SIZE = Size.SIX_LINE;
//	
//	// invitar jugador.
//	public static final ItemStack TEAM_CREATOR_MENU_INV_MEMBER_ITEM  = new ItemStack(Material.PAPER, 1);
//	public static final ItemStack TEAM_INV_ITEM                      = new ItemStack(Material.SKULL_ITEM, 1);
//	public static final Size TEAM_INV_REQ_MENU_SIZE                  = Size.THREE_LINE;
//	public static final ItemStack TEAM_INV_REQ_ACCEPT_ITEM           = new ItemStack(Material.WOOL, 1, (byte) 5);
//	public static final ItemStack TEAM_INV_REQ_REJECT_ITEM           = new ItemStack(Material.WOOL, 1, (byte) 14);
//	
//	// expulsar jugador.
//	public static final ItemStack TEAM_CREATOR_MENU_KICK_MEMBER_ITEM  = new ItemStack(Material.IRON_AXE, 1);
//	public static final ItemStack TEAM_KICKER_ITEM                    = new ItemStack(Material.SKULL_ITEM, 1);
//	
//	/**
//	 * STRUCTURE MENUS.
//	 */
//	public static final Size STRUCTURE_SELECTOR_MENU_SIZE = Size.THREE_LINE;
}