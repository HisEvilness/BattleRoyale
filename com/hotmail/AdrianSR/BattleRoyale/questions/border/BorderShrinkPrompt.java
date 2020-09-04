package com.hotmail.AdrianSR.BattleRoyale.questions.border;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border.BorderSelectorData;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

/**
 * Represents a Border Shrink Succession maker,
 * with simple questions.
 * 
 * @author AdrianSR
 */
public class BorderShrinkPrompt extends ValidatingPrompt {
	
	/**
	 * Global class values.
	 */
	private static final ChatColor PURPLE = ChatColor.LIGHT_PURPLE;
	private static final ChatColor RED    = ChatColor.RED;
	private static final ChatColor GREEN  = ChatColor.GREEN;
	
	/**
	 * Class values.
	 */
	private boolean starting;
	private boolean finished;
	private int        level;
	private final UUID    id;
	private final ConfigurableLocation    loc;
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
	public BorderShrinkPrompt(final Player p, final ConfigurableLocation loc) {
		// get vals.
		this.id  = p.getUniqueId();
		this.loc = loc;
		
		// set starting.
		starting = true;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		// get message.
		String message = "";
		
		// when is starting.
		if (starting) {
//			context.getForWhom().sendRawMessage(GOLD + "Punto para la sucesion iniciado!");
			context.getForWhom().sendRawMessage(Global.THEME_FIRST_COLOR + "New point for the succession started!");
			starting = false;
		}
		
		// levels
		switch(level) {
			case 0:
			default: {
//				sendMessage(PURPLE + "¿Cuanto tiempo tardara este punto para empezar a cerrarse?", context);
				sendMessage(PURPLE + "How long do you wants this point will take to start shrinking?", context);
				message = PURPLE + "Enter in the chat the time in the following way: " + RED + "[" + GREEN + "Time" + RED + "] [" + GREEN + "Unit" + RED + "]. Available units: " + "(seconds, minutes, hours) -> (s, m, h)";
				break;
			}
			
			case 1: {
	//			sendMessage(PURPLE + "¿Cuanto tiempo quiere que tarde este punto para cerrarse y pasar al siguente?", context);
//				sendMessage(PURPLE + "How long do you wants this point to close and move to the next?", context);
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
				sendMessage(PURPLE + "Which will be the size/radius of the border when it is at this point?", context);
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
		if (input.equalsIgnoreCase("close") || input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")) {
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
			if ( args.length == 2 ) {
				try {
					// get time unit.
					TimeUnit unit = getUnitFromName(args[1]);
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
					TimeUnit unit = getUnitFromName(args[1]);
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
//						sendMessage(RED + "El Radio tiene que ser mayor a 0!", context);
						sendMessage(RED + "!The radius must be higher than or equal to " + BorderShrink.MIN_BORDERS_RADIUS + "!", context);
						return this;
					}
					
					// add new shrink point.
					BorderSelectorData.getSelection(Bukkit.getPlayer(id)).getData().addNextShrinkPoint(new BorderShrink(loc, (double) radio, damage, 
							time_in_shrinking      .longValue(),       unit_in_shrinking,
							time_to_start_shrinking.longValue(), unit_to_start_shrinking));
					
					// send end message.
//					sendMessage(GOLD + "Punto de cerrado Agregado correctamente!", context);
					sendMessage(Global.THEME_FIRST_COLOR + "Point for the succession of shriking added successfully!", context);
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
	
	private TimeUnit getUnitFromName(String input) {
		TimeUnit u;
		switch (input.toLowerCase()) {
		case "error":
		default:
			return null;

		case "s":
		case "sec":
		case "secs":
		case "second":
		case "seconds":
			u = TimeUnit.SECONDS;
			break;

		case "m":
		case "min":
		case "mins":
		case "minute":
		case "minutes":
			u = TimeUnit.MINUTES;
			break;

		case "h":
		case "hr":
		case "hrs":
		case "hour":
		case "hours":
			u = TimeUnit.HOURS;
			break;

		case "d":
		case "day":
		case "days":
			u = TimeUnit.DAYS;
			break;
		}
		return u;
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