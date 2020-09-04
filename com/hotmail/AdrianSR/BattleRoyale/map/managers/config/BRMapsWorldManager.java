package com.hotmail.AdrianSR.BattleRoyale.map.managers.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.adriansr.core.util.file.FileUtil;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;

/**
 * Represents a Battle Royale Worlds loader manager.
 * 
 * @author AdrianSR
 */
public final class BRMapsWorldManager {
	
	/**
	 * Maps folder temp copy sufix.
	 */
	public static final String MAPS_TEMP_COPY_FOLDER_SUFIX = "-TempCopy";

	/**
	 * Class values.
	 */
	private final BattleMap map;
	
	/**
	 * Construct a new Battle Royale world loader manager.
	 * 
	 * @param map the {@link BattleMap}.
	 */
	public BRMapsWorldManager(final BattleMap map) {
		this.map = map;
	}
	
	/**
	 * Load the map world.
	 * 
	 * @param worldFolder the folder.
	 * @param isMapBuilder check is editing the map.
	 * @return true if is correctly loaded.
	 */
	public boolean loadWorld(File worldFolder, boolean isMapBuilder) {
		// check is not already loaded.
		if (map.getWorld() != null) {
			return false;
		}
		
		// check if the world folder is a directory.
		if (worldFolder.exists() && worldFolder.isDirectory()) {
			// File Filter
			final File[] files = worldFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return name.equalsIgnoreCase("level.dat");
				}
			});

			// Check
			if (files != null && files.length == 1) {
				try {
					// Get Path
					String path = worldFolder.getPath();
					if (path.contains("plugins")) {
						path = path.substring(path.indexOf("plugins"));
					}

					// Get World Creator and check
					WorldCreator cr = new WorldCreator ( path.replace ( '\\' , '/' ) );
					cr.environment(Environment.NORMAL);
					World mapWorld = Bukkit.createWorld(cr);
					if (mapWorld != null) {
						// Get orginal Map Name
						final String olName = worldFolder.getName();
						
						// When is Map Builder
						if (!isMapBuilder) {
							// Unload original map world.
							Bukkit.unloadWorld(mapWorld.getName(), false);

							// Create Temp World Folder Copy
							final File fol = new File(worldFolder.getParent(), worldFolder.getName() + MAPS_TEMP_COPY_FOLDER_SUFIX);
							if (fol.exists()) {
								try {
//									org.apache.commons.io.FileUtils.forceDelete(fol);
//									FileUtils.forcedRemoveDir(fol);
									
									FileDeleteStrategy.FORCE.delete ( fol );
									FileUtils.forceDelete ( fol );
									
									fol.delete();
								}
								catch(Throwable t) {
									// ignore.
									return false;
								}
							}

							// MkDir
							fol.mkdir();
							
							// Copy Files
//							FileUtils.copy(worldFolder, fol);
//							FileUtil.copyDirectoryToDirectory ( worldFolder , fol );
							FileUtil.copyDirectory ( worldFolder , fol );
							
							// Change Path
							path = fol.getPath();
							path = path.contains("plugins") ? path.substring(path.indexOf("plugins")) : path;
							
							// Change WorldCreator
							cr = new WorldCreator ( path.replace ( '\\' , '/' ) );
							cr.environment(Environment.NORMAL);
							
							// Create new World.
							mapWorld = Bukkit.createWorld(cr);
							
							// Yml world name changer
							worldTempCopyYmlEdit(olName, fol.getName(), fol);
							
							// Change rules
							mapWorld.setAutoSave(false);
							mapWorld.setGameRuleValue("doMobSpawning", "false");
							mapWorld.setGameRuleValue("doFireTick", "false");
							
							// always day
							mapWorld.setTime(500L);
							mapWorld.setGameRuleValue("doDaylightCycle", "false");
							
							/* clear world border */
							BukkitReflection.clearBorder ( mapWorld );
							
							// create new map and set values.
							final BattleMap mapa = new BattleMap(fol);
							mapa.setWorld(mapWorld);
							mapa.setBuildLoaded(false);
							
							// change map.
							MapsManager.setBattleMap(mapa);
							return true;
						}
	
						// Set Map rules and disable auto save
						mapWorld.setAutoSave(false);
						mapWorld.setGameRuleValue("doMobSpawning", "false");
						mapWorld.setGameRuleValue("doFireTick", "false");
						
						// always day
						mapWorld.setTime(500L);
						mapWorld.setGameRuleValue("doDaylightCycle", "false");
						
						/* clear world border */
						BukkitReflection.clearBorder ( mapWorld );
						
						// set world.
						map.setWorld(Bukkit.getWorld(mapWorld.getName()));
						
						// set build loaded.
						map.setBuildLoaded(true);
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Remove temp copy world folder.
	 */
	public void removeTempWorld() {
		MapsManager.removeCacheWorld(map);
	}
	
	/**
	 * Modify temp copy Yml file.
	 * 
	 * @param oldName the original world name.
	 * @param newName the new world name.
	 * @param worldFolder the world folder.
	 * @throws IOException
	 */
	private static void worldTempCopyYmlEdit(final String oldName, final String newName, final File worldFolder) throws IOException {
		// get config file and his Path.
		final File file     = new File(worldFolder, BRMapsYamlManager.BATTLE_MAPS_YML_CONFIG_FILE_NAME);
		final Path filePath = Paths.get(file.getPath());
		
		// check if exists.
		if (!file.exists()) {
			return;
		}
		
		// get all lines in the file.
		final List<String> lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
		
		// change name.
		for (int x = 0; x < lines.size(); x++) {
			// get and check line.
			String lin = lines.get(x);
			if (lin == null || !lin.contains(oldName)) {
				continue;
			}
			
			// get new line text.
			String new_line = lin.substring(0, lin.lastIndexOf(oldName));
			
			// set.
			lines.set(x, new_line + newName);
		}
		
		// change lines in the file.
		Files.write(filePath, lines, Charset.forName("UTF-8"));
	}
}
