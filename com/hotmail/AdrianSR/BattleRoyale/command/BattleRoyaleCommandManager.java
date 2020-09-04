package com.hotmail.AdrianSR.BattleRoyale.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.EditorsManager;
import com.hotmail.adriansr.core.command.CommandArgument;
import com.hotmail.adriansr.core.command.CommandHandler;
import com.hotmail.adriansr.core.command.CommandHelpArgument;
import com.hotmail.adriansr.core.util.StringUtil;

/**
 * Represents the '/BattleRoyale' command class executor.
 * <p>
 * @author AdrianSR / Monday 04 November, 2019 / 03:05 PM
 */
public final class BattleRoyaleCommandManager extends CommandHandler {

	/**
	 * Construct the new /BattleRoyale command manager.
	 * <p>
	 * @param plugin the BattleRoyale plugin instance.
	 */
	public BattleRoyaleCommandManager ( BattleRoyale plugin ) {
		super ( plugin , "BattleRoyale" ,  
				
				
			// 'editor' argument
			new BattleRoyaleCommandArgument ( ) {
			
			@Override public void execute ( CommandSender sender , Command command , String label , String [ ] subargs ) {
				if ( sender instanceof Player ) {
					if ( sender.isOp ( ) ) {
						EditorsManager.openTo ( (Player) sender );
					} else {
						sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
					}
				} else {
					sender.sendMessage ( ChatColor.RED + "Cannot execute this command from console!" );
				}
			}

			@Override public List < String > tab ( CommandSender sender , Command command , String alias , String [ ] subargs ) {
				return null;
			}
			
			@Override public String getName ( ) {
				return "editor";
			}

			@Override public String getDescription ( ) {
				return "Opens the configuration editor";
			}
			
			// 'start' argument
		} , new BattleRoyaleCommandArgument ( ) {
			
			@Override public void execute ( CommandSender sender , Command command , String label , String [ ] subargs ) {
				if ( sender.isOp ( ) ) {
					if ( GameManager.isRunning ( ) ) {
						sender.sendMessage ( ChatColor.RED + "The game is already running!" );
					} else {
						GameManager.startGame ( );
						
						// we're immediately checking if the game started correctly.
						if ( GameManager.isNotRunning ( ) ) {
							sender.sendMessage ( ChatColor.RED + "Couldn't start the game" + ( sender instanceof Player ? ", check the console." : "!" ) );
						}
					}
				} else {
					sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
				}
			}

			@Override public List < String > tab ( CommandSender sender , Command command , String alias , String [ ] subargs ) {
				return null;
			}
			
			@Override public String getName ( ) {
				return "start";
			}

			@Override public String getDescription ( ) {
				return "Starts the game";
			}
			
			// 'stop' argument
		} , new BattleRoyaleCommandArgument ( ) {
			
			@Override public void execute ( CommandSender sender , Command command , String label , String [ ] subargs ) {
				if ( sender.isOp ( ) ) {
					if ( GameManager.isNotRunning ( ) ) {
						sender.sendMessage ( ChatColor.RED + "The game has never been started!" );
					} else {
						GameManager.stopGame ( );
						
						// we're immediately checking if the game was stopped correctly.
						if ( GameManager.isRunning ( ) ) {
							sender.sendMessage ( ChatColor.RED + "Couldn't stop the game" + ( sender instanceof Player ? ", check the console." : "!" ) );
						}
					}
				} else {
					sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
				}
			}

			@Override public List < String > tab ( CommandSender sender , Command command , String alias , String [ ] subargs ) {
				return null;
			}
			
			@Override public String getName ( ) {
				return "stop";
			}

			@Override public String getDescription ( ) {
				return "Stops the game";
			}
		} );
		
		// 'help' argument.
		setHelpArgument ( new CommandHelpArgument ( ) {
			@Override public void execute ( CommandSender sender , Command command , String label , String [ ] subargs ) {
				if ( sender instanceof Player && !sender.isOp ( ) ) {
					sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
					return;
				}
				
				// we're using a string builder to avoid other messages to interfere this.
				StringBuilder message = new StringBuilder ( );
				
				for ( CommandArgument abs_argument : BattleRoyaleCommandManager.this.getArguments ( ) ) {
					BattleRoyaleCommandArgument argument = (BattleRoyaleCommandArgument) abs_argument;
					
					message.append ( ChatColor.GOLD + ChatColor.BOLD.toString ( ) + "- " 
								+ StringUtil.capitalize ( argument.getName ( ) ) + "\n" );
					message.append ( "   " + ChatColor.GOLD + argument.getDescription ( ) + "\n\n" );
				}
				
				sender.sendMessage ( message.toString ( ) );
			}
			
			@Override public List<String> tab ( CommandSender sender , Command command , String alias , String [ ] subargs ) {
				return null;
			}
		} );
	}
}

//package com.hotmail.AdrianSR.BattleRoyale.command;
//
//import java.util.List;
//
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//
//import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
//import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
//import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.EditorsManager;
//import com.hotmail.AdrianSR.core.command.CustomCommandArgument;
//import com.hotmail.AdrianSR.core.command.CustomCommandManager;
//import com.hotmail.AdrianSR.core.util.CommandUtils;
//
///**
// * Represents the '/BattleRoyale' command class executor.
// * <p>
// * @author AdrianSR / Monday 04 November, 2019 / 03:05 PM
// */
//public final class BattleRoyaleCommandManager extends CustomCommandManager {
//
//	/**
//	 * Construct the new /BattleRoyale command manager.
//	 * <p>
//	 * @param plugin the BattleRoyale plugin instance.
//	 */
//	public BattleRoyaleCommandManager(BattleRoyale plugin) {
//		super(plugin, "BattleRoyale", true);
//		
//		this.registerArgument(new CustomCommandArgument() {
//			@Override public String getName()         { return "editor"; }
//			@Override public boolean isAllowConsole() { return false; }
//			@Override public String getUsageMessage() { return "/BattleRoyale editor"; }
//			@Override public String getDescription()  { return "Opens the maps editor"; }
//			
//			@Override
//			public boolean execute(CommandSender sender, Command command, String... sub_args) {
//				if ( sender.isOp ( ) ) {
//					EditorsManager.openTo(CommandUtils.getPlayer(sender));
//				} else {
//					sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
//				}
//				return true;
//			}
//			
//			@Override
//			public List<String> tab(CommandSender sender, Command command, String... sub_args) {
//				return null;
//			}
//		});
//		
//		this.registerArgument(new CustomCommandArgument() {
//			@Override public String getName()         { return "start"; }
//			@Override public boolean isAllowConsole() { return true; }
//			@Override public String getUsageMessage() { return "/BattleRoyale start"; }
//			@Override public String getDescription()  { return "Starts the game"; }
//			
//			@Override
//			public boolean execute(CommandSender sender, Command command, String... sub_args) {
//				if ( !sender.isOp ( ) ) {
//					sender.sendMessage ( ChatColor.RED + "You don't have permissions to use this command!" );
//					return true;
//				}
//				
//				if (GameManager.isRunning()) {
//					sender.sendMessage(ChatColor.RED + "!The game is already running!");
//					return true;
//				}
//
//				GameManager.startGame();
//				if (GameManager.isNotRunning()) {
//					sender.sendMessage(ChatColor.RED + "The game could not start"
//							+ ( CommandUtils.isPlayer(sender) ? ", check the console." : "!" ));
//				}
//				return true;
//			}
//			
//			@Override
//			public List<String> tab(CommandSender sender, Command command, String... sub_args) {
//				return null;
//			}
//		});
//	}
//}