package com.hotmail.AdrianSR.BattleRoyale.main;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.command.BattleRoyaleCommandManager;
import com.hotmail.AdrianSR.BattleRoyale.enums.file.BattleRoyaleConfigFileType;
import com.hotmail.AdrianSR.BattleRoyale.enums.manager.BattleRoyalePluginManager;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MotdManager;
import com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.lobby.LobbyScoreboardHandler;
import com.hotmail.AdrianSR.BattleRoyale.game.tasks.AutoStarter;
import com.hotmail.AdrianSR.BattleRoyale.map.lobbymap.LobbyMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.metrics.Metrics;
import com.hotmail.AdrianSR.BattleRoyale.questions.SingleQuestionPrompt;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.plugin.Plugin;
import com.hotmail.adriansr.core.plugin.PluginAdapter;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.version.CoreVersion;

/**
 * A Battle Royale plugin 
 * developed by AdrianSR.
 * <p>
 * @author AdrianSR
 */
public final class BattleRoyale extends PluginAdapter {
	
	private static final Map<Class<? extends PluginHandler>, PluginHandler> MANAGERS0 = new HashMap<>();
	public  static final Map<Class<? extends PluginHandler>, PluginHandler>  MANAGERS = Collections.unmodifiableMap(MANAGERS0);
	
	private static final String           LISTENERS_PACKAGE      = "com.hotmail.AdrianSR.BattleRoyale.game.listeners";
	private static       boolean          QUALITY_ARMORY_ENABLED = false;
	private static       boolean QUALITY_ARMORY_VEHICLES_ENABLED = false;
	private static       boolean                VEHICLES_ENABLED = false;
	private static       boolean          CRACKSHOT_PLUS_ENABLED = false;
	
	/**
	 * Get the global BattleRoyalePlugin instance.
	 * <p>
	 * @return the BattleRoyalePlugin instance.
	 */
	public static BattleRoyale getInstance ( ) {
		return Plugin.getPlugin ( BattleRoyale.class );
	}
	
	/**
	 * Returns Battle Royale plugin logger.
	 * <p>
	 * @return plugin logger.
	 */
	public static Logger getPluginLogger() {
		return getInstance().getLogger();
	}
	
	/**
	 * Returns true when the QualityArmory
	 * plugin is enabled.
	 * <p>
	 * @return true if QualityArmory is enabled.
	 */
	public static boolean isQualitArmoryEnabled() {
		return QUALITY_ARMORY_ENABLED && pluginEnabled("QualityArmory");
	}
	
	/**
	 * Returns true when the QualityArmoryVehicles
	 * plugin is enabled.
	 * <p>
	 * @return true if QualityArmoryVehicles is enabled.
	 */
	public static boolean isQualitArmoryVehiclesEnabled() {
		return QUALITY_ARMORY_VEHICLES_ENABLED && pluginEnabled("QualityArmoryVehicles");
	}
	
	/**
	 * Returns true when the Vehicles
	 * plugin is enabled.
	 * <p>
	 * @return true if Vehicles is enabled.
	 */
	public static boolean isVehiclesEnabled() {
		return VEHICLES_ENABLED && pluginEnabled("Vehicles");
	}
	
	/**
	 * Returns true when the CrackshotPlus
	 * plugin is enabled.
	 * <p>
	 * @return true if csp is enabled.
	 */
	public static boolean isCrackshotPlusEnabled() {
		return CRACKSHOT_PLUS_ENABLED && pluginEnabled("CrackShotPlus");
	}
	
	/**
	 * Returns true if the plugin is enabled.
	 */
	private static boolean pluginEnabled(String name) {
		return Bukkit.getPluginManager().isPluginEnabled(name);
	}
	
	@Override
	public boolean setUp() {
		/* OLD: anti-piracy, doesn't allowed by spigot */
//		try {
//			new FirstCheck().execute(this);
//		} catch (BlockedPluginVersionException e) {
//			ConsoleUtil.sendPluginMessage(ChatColor.RED,
//					"This version of Battle Royale is no longer valid. Please download the latest version!",
//					this);
//			return false;
//		} catch (BlockedUserException e) {
//			ConsoleUtil.sendPluginMessage(ChatColor.RED,
//					"Hey " + e.getMessage() + ". This copy of the Battle Royale plugin is no longer valid. "
//							+ "Please download the latest version!",
//					this);
//			return false;
//		} catch (IOException e) {
//			/* ignore */
//		}
		
		// print enabled.
//		ConsoleUtil.sendPluginMessage(ChatColor.GREEN, "!Enabled! !Enjoy the Battle Royale " + DownloadData.A.getData() + "!", this);
		ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "!Enabled! !Enjoy the Battle Royale!" , this );
		
		/* check quality armory */
		if ( Bukkit.getPluginManager ( ).isPluginEnabled ( "QualityArmory" ) ) {
			ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "QualityArmory found!" , this );
			QUALITY_ARMORY_ENABLED = true;
		}
		
		/* check crackshot plus */
		if ( Bukkit.getPluginManager ( ).isPluginEnabled ( "CrackShotPlus" ) ) {
			ConsoleUtil.sendPluginMessage  ( ChatColor.GREEN , "Crackshot plus found!" , this );
			CRACKSHOT_PLUS_ENABLED = true;
		}
		
		// check battle maps folder.
		MapsManager.checkBattleMapsFolder ( );
        
		/* start metrics */
		new Metrics(this);
		return true;
	}
	
	@Override
	public boolean setUpHandlers ( ) {
		for ( BattleRoyalePluginManager manager : BattleRoyalePluginManager.values ( ) ) {
			try {
				MANAGERS0.put ( manager.getManagerClass ( ) ,
						manager.getManagerClass ( ).getConstructor ( BattleRoyale.class ).newInstance ( this ) );
			} catch ( InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		// initialize members loader.
		BRPlayer.initLoader(this);
		
		// initialize lobby.
		initLobby();
		
		/* motd */
		updateMotdEnabling();
		
		/* load battle map instantly */
		MapsManager.loadBattleMapInstantly();
		return true;
	}
	
	@Override
	public CoreVersion getRequiredCoreVersion ( ) {
		return CoreVersion.v2_0_0;
	}

	@Override
	public boolean setUpCommands ( ) {
		new BattleRoyaleCommandManager(this);
		return true;
	}

	@Override
	public boolean setUpListeners() {
		setUpListenersPackage ( LISTENERS_PACKAGE );

		// initialize the Game AutoStarter.
		if (MapsManager.LOBBY_MAP != null && MapsManager.LOBBY_MAP.getSpawn() != null) { // check is correctly loaded
			new AutoStarter(this).runTaskTimer(this, 20L, 20L); // initialzie auto starter task.
		}
		return true;
	}
	
	/**
	 * Load lobby.
	 */
	public void initLobby() {
		MapsManager.unloadOldWorlds(); // unload old worlds
		
		File file = BattleRoyaleConfigFileType.LOBBY_MAP_CONFIG.getFile();
		if (!getDataFolder().exists() || !file.exists() || !file.isFile()) {
			getDataFolder().mkdirs(); // make sure data folder exists
			ConsoleUtil.sendPluginMessage(ChatColor.YELLOW, "The lobby has never been set, please set it!", this);
			return; // nothing to load!
		}
		
		LobbyMap lobby = new LobbyMap(YamlConfiguration.loadConfiguration(file));
		if (lobby.getSpawn() != null) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				lobby.sendToSpawn(p);
			}
			
			ConsoleUtil.sendPluginMessage(ChatColor.GREEN, "The lobby has been loaded successfully!", this);
		} else {
			ConsoleUtil.sendPluginMessage(ChatColor.YELLOW, "The spawn of the lobby couldn't be loaded correctly, or its configuration is invalid!", this);
		}
		
		MapsManager.setLobbyMap(lobby);
		LobbyScoreboardHandler.getInstance().startUpdaterTask(); // start updating the lobby scoreboard
	}
	
	/**
	 * Update motd.
	 */
	private void updateMotdEnabling() {
		MotdManager.setAvailable(true);
		MotdManager.setRunning(false);
		MotdManager.setMaxPlayers(BRPlayer.getBRPlayers().size());
	}
	
	@Override
	public void onDisable() {
		try {
			if (MapsManager.BATTLE_MAP != null) {
				MapsManager.BATTLE_MAP.unload();
			}
			
			// leave all vehicles.
			for (Vehicle vel : Vehicle.VEHICLES) {
				if (vel != null) {
					vel.close();
				}
			}
			
			// eject players from his vehicles.
			for (Player p : Bukkit.getOnlinePlayers()) {
				try {
					p.getVehicle().eject();
					p.leaveVehicle();
					p.eject();
				} catch(Throwable t) {
					// ignore.
				}
			}
			
//			// destroy movables.
//			for (Movable movable : Movable.MOVABLES) {
//				if (movable != null) {
//					movable.destroy();
//				}
//			}
//			
//			// destroy BossBars.
//			for (BossBar bar : BossBar.CACHE.values()) {
//				if (bar != null) {
//					bar.destroy();
//				}
//			}
			
			// end conversations.
			SingleQuestionPrompt.endConversations();
			
			/* update motd */
			MotdManager.setRunning(false);
			MotdManager.setAvailable(false);
			MotdManager.setPlayersLeft(-1);
			MotdManager.setMaxPlayers(-1);
		}
		catch(Throwable t) {
			// ignore.
		}
	}

	// Testing ProtocolLib.
//	final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//	protocolManager.addPacketListener(
//			new PacketAdapter(this, ListenerPriority.LOWEST, PacketType.Play.Client.STEER_VEHICLE) {
//				@Override
//				public void onPacketReceiving(PacketEvent event) {
//					if (event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)) {
//						// get vals
//						final PacketPlayInSteerVehicle ppisv = (PacketPlayInSteerVehicle) event.getPacket().getHandle();
//						final Player p = event.getPlayer();
//						final float forward = ppisv.b();
//						final float side = ppisv.a();
//						
//						// get directions
//						final boolean space = ppisv.c();
//						final boolean front = forward > 0;
//						final boolean back  = !front;
//						final boolean left  = side > 0;
//						final boolean right = !left;
//						
//						if (space) {
//							p.sendMessage("Hiciste space");
//						}
//						
//						p.sendMessage("side: " + side);
//						p.sendMessage("forward: " + forward);
//						
//						// check is a ArmorStand,
//						if (p.getVehicle() instanceof ArmorStand) {
//							final ArmorStand a = (ArmorStand) p.getVehicle();
//						}
//					}
//				}
//			});
}
