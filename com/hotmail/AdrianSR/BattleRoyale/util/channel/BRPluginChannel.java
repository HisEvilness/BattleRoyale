package com.hotmail.AdrianSR.BattleRoyale.util.channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.bungeecord.MessagingUtil;
import com.hotmail.adriansr.core.util.bungeecord.Written;

/**
 * A listener for Battle Royale Plugin Channel, 
 * which will receive notifications
 * of messages sent from a client.
 * <p>
 * @author AdrianSR
 */
public final class BRPluginChannel extends PluginHandler implements PluginMessageListener, Listener {
	
	public static BRPluginChannel getInstance ( ) {
		return (BRPluginChannel) HANDLER_INSTANCES.get ( BRPluginChannel.class );
	}

	/**
	 * Construct new BattleRoyal 
	 * plugin messaging channel.
	 * <p>
	 * @param plugin the BRSigns plugin instance.
	 */
	public BRPluginChannel(BattleRoyale plugin) {
		super(plugin);
		
		/* register channel */
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, MessagingUtil.MESSAGING_CHANNEL, this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, MessagingUtil.MESSAGING_CHANNEL);
	}
	
	/**
	 * Send player to a
	 * bungeecord server.
	 * <p>
	 * @param player Player to send.
	 * @param server target server .
	 */
	public void sendPlayer(Player player, String server) {
		/* send to target server */
		try {
			MessagingUtil.sendPluginMessage(plugin, player, new Written()
					.writeUTF(MessagingUtil.CONNECT_OTHER_ARGUMENT)
					.writeUTF(player.getName())
					.writeUTF(server));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     * <p>
     * @param channel Channel that the message was sent through.
     * @param player Source of the message.
     * @param message The raw message that was sent.
     */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
//		/* TEST: Print reponse objects */
//		for (Object repo : reponse) {
//			System.out.println("repo " + (repo != null ? repo.toString() : null));
//		}
	}

	@Override
	protected boolean isAllowMultipleInstances() {
		return false;
	}
}