package com.hotmail.AdrianSR.BattleRoyale.questions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;

public final class SingleQuestionPrompt extends ValidatingPrompt {
	
	/**
	 * Global class values.
	 */
	private static ConversationFactory factory;
	private static final Map<UUID, Conversation> CONVERSATIONS = new HashMap<UUID, Conversation>();
	
	/**
	 * Class values.
	 */
	private final String question;
	private final AcceptAnswer listener;

	public static void newPrompt(final Player player, final String question, final AcceptAnswer listener) {
		// check factory
		if (factory == null) {
			factory = new ConversationFactory(BattleRoyale.getInstance());
		}
		
		// start conversation.
		if (!player.isConversing()) {
			// create conversation.
			Conversation conv = factory.withModality(false)
					.withFirstPrompt(new SingleQuestionPrompt(question, listener)).withLocalEcho(true)
					.buildConversation(player);
			
			// begin conversation.
			conv.begin();
			
			// register conversation.
			CONVERSATIONS.put(player.getUniqueId(), conv);
		}
	}
	
	public static void endConversations() {
		for (UUID id : CONVERSATIONS.keySet()) {
			// get player and check.
			Player p = Bukkit.getPlayer(id);
			if (p == null || !p.isOnline()) {
				continue;
			}
			
			// get and check conversation.
			Conversation conversation = CONVERSATIONS.get(id);
			if (conversation == null) {
				continue;
			}
			
			// end conversation.
			if (p.isConversing()) {
				p.abandonConversation(conversation);
			}
		}
	}

	private SingleQuestionPrompt(final String question, final AcceptAnswer listener) {
		this.question = question;
		this.listener = listener;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return question;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop") || input.equalsIgnoreCase("end")
				|| input.equalsIgnoreCase("quit"))
			return Prompt.END_OF_CONVERSATION;

		if (listener.onAnswer(input))
			return Prompt.END_OF_CONVERSATION;
		else
			return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		return true;
	}
}