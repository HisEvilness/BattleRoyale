package com.hotmail.AdrianSR.BattleRoyale.util.mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;

import com.hotmail.AdrianSR.BattleRoyale.exceptions.InvalidDescriptionException;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.BattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.complex.ComplexBattleMode;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.complex.ComplexBattleModeDescription;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.complex.ComplexBattleModeLoader;
import com.hotmail.AdrianSR.BattleRoyale.game.mode.manager.BattleModeManager;
import com.hotmail.AdrianSR.BattleRoyale.game.time.border.BorderTimer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;

public final class BattleModeUtils {
	
	public static ComplexBattleMode loadComplex(File file, BattleModeManager manager) {
		JarFile           jar = null;
		URLClassLoader loader = null;
		InputStream    stream = null;
		try {
			jar                          = new JarFile(file);
//			Logger                logger = BattleRoyale.INSTANCE().getLogger();
			ClassLoader     clazz_loader = new BattleModeUtils().getClass().getClassLoader();
							      loader = new URLClassLoader(new URL[] { file.toURI().toURL() }, clazz_loader);
			List<String>           names = getClassNames(file);
			Map<String, Class<?>> loaded = new HashMap<String, Class<?>>();
			for (String name : names) {
				loaded.put(name, loader.loadClass(name));
			}
			
			JarEntry entry = jar.getJarEntry(BattleMode.COMPLEX_BATTLE_MODE_YML);
            if (entry == null) {
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain " + BattleMode.COMPLEX_BATTLE_MODE_YML));
            }
            
            stream                                   = jar.getInputStream(entry);
            ComplexBattleModeDescription description = new ComplexBattleModeDescription(stream);
            if (description.isValid()) {
            	/* info: loading plugin */
//            	BattleRoyale.getInstance().getComboLogger().info("Loading battle mode '" + description.getName() + "'....");
            	BattleRoyale.getInstance ( ).getLogger ( ).info ( "Loading battle mode '" + description.getName() + "'...." );
            	
            	/* initialize */
            	String         main = description.getMain();
            	if (StringUtils.isBlank(main)) {
            		throw new InvalidDescriptionException(new IllegalArgumentException("main is not defined"));
            	}
            	
            	Class<?> main_class = loaded.get(main);
            	if (main_class == null) {
            		 throw new InvalidDescriptionException(new IllegalArgumentException("Cannot find main class '" + main + "'"));
            	}
            	
            	Class<? extends ComplexBattleMode> mode_class;
            	try {
            		mode_class = main_class.asSubclass(ComplexBattleMode.class);
            	} catch (ClassCastException ex) {
                    throw new InvalidDescriptionException("main class '" + description.getMain() + "' does not extend ComplexBattleMode", ex);
                }
            	
            	ComplexBattleMode mode = mode_class.newInstance();
            	new ComplexBattleModeLoader(description, file).initialize(mode);
            	return mode;
            }
		} catch(Throwable t) {
			t.printStackTrace(); /* TODO: REMOVE */
			/* ignore */
		} finally {
			try {
				if (jar != null) {
					jar.close();
				}
				
				if (loader != null) {
					loader.close();
				}
				
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				/* ignore */
			}
		}
		return null;
	}
	
	private static List<String> getClassNames(File file) {
		List<String> classNames = new ArrayList<String>();
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(new FileInputStream(file));
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
					StringBuilder className = new StringBuilder();
					for (String part : entry.getName().split("/")) {
						if (className.length() != 0)
							className.append(".");
						className.append(part);
						if (part.endsWith(".class"))
							className.setLength(className.length() - ".class".length());
					}
					classNames.add(className.toString());
				}
			}
		} catch (IOException e) {
			classNames = null;
		} finally {
			try {
				if (zip != null)
					zip.close();
			} catch (IOException e) {
			}
		}
		return classNames;
	}
	
	public static boolean isDeterminatedByKills(BattleMode mode) {
		return mode.getMaxKills() > 0;
	}
	
	public static boolean isLimitedTeams(BattleMode mode) {
		return mode.getMaxTeams() > 0;
	}
	
	public static boolean isLimitedPlayersPerTeam(BattleMode mode) {
		return mode.getMaxPlayersPerTeam() > 0;
	}
	
	/**
	 * Returns whether a player is inside the safe zone or not.
	 * Note that this method also returns true if the given member is a spectator.
	 * <p>
	 * @param member the {@link Member} to check.
	 * @return true if safe.
	 */
	public static boolean isSafe(Member member) {
		BorderTimer border_timer = BorderTimer.getInstance ( );
		if ( border_timer != null ) {
			BorderShrink current_shrink = border_timer.getCurrentShrink ( );
			if ( current_shrink == null ) {
				return true;
			} else {
				ConfigurableLocation current_shrink_location = current_shrink.getLocation ( );
				if ( current_shrink_location == null || current_shrink_location.isInvalid ( ) ) {
					return true;
				} else {
					Location safe_center = current_shrink_location;
					double   safe_radius = BorderTimer.getInstance().getCurrentShrink().getRadius();
					if (LocUtils.isInsideOfImaginaryCuboid(member.getPlayer().getLocation(), safe_center, safe_radius)) {
						return true;
					}
				}
			}
		} else {
			if ( LocUtils.isInsideOfBorder ( member.getPlayer ( ) , member.getPlayer ( ).getWorld ( ).getWorldBorder ( ) ) ) {
				return true;
			}
		}
		
		if (member.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			return true; // spectators are always safe
		}
		return false;
	}
}