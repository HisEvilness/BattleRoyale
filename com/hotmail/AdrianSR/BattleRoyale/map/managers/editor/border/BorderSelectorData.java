package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrinkingSuccession;
import com.hotmail.AdrianSR.BattleRoyale.questions.border.BorderShrinkPrompt;
import com.hotmail.AdrianSR.BattleRoyale.questions.border.InitialBorderShrinkPrompt;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

public class BorderSelectorData {
	
	/**
	 * Player selections data.
	 */
	private static final Map<UUID, BorderSelectorData> SELECTIONS = new HashMap<UUID, BorderSelectorData>();
	private static ConversationFactory       CONVERSATION_FACTORY;
	
	/**
	 * Class values.
	 */
	private final UUID                        id;
	private final String                    name;
	private final BorderShrinkingSuccession data;
	private       BorderShrinkPrompt      prompt;
	
	/**
	 * Construct the player selector.
	 * 
	 * @param plugin to register listener.
	 */
	public BorderSelectorData(final Player player, final BorderShrinkingSuccession data) {
		// set vals.
		id        = player.getUniqueId();
		name      = player.getName();
		this.data = data; //new BorderShrinkingSuccession();
		
		// save.
		SELECTIONS.put(id, this);
	}
	
	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the data
	 */
	public BorderShrinkingSuccession getData() {
		return data;
	}

	/**
	 * @return the prompt
	 */
	public BorderShrinkPrompt getPrompt() {
		return prompt;
	}
	
	/**
	 * @return true if a new Selection is correctly started.
	 */
	public static void doLeftClick(final Player player, final Location point) {
		// check vals.
		if (player == null || point == null) {
			return;
		}
		
		// check is not conversing.
		if (player.isConversing()) {
			player.sendMessage(Global.THEME_FIRST_COLOR + "You cannot set initial border right now");
			return;
		}
		
		// check conversation factory.
		checkFactory();
		
		// start prompt.
		final Conversation conv = CONVERSATION_FACTORY
				.withModality(true)
				.withFirstPrompt(new InitialBorderShrinkPrompt(player, point))
				.withLocalEcho(true)
				.buildConversation(player);
		
		// begin conversation.
		conv.begin();
	}
	
	/**
	 * Add new Shrink point to selection data.
	 */
	public static void doRightClick(final Player player, final Location point) {
		// check vals.
		if (player == null || point == null) {
			return;
		}
		
		// check is not conversing.
		if (player.isConversing()) {
			player.sendMessage(Global.THEME_FIRST_COLOR + "You cannot create a point of shrinking right now!");
			return;
		}
		
		// get and check current selection.
		final BorderSelectorData currentData = getSelection(player);
		if (currentData == null || currentData.getData() == null) {
			player.sendMessage(ChatColor.RED + "You have not started a succession of shrinking!");
			return;
		}
		
		// check current prompt.
		if (currentData.getPrompt() != null) {
			player.sendMessage(Global.THEME_FIRST_COLOR + "!You are already creating a point of shrinking!");
			return;
		}
		
		// check conversation factory.
		checkFactory();
		
		// start border creator prompt.
		final Conversation conv = CONVERSATION_FACTORY
				.withModality(true)
				.withFirstPrompt(new BorderShrinkPrompt(player, new ConfigurableLocation(point)))
				.withLocalEcho(true)
				.buildConversation(player);
		
		// begin conversation.
		conv.begin();
		return;
	}
	
	/**
	 * Check conversation factory.
	 */
	private static void checkFactory() {
		if (CONVERSATION_FACTORY == null) {
			CONVERSATION_FACTORY = new ConversationFactory(BattleRoyale.getInstance());
		}
	}
	
	/**
	 * @param player that have the data.
	 * @return the current player selection.
	 */
	public static BorderSelectorData getSelection(final Player player) {
		return player != null ? getSelection(player.getUniqueId()) : null;
	}
	
	/**
	 * @param id the id of the player that have the data.
	 * @return the current player selection.
	 */
	public static BorderSelectorData getSelection(final UUID id) {
		return id != null ? SELECTIONS.get(id) : null;
	}
	
	/**
	 * @param player that have the data.
	 * @return the last player selection.
	 */
	public static BorderSelectorData clearSelection(final Player player) {
		return player != null ? clearSelection(player.getUniqueId()) : null;
	}
	
	/**
	 * @param id the id of the player that have the data.
	 * @return the last player selection.
	 */
	public static BorderSelectorData clearSelection(final UUID id) {
		return SELECTIONS.remove(id);
	}
	
	/**
	 * Get Total Selections.
	 * 
	 * @return an unmodifiable list with all selections.
	 */
	public static List<BorderSelectorData> getSelections() {
		return Collections.unmodifiableList(new ArrayList<BorderSelectorData>(SELECTIONS.values()));
	}
	
	/**
	 * Get Total Valid Selections.
	 * 
	 * @return an unmodifiable list with all valid selections.
	 */
	public static List<BorderSelectorData> getValidSelections() {
		// get valids.
		List<BorderSelectorData> valid = new ArrayList<BorderSelectorData>();
		for (BorderSelectorData data : getSelections()) {
			// check is valid
			if (data != null && data.getData().isValid()) {
				// add.
				valid.add(data);
			}
		}
		return Collections.unmodifiableList(valid);
	}
	
	/**
	 * @return the total valid selections.
	 */
	public static int getValidSelectionsCount() {
		return getValidSelections().size();
	}
}