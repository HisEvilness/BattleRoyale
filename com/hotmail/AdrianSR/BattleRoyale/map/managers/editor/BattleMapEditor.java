package com.hotmail.AdrianSR.BattleRoyale.map.managers.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.lobbymap.LobbyMap;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsYamlManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border.BorderCreatorManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.border.BorderSelectorData;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.chests.RandomChestSelectorManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.minimap.AreaSelection;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.editor.minimap.AreaSelectorManager;
import com.hotmail.AdrianSR.BattleRoyale.menus.ActionMenuItem;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickEvent;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemClickHandler;
import com.hotmail.AdrianSR.BattleRoyale.menus.ItemMenu;
import com.hotmail.AdrianSR.BattleRoyale.util.Global;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * Represents a Battle Royale maps editor menu.
 * 
 * @author AdrianSR
 */
public class BattleMapEditor extends MapEditor {

	/**
	 * Global Battle Maps editor menu.
	 */
	private static final BattleMapEditor EDITOR = new BattleMapEditor();

	/**
	 * Construct a new battle maps editor.
	 */
	public BattleMapEditor ( ) {
		super ( Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString ( ) + "Edit a battle map" , Size.SIX_LINE );
	}

	/**
	 * Set option items.
	 */
	private void build() {
		// clear items.
		clearAllItems();
	    update();
	    
		// load map item.
		if ( MapsManager.BATTLE_MAP == null ) {
			EDITOR.addOption(22,
					new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Load battle map", new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							// get location.
							final Player p = event.getPlayer();

							// load menu.
							final File mapsFolder = new File(BattleRoyale.getInstance().getDataFolder(), MapsManager.MAPS_FOLDER_NAME);
							if (!mapsFolder.exists()) {
								// send message.
								p.sendMessage(ChatColor.RED + "The maps folder '" + MapsManager.MAPS_FOLDER_NAME + "' did not exist! Making it...");

								// make dir.
								mapsFolder.mkdir();
								return;
							}

							// get maps in the folder.
							final List<File> maps = new ArrayList<File>();
							for (File f : mapsFolder.listFiles()) {
								if (f.exists() && f.isDirectory()) {
									maps.add(f);
								}
							}
							
							// check is not empty folder.
							if (maps.isEmpty()) {
								// send folder empty message.
								p.sendMessage(ChatColor.RED + "The maps folder '" + MapsManager.MAPS_FOLDER_NAME + "' is empty!");
								return;
							}

							// open.
							new MapEditorLoader(maps).open(p);
						}
					}, new ItemStack(Material.GRASS, 1)));
			
			EDITOR.addOption(EDITOR.size.getSize() - 1, new ActionMenuItem(ChatColor.RED + "Go Back", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// open principal editor menu.
					EditorsManager.openTo(event.getPlayer());
				}
			}, new ItemStack(Material.ARROW)));
		} else {
			EDITOR.addOption(21, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Set Game Area.", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// add area selector item to inventory.
					event.getPlayer().getInventory().addItem(AreaSelectorManager.getAreaSelectorItem());
					event.getPlayer().updateInventory();

					// send message.
					event.getPlayer().sendMessage(ChatColor.GREEN + "Puff!");

					// close
					event.setWillClose(true);
				}
			}, new ItemStack(Material.DIAMOND_SWORD)));
			
			EDITOR.addOption(23, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Random Loot Chests.", 
					new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// add chests selector item to inventory.
					event.getPlayer().getInventory().addItem(RandomChestSelectorManager.getChestsSelectorItem());
					event.getPlayer().updateInventory();

					// send message.
					event.getPlayer().sendMessage(ChatColor.GREEN + "Puff!");

					// close
					event.setWillClose(true);
				}
			}, new ItemStack(Material.CHEST, 1),
					"",
					Global.THEME_THIRD_COLOR + "Add random loot chests. ",
					Global.THEME_THIRD_COLOR + "These chests will appear with",
					Global.THEME_THIRD_COLOR + "a completely random loot."));
			
			EDITOR.addOption(30, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Add spawn for players here.", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// get location.
					final Player p     = event.getPlayer();
					final Location loc = p.getLocation();
					
					// check is battle map world.
					if (!LocUtils.isOnBattleMap(p)) {
						p.sendMessage(ChatColor.RED + "!You are not on the battle map!");
						return;
					}
					
					// check player is inside of the world.
					if (!LocUtils.isInsideOfBorder(p, p.getWorld().getWorldBorder())) {
						p.sendMessage(ChatColor.RED + "!You must be inside of the initial border to add a new spawn!");
						return;
					}

					// add.
					MapsManager.BATTLE_MAP.getConfig().addSpawn(loc);
					
					// send added message.
					p.sendMessage(ChatColor.GREEN + "Spawn added!");
				}
			}, new ItemStack(Material.BED, 1),
					Arrays.asList(new String[] { 
							"", 
							Global.THEME_THIRD_COLOR + "The spawns, are",
							Global.THEME_THIRD_COLOR + "the locations where", 
							Global.THEME_THIRD_COLOR + "players will spawn",
							Global.THEME_THIRD_COLOR + "at the beginning of the game.",
							Global.THEME_THIRD_COLOR + "Be careful, vehicles will travel",
							Global.THEME_THIRD_COLOR + "in the direction you are",
							Global.THEME_THIRD_COLOR + "looking when you add a new spawn."})));

			EDITOR.addOption(32, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Create succession of shrinking for the border.",
					new ItemClickHandler() {
						@Override
						public void onItemClick(ItemClickEvent event) {
							// add border creator item to inventory.
							event.getPlayer().getInventory().addItem(BorderCreatorManager.getBorderCreatorItem());
							event.getPlayer().updateInventory();

							// send message.
							event.getPlayer().sendMessage(ChatColor.GREEN + "Puff!");

							// close
							event.setWillClose(true);
						}
					}, new ItemStack(Material.TNT, 1),
					Arrays.asList(new String[] {
							"", 
							Global.THEME_THIRD_COLOR + "The succesion of skrining for the border,",
							Global.THEME_THIRD_COLOR + "will shrink the border",
							Global.THEME_THIRD_COLOR + "of the battle map. (Like any battle royale game)."
							})));
			
			// set border configuration save item.
			if (BorderSelectorData.getValidSelectionsCount() > 0) {
				EDITOR.addOption(EDITOR.getSize().getSize() - 4,
						new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Save Borders configuration.", new ItemClickHandler() {
							@Override
							public void onItemClick(ItemClickEvent event) {
								// get Item menu to open.
								final ItemMenu to = new ItemMenu(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Set Borders configuration.",
										ItemMenu.Size.fit(BorderSelectorData.getValidSelectionsCount()));

								// get slot for the item
								int slot = 0;
								for (BorderSelectorData data : BorderSelectorData.getValidSelections()) {
									// get material for the item
									Material mate = (Bukkit.getPlayer(data.getId()) != null)
											&& (Bukkit.getPlayer(data.getId()).isOnline()) ? Material.REDSTONE_TORCH_ON
													: Material.REDSTONE_TORCH_OFF;

									// set item.
									to.setItem(slot, new ActionMenuItem(Global.THEME_SECOND_COLOR + "Use the configuration made by the player " + data.getName() + "'.", new ItemClickHandler() {
												@Override
												public void onItemClick(ItemClickEvent event) {
													// get selection data.
													BorderSelectorData finalData = BorderSelectorData.getSelection(data.getId());

													// set border shrink succession.
													MapsManager.BATTLE_MAP.getConfig().setBorderShrinkSuccession(finalData.getData());

													// send seted message.
													event.getPlayer().sendMessage(Global.THEME_SECOND_COLOR + "!Borders configuration set!");

													// open main menu.
													BattleMapEditor.openTo(event.getPlayer());
												}
											}, new ItemStack(mate, 1),
											Arrays.asList(new String[]
											{ 
											"",
											Global.THEME_SECOND_COLOR + "Use this configuration as the",
											Global.THEME_SECOND_COLOR + "ultimate Borders configuration for this map." 
											})));
									
									// ++ slot.
									slot++;
								}
								to.open(event.getPlayer());
							}
						}, new ItemStack(Material.REDSTONE_BLOCK)));
			}
			
			EDITOR.addOption(EDITOR.getSize().getSize() - 3, new ActionMenuItem(Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Save world changes.", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					// get location.
					final Player p = event.getPlayer();
					
					// check is battle map world.
					if (!LocUtils.isOnBattleMap(p)) {
						p.sendMessage(ChatColor.RED + "!You are not on the battle map!");
						return;
					}

					// save.
					MapsManager.BATTLE_MAP.getWorld().save();
					
					// send added message.
					p.sendMessage(ChatColor.GREEN + "World saved!");
				}
			}, new ItemStack(Material.GRASS, 1)));
			
			EDITOR.addOption(EDITOR.getSize().getSize() - 2, new ActionMenuItem(
					Global.THEME_SECOND_COLOR + ChatColor.BOLD.toString() + "Save configurations to the .yml file.",
					new ItemClickHandler() {
				@Override public void onItemClick ( final ItemClickEvent event ) {
					final Player player = event.getPlayer ( );
					
					BRMapsYamlManager configuration = MapsManager.BATTLE_MAP.getConfig ( );
					AreaSelection         selection = AreaSelection.getSafeSelection ( player.getUniqueId ( ) );
					
					boolean generate_minimap = false;
					
					if ( selection.isDone ( ) ) {
						Area last_area = configuration.getArea ( );
						Area      area = selection.getResult ( ).getSquared ( );
						
						configuration.setArea ( area );
						
						// here we're rendering the minimap if required.
						if ( !Objects.equals ( last_area , area ) || configuration.getMiniMap ( ) == null 
								|| configuration.getMiniMap ( ).getColors ( ) == null ) {
							generate_minimap = true;
							
							// we're making sure the server is no stopped during minimap rendering by
							// notifying this process.
							player.sendMessage ( ChatColor.GREEN 
									+ "Rendering mini map... please do not stop the server until this process has finished!" );
							ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , 
									"Rendering mini map... please do not stop the server until this process has finished!" , BattleRoyale.getInstance ( ) );
						}
					}
					
					final boolean block_flag = generate_minimap;
					Runnable       save_task = new Runnable ( ) {
						@Override public void run ( ) {
							configuration.saveConfig ( block_flag );
							
							// configuration saved successfully
							player.sendMessage ( ChatColor.GREEN + "Configuration saved!" );
							ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "Configuration saved!" , BattleRoyale.getInstance ( ) );
						}
					};
					
					// notify that we're going to save the configuration.
					player.sendMessage ( ChatColor.YELLOW + "Saving configuration..." );
					ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , "Configuration saved!" , BattleRoyale.getInstance ( ) );
					
					if ( block_flag ) {
						SchedulerUtil.runTaskAsynchronously ( save_task , BattleRoyale.getInstance ( ) );
					} else {
						SchedulerUtil.runTask ( save_task , BattleRoyale.getInstance ( ) );
					}
//					
//					// send saved message.
//					player.sendMessage(ChatColor.GREEN + "Configuration saved!");
//					
//					/* set battle map area */
//					final AreaSelection selection = AreaSelection.getSafeSelection(player.getUniqueId());
//					if ( selection.isDone ( ) ) {
//						
//						
//						MapsManager.BATTLE_MAP.getConfig ( ).saveConfig ( );
//						
//						/* send procces start message */
//						player.sendMessage(ChatColor.YELLOW + "Creating Mini Map.....");
//						
//						/* set area */
//						MapsManager.BATTLE_MAP.setArea ( selection.getResult ( ) );
//						
//						/* write colors to image thread */
//						new Thread(() -> {
//							/* get battle map and their area */
//							final BattleMap br_map = MapsManager.BATTLE_MAP;
//							
//							/* get selection area */
//							final Area area = selection.getResult();
//
//							/* renderize */
//							Map<Vector2i, EnumMapBaseColor> map  = MiniMapUtil.getMapColors(area.getMin(), area.getMax(), br_map.getWorld());
//							Map<Vector2i, WSMapColor> translated = new HashMap<>();
//							map.forEach((vector2i, enumMapBaseColor) -> translated.put(
//									vector2i.sub(new Vector2i(area.getMin().getX(), area.getMin().getZ())),
//									new WSMapColor(enumMapBaseColor, EnumMapIllumination.NORMAL)));
//
//							/* set and send update*/
//							br_map.getConfig().setMap(translated, true);
////							MiniMap.sendMatrixUpdate();
//							
//							if (player != null && player.isOnline()) {
//								player.sendMessage(ChatColor.GREEN + "Mini map Created!");
//							}
//							
//							ConsoleUtil.sendPluginMessage ( ChatColor.GREEN, "Mini map Created!", BattleRoyale.getInstance());
//						}).start();
//					}
				}
			}, new ItemStack(Material.PAPER, 1),
					Arrays.asList(new String[] { 
							"", 
							Global.THEME_THIRD_COLOR + "The configuration of this battle map",
							Global.THEME_THIRD_COLOR + "will be save in a file called '" + BRMapsYamlManager.BATTLE_MAPS_YML_CONFIG_FILE_NAME + "'",
							Global.THEME_THIRD_COLOR + "inside the folder of the world of this battle map." })));
			
			EDITOR.addOption(EDITOR.getSize().getSize() - 1, new ActionMenuItem(ChatColor.RED + "Go to lobby.", new ItemClickHandler() {
				@Override
				public void onItemClick(ItemClickEvent event) {
					Player p = event.getPlayer();
					
					// open main editors menu.
					EditorsManager.openTo(p);

					// send to lobby.
					LobbyMap lobby_map = MapsManager.LOBBY_MAP;
					if (lobby_map == null || lobby_map.getSpawn() == null) {
						p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
						
						/* print lobby spawn not found */
						ConsoleUtil.sendPluginMessage ( ChatColor.RED, "The Lobby or its spawn is not set!", BattleRoyale.getInstance());
					} else {
						p.teleport(lobby_map.getSpawn());
					}
					p.sendMessage(ChatColor.GREEN + "Puff!");
				}
			}, new ItemStack(Material.ARROW, 1),
					Arrays.asList(new String[] {})));
		}

		// update.
		update();
	}

	/**
	 * open the maps editor.
	 * 
	 * @param p the target.
	 */
	public static void openTo(final Player p) {
		EDITOR.build ( );
		
		// send to map if is already loaded.
		if (MapsManager.BATTLE_MAP != null && !LocUtils.isOnBattleMap(p)) {
			// get random spawn.
			final Location rand = MapsManager.BATTLE_MAP.getConfig().getRandomSpawn();
			
			// send.
			p.teleport(rand != null ? rand : MapsManager.BATTLE_MAP.getWorld().getSpawnLocation());
			p.setGameMode ( GameMode.CREATIVE );
			// this avoid the player to fall
			p.setFlying ( true );
			
			/* give minimap */
			if ( MapsManager.BATTLE_MAP.getArea ( ) != null ) {
				SchedulerUtil.runTaskLater ( ( ) -> {
					if (p.isOnline()) {
						p.getInventory().clear();
						BattleItems.MINI_MAP.giveToPlayer(p);
					}
				}, 10, BattleRoyale.getInstance());
			}
		}

		// open.
		EDITOR.open(p);
	}

	/**
	 * Update the maps editor menu.
	 * 
	 * @param p the target.
	 */
	public static void updateTo(final Player p) {
		// update.
		EDITOR.update(p);
	}

	/**
	 * Update menu.
	 */
	public static void refresh() {
		// build.
		EDITOR.build();

		// update.
		EDITOR.update();
	}

	/**
	 * Map editor loader menu.
	 * 
	 * @author AdrianSR
	 */
	private final class MapEditorLoader extends ItemMenu {

		/**
		 * Construct a ne map editor loader.
		 * <p>
		 * @param maps the maps on the maps folder.
		 */
		public MapEditorLoader(final List<File> maps) {
			super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Load battle map world", Size.fit(maps.size()));
			
			int slot = 0;
			for (File f : maps) { // create and set the map selector action item.
				setItem(slot, new ActionMenuItem(Global.THEME_SECOND_COLOR + f.getName(), new ItemClickHandler() {
					@Override
					public void onItemClick(ItemClickEvent event) {
						try {
							final Player p = event.getPlayer();
							
							// create map and check his world.
							final BattleMap map = new BattleMap(f);
							
							/* compare map with the already load map*/
							boolean already_load = false;
							if (MapsManager.BATTLE_MAP != null) {
								if (MapsManager.BATTLE_MAP.getFolder().equals(map.getFolder())) {
									already_load = true;
								}
							}
							
							/* load map */
							if (!already_load) {
								if (map.getWorldManager().loadWorld(f, true)) {
									// check is the map has been loaded correctly.
									if (map.getWorld() == null) {
										p.sendMessage(ChatColor.RED + "The world of this battle map could not be loaded!");
										return;
									}
									
									// config must be reloaded because to world is now available.
									map.reloadConfig ( );
									
									// prepare world to config.
									map.prepareWorldToConfig();

									// set.
									MapsManager.setBattleMap(map);

									// refresh editor.
									refresh();

									// message.
									p.sendMessage(ChatColor.GREEN + "Map loaded!");
									ConsoleUtil.sendPluginMessage ( "Map loaded!", BattleRoyale.getInstance());
								} else {
									p.sendMessage(ChatColor.RED + "The world of this battle map could not be loaded!");
								}
							}
							
//							/* load minimap */ // not required anymore
//							if (Config.MINIMAP_SAFE_LOAD.getAsBoolean()) {
//								new Thread(() -> {
//									map.getConfig().restoreMiniMap();
//								}).start();
//							} else {
//								map.getConfig().restoreMiniMap();
//							}

							// open.
							openTo(p);
						} catch(OutOfMemoryError t) {
							ConsoleUtil.sendPluginMessage ( ChatColor.RED, "There is not enough memory to load this Map!", BattleRoyale.getInstance());
						}
					}
				}, new ItemStack(Material.MAP, 1)));

				// ++ slot.
				slot++;
			}
		}
	}
	
//	private final class AddVehiclesMenu extends ItemMenu {
//		
//		public AddVehiclesMenu() {
//			super(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Supported Vehicle Plugins", Size.SIX_LINE);
//			for (VehiclesPlugin plugin : VehiclesPlugin.enabledValues()) {
//				switch(plugin) { /* TODO: All new compatible vehicles plugins must be added here */
//				case QUALITY_ARMORY_VEHICLES:
//					setItem(0, new ActionMenuItem(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "QualityArmoryVehicles", event -> {
//						new AddQAVehiclesMenu().open(event.getPlayer());
//					}, new ItemStack(Material.IRON_BARDING)));
//					break;
//				case VEHICLES:
//					setItem(1, new ActionMenuItem(Global.THEME_FIRST_COLOR + ChatColor.BOLD.toString() + "Vehicles", event -> {
//						new AddVehiclesVehiclesMenu().open(event.getPlayer());
//					}, new ItemStack(Material.IRON_BARDING)));
//					break;
//				default:
//					break;
//				}
//			}
//		}
//		
//		@Override
//		public void open(Player player) { // None of the compatible vehicle plugins could be found.
//			if (VehiclesPlugin.enabledValues().length == 0) {
//				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "None of the compatible vehicle plugins could be found: ", BattleRoyale.INSTANCE());
//				for (VehiclesPlugin plugin : VehiclesPlugin.values()) {
//					ConsoleUtil.sendPluginMessage ( ChatColor.RED, "- " + plugin.getPluginName(), BattleRoyale.INSTANCE());
//				}
//				return;
//			}
//			super.open(player);
//		}
//	}
}