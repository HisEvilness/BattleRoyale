package com.hotmail.AdrianSR.BattleRoyale.questions.border;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrinkingSuccession;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border.BorderSelectorData;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.TimeUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

/**
 * Represents a Border Shrink Succession maker,
 * with simple questions.
 * 
 * @author AdrianSR
 */
public class InitialBorderShrinkPrompt extends ValidatingPrompt {
	
	/**
	 * Global class values.
	 */
	private static final ChatColor PURPLE = ChatColor.LIGHT_PURPLE;
	private static final ChatColor RED    = ChatColor.RED;
	private static final ChatColor GREEN  = ChatColor.GREEN;
	
	/**
	 * Class values.
	 */
	private boolean finished;
	private int        level;
	private final UUID    id;
	private final Location    point;
	private double    damage;
	private Long           time_in_shrinking;
	private TimeUnit       unit_in_shrinking;
	private Long     time_to_start_shrinking;
	private TimeUnit unit_to_start_shrinking;
	
	/**
	 * Construct a new Border Shrink point creator.
	 * 
	 * @param p the creator.
	 */
	public InitialBorderShrinkPrompt(final Player p, final Location point) {
		// get vals.
		this.id    = p.getUniqueId();
		this.point = point;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		// get message.
		String message = "";
		
		// levels
		switch(level) {
			case 0:
			default: {
				sendMessage(PURPLE + "How long do you wants this point will take to start shrinking?", context);
				message = PURPLE + "Enter in the chat the time in the following way: " + RED + "[" + GREEN + "Time" + RED + "] [" + GREEN + "Unit" + RED + "]. Available units: " + "(seconds, minutes, hours) -> (s, m, h)";
				break;
			}
			
			case 1: {
				sendMessage(PURPLE + "How long do you wants this point will take to close?", context);
				message = PURPLE + "Enter in the chat the time in the following way: " + RED + "[" + GREEN + "Time" + RED + "] [" + GREEN + "Unit" + RED + "]. Available units: " + "(seconds, minutes, hours) -> (s, m, h)";
				break;
			}
			
			case 2: {
				sendMessage(PURPLE + "Which will be the damage by the radiation at this point?", context);
				message =    PURPLE + "Enter in the chat the damage.";
				break;
			}
		
			case 3: {
				sendMessage(PURPLE + "Which will be the initial border radius?", context);
				message =    PURPLE + "Enter in the chat the size/radius (in blocks).";
				break;
			}
		}
		return message;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		// get input.
		input = input.toLowerCase().trim();
		if (input.startsWith("/")) {
			input = input.substring(1);
		}
		
		// check is ending.
		if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("close") 
				|| input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
			sendMessage(Global.THEME_FIRST_COLOR + "Point cancelled!", context);
			return Prompt.END_OF_CONVERSATION;
		} else if (input.equalsIgnoreCase("back") || input.equalsIgnoreCase("previous") || input.equalsIgnoreCase("close")) {
			level--;
			return this;
		}
		
		// get arguments.
		String[] args = input.split(" ");
	
		// levels
		switch(level) {
		case 0: {
			// check args.
			if (args.length == 2) {
				try {
					// get time unit.
					TimeUnit unit = TimeUtils.getUnitFromName(args[1]);
					if (unit != null) {
						// get time
						int time = Integer.parseInt(args[0]);
						
						// save time
						this.time_to_start_shrinking = (long) time;
						
						// save unit.
						this.unit_to_start_shrinking = unit;
						
						// next level
						level = 1;
					} else {
						// send invalid argument message.
						sendMessage(RED + "!Invalid time unit!", context);
					}
				} catch (IllegalArgumentException e) {
					// send invalid argument message.
					sendMessage(RED + "!Invalid time unit!", context);
				}
			}
			break;
		}
		
		case 1: {
			// check args.
			if (args.length == 2) {
				try {
					// get time unit.
					TimeUnit unit = TimeUtils.getUnitFromName(args[1]);
					if (unit != null) {
						// get time
						int time = Integer.parseInt(args[0]);
						
						// save time
						this.time_in_shrinking = (long) time;
						
						// save unit.
						this.unit_in_shrinking = unit;
						
						// next level
						level = 2;
					} else {
						// send invalid argument message.
						sendMessage(RED + "!Invalid time unit!", context);
					}
				} catch (IllegalArgumentException e) {
					// send invalid argument message.
					sendMessage(RED + "!Invalid time unit!", context);
				}
			}
			break;
		}
		
		case 2: {
			// check args.
			if (args.length == 1) {
				try {
					// set damage.
					damage = Double.parseDouble(args[0]);
					
					// next level
					level = 3;
				} catch (IllegalArgumentException e) {
					// send invalid argument message.
					sendMessage(RED + "!Invalid number!", context);
				}
			}
			break;
		}
		
		case 3: {
			// check args.
			if (args.length == 1) {
				try {
					// get radio
					int radio = Integer.parseInt(args[0]);
					if (radio < BorderShrink.MIN_BORDERS_RADIUS) {
						// send invalid argument message.
						sendMessage(RED + "!The radius have to be older than " + BorderShrink.MIN_BORDERS_RADIUS + "!", context);
						return this;
					}
					
					// get new Succession.
					final BorderShrinkingSuccession succession = new BorderShrinkingSuccession();
					
					// add first shrink point.
					succession.addNextShrinkPoint(new BorderShrink(new ConfigurableLocation(point), (double) radio, damage, 
							time_in_shrinking      .longValue(),       unit_in_shrinking,
							time_to_start_shrinking.longValue(), unit_to_start_shrinking));
					
					// start new selection.
					new BorderSelectorData(Bukkit.getPlayer(id), succession);
					
					// send end message.
					sendMessage(Global.THEME_FIRST_COLOR + "A new border shrinking succession was started!", context);
					return Prompt.END_OF_CONVERSATION;
				} catch (IllegalArgumentException e) {
					// send invalid argument message.
					sendMessage(RED + "!Invalid number!", context);
				}
			}
			break;
		}
		}
		return this;
	}
	
	private void sendMessage(final String message, final ConversationContext context) {
		// send.
		context.getForWhom().sendRawMessage(message);
	}
	
	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		return true;
	}
	
	public boolean isFinished() {
		return finished;
	}
}