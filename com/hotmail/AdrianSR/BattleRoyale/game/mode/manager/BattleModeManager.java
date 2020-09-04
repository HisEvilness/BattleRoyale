package com.hotmail.AdrianSR.BattleRoyale.game.mode.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleModeType;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.DefaultBattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.SimpleBattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.complex.ComplexBattleMode;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.mode.BattleModeUtils;
import com.hotmail.adriansr.core.handler.PluginHandler;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.file.filter.JarFileFilter;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;

/**
 * The Battle Royale {@link BattleModes} loader.
 * <p>
 * @author AdrianSR
 */
public final class BattleModeManager extends PluginHandler {
	
	public  static final String BATTLE_MODES_FOLDER_NAME = "BattleModes";
	private static final List<BattleMode>   BATTLE_MODES = new ArrayList<BattleMode>();	
	private static       BattleMode          BATTLE_MODE = new DefaultBattleMode();
	
	/**
	 * Returns the battle mode
	 * players will play in this server.
	 * <p>
	 * @return the battle mode.
	 */
	public static BattleMode getBattleMode() {
		return BATTLE_MODE;
	}
	
	/**
	 * Sets the battle mode
	 * players will play in this serve.
	 * <p>
	 * @param mode to play.
	 */
	public static void setBattleMode(BattleMode mode) throws IllegalArgumentException, UnsupportedOperationException {
		if (mode == null || !mode.isValid()) {
			throw new IllegalArgumentException("mode cannot be null or invalid!");
		}
		
		if (GameManager.isRunning()) {
			throw new UnsupportedOperationException("the mode cannot be changed while the game is running!");
		}
		
		BATTLE_MODE = mode;
	}
	
	public BattleModeManager(final BattleRoyale plugin) {
		super(plugin);
		
		/* check folders */
		File folder = BattleModeType.SIMPLE.getDirectory();
		for (BattleModeType type : BattleModeType.values()) {
			type.mkdir();
		}
		
		saveDefaults(folder);    	   /* save default modes */
		loadSimpleBattleModes(folder); /* load battle modes in folder */
		loadMode();                    /* load battle mode the players will play in this server */
	}
	
	private void loadMode() {
		String         name = Config.BATTLE_MODE.toString();
		BattleModeType type = EnumReflection.getEnumConstant(BattleModeType.class,
				StringUtil.defaultString(Config.BATTLE_MODE_TYPE.getAsString(), "").trim().toUpperCase());
		
		if ( StringUtils.isBlank ( name ) ) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "the file of the battle mode specified in config could not be found!",
					BattleRoyale.getInstance());
			return;
		}
		
		if ( type == null ) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "the type of battle mode specified in config is invalid!", BattleRoyale.getInstance());
			return;
		}
		
		File file = new File ( type.getDirectory ( ) , 
				( name.lastIndexOf ( '.' ) == -1 ? name + ( type == BattleModeType.SIMPLE ? ".yml" : ".jar" ) : name ) );
		if ( !file.exists ( ) ) {
			ConsoleUtil.sendPluginMessage(ChatColor.RED, "the file of the battle mode specified in config could not be found!", BattleRoyale.getInstance());
			return;
		}
		
		switch(type) {
			case SIMPLE: {
				SimpleBattleMode mode = new SimpleBattleMode(file);
				if (mode.isValid()) {
					BATTLE_MODE = mode;
					ConsoleUtil.sendPluginMessage(ChatColor.GREEN,
							"Simple Battle Mode '" + Config.BATTLE_MODE.toString() + "' loaded!",
							BattleRoyale.getInstance());
				} else {
					ConsoleUtil.sendPluginMessage(ChatColor.RED,
							"The Battle Mode specified in config could not be loaded or has an invalid configuration!",
							BattleRoyale.getInstance());
				}
				break;
			}
				
			case COMPLEX: {
				if ( !new JarFileFilter ( ).accept ( file ) ) {
					ConsoleUtil.sendPluginMessage(ChatColor.RED,
							"The Jar file of the Battle Mode specified in config could not be found!",
							BattleRoyale.getInstance());
				}
				
				ComplexBattleMode mode = BattleModeUtils.loadComplex(file, this);
				if (mode != null) { /* initialize */
					BATTLE_MODE = mode;
				} else {
					ConsoleUtil.sendPluginMessage(ChatColor.RED,
							"The Complex Battle Mode specified in config could not be loaded!",
							BattleRoyale.getInstance());
				}
				break;
			}
		}
	}
	
	private void loadSimpleBattleModes(File folder) {
		Arrays.stream(folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String file_name) {
				return file_name.toLowerCase().endsWith(".yml");
			}
			
		})).forEach(file -> {
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			if (yml == null) {
				return;
			}
			
			BattleMode mode = new SimpleBattleMode(yml);
			if (mode.isValid()) {
				BATTLE_MODES.add(mode);
			}
		});
	}

	private void saveDefaults(File folder) {
		/* save only when the folder does not contains any .yml file */
		if (folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String file_name) {
				return file_name.toLowerCase().endsWith(".yml");
			}
			
		}).length > 0) {
			return;
		}
		
		/* save defaults */
		Arrays.stream(new String[] { "Solo.yml", "Duos.yml", "Squads.yml", "50vs50.yml" }).forEach(mode_file -> {
			BattleRoyale.getInstance ( ).saveResource ( mode_file , folder , false );
		});
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}