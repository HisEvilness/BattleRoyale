package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.Vector;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.time.border.BorderTimer;
import com.hotmail.AdrianSR.BattleRoyale.map.Area;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.BattleMap;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrink;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.border.BorderShrinkingSuccession;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.config.BRMapsYamlManager;
import com.hotmail.AdrianSR.BattleRoyale.util.configurable.location.ConfigurableLocation;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.server.Version;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Wednesday 02 September, 2020 / 01:22 PM
 */
public class MiniMapRenderer extends MapRenderer {
	
	/**
	 * Distance between border cursors measured in pixels.
	 */
	protected static final int DISTANCE_BETWEEN_BORDER_CURSORS = 10;
	
	/**
	 * TODO: Description
	 * <p>
	 * @author AdrianSR / Wednesday 02 September, 2020 / 02:42 PM
	 */
	protected static class MapLocation {
		
		protected final int          x;
		protected final int          y;
		protected final byte direction;
		
		protected MapLocation ( int x , int y , byte direction ) {
			this.x         = x;
			this.y         = y;
			this.direction = (byte) ( direction & 15 );
		}
		
		protected boolean isOffOfLimits ( ) {
			return x <= -128 || x >= 127 || y <= -128 || y >= 127;
		}
	}
	
	public MiniMapRenderer ( ) {
		super ( true );
	}
	
	@Override @SuppressWarnings ( "deprecation" )
	public void render ( MapView view , MapCanvas canvas , Player player ) {
		BattleMap   map = MapsManager.BATTLE_MAP;
		MiniMap minimap = map.getMiniMap ( );
		if ( map == null || minimap == null || minimap.getColors ( ) == null ) {
			return;
		}
		
		boolean edit_mode = GameManager.isNotRunning ( );
		MiniMapZoom  zoom = MiniMapZoomPreferences.getPreferences ( player ).getZoom ( );
		Area display_area = map.getArea ( );
		
		// here we're drawing the map
		if ( zoom == MiniMapZoom.NORMAL ) {
			for ( int x = 0 ; x < 128 ; x ++ ) {
				for ( int y = 0 ; y < 128 ; y ++ ) {
					double d0 = (double) x / 128.0D;
					double d1 = (double) y / 128.0D;
					int range = minimap.getColors ( ).capacity;
					
					int rgb = minimap.getColors ( ).get ( (int) ( range * d0 ) , (int) ( range * d1 ) );
					if ( rgb == 0 ) {
						canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
					} else {
						canvas.setPixel ( x , y , MapPalette.matchColor ( new Color ( rgb ) ) );
					}
				}
			}
		} else {
			Vector3i location = toVector3i ( player.getLocation ( ) );
			int display_range = zoom.getDisplayRange ( );
			
			display_area = new Area ( 
					location.sub ( display_range / 2 , 0.0D , display_range / 2 ) ,
					location.add ( display_range / 2 , 0.0D , display_range / 2 ) );
			
			int x_offset = display_area.getMin ( ).getX ( ) - map.getArea ( ).getMin ( ).getX ( );
			int y_offset = display_area.getMin ( ).getZ ( ) - map.getArea ( ).getMin ( ).getZ ( );
			
			for ( int x = 0 ; x < 128 ; x ++ ) {
				for ( int y = 0 ; y < 128 ; y ++ ) {
					double d0 = (double) x / 128.0D;
					double d1 = (double) y / 128.0D;
					
					int xx = x_offset + (int) ( display_range * d0 );
					int yy = y_offset + (int) ( display_range * d1 );
					
					if ( xx >= 0 && yy >= 0 && xx < minimap.getColors ( ).capacity && yy < minimap.getColors ( ).capacity ) {
						int rgb = minimap.getColors ( ).get ( xx , yy );
						if ( rgb == 0 ) {
							canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
						} else {
							canvas.setPixel ( x , y , MapPalette.matchColor ( new Color ( rgb ) ) );
						}
					} else {
						canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
					}
				}
			}
		}
		
		// here we're going to add cursors, but first we have to clear the cursors of
		// the last render.
		clearCursors ( canvas );
		drawPlayers ( canvas , display_area , player , edit_mode );
		drawBorders ( canvas , map , display_area , edit_mode );
		
		if ( edit_mode ) {
			drawLootChestsTravelPath ( canvas , map , display_area );
		}
		
		// here we're implementing our unlimited tracking
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_11_R1 ) ) {
			unlimitedTrackingCheck ( canvas );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void drawLootChestsTravelPath ( MapCanvas canvas , BattleMap map , Area display_area ) {
		BRMapsYamlManager configuration = map.getConfig ( );
		
		// drawing loot chests
		for ( ConfigurableLocation chest : configuration.getChests ( ) ) {
			MapLocation location = project ( chest , display_area );
			if ( location == null || location.isOffOfLimits ( ) ) { continue; }
			
			canvas.getCursors ( ).addCursor ( location.x , location.y , location.direction , 
					MapCursor.Type.WHITE_CROSS.getValue ( ) );
		}
		
		// drawing travel paths
		for ( ConfigurableLocation spawn : configuration.getSpawns ( ) ) {
			byte rotation = (byte) ( ( 15 / 2 + (int) Math.round ( (int) spawn.getYaw ( ) / 22.5 ) + 1 ) & 15 );
			
			for ( int i = 0 ; i < 128 ; i ++ ) {
				MapLocation location = null;
				
				if ( i == 0 ) {
					location = project ( spawn , display_area );
				} else {
					Location path_point = spawn.clone ( )
							.add ( spawn.getDirection ( ).normalize ( ).multiply ( i * DISTANCE_BETWEEN_BORDER_CURSORS ) );
					if ( display_area.isInAreaNoY ( toVector3i ( path_point ) ) ) {
						location = project ( path_point , display_area );
					} else {
						continue;
					}
				}
				
				if ( location != null && !location.isOffOfLimits ( ) ) {
					canvas.getCursors ( ).addCursor ( location.x , location.y , rotation , 
							MapCursor.Type.RED_MARKER.getValue ( ) );
				}
			}
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void drawBorders ( MapCanvas canvas , BattleMap map , Area display_area , boolean edit_mode ) {
		if ( edit_mode ) {
			BRMapsYamlManager      configuration = map.getConfig ( );
			BorderShrinkingSuccession succession = configuration.getBorderSuccession ( );
			if ( succession != null ) {
				for ( BorderShrink shrink : succession.getShrinks ( ) ) {
					if ( shrink.getLocation ( ) == null || shrink.getRadius ( ) <= 0 ) { continue; }
					
					Location center = shrink.getLocation ( );
					double   radius = shrink.getRadius ( );
					
					for ( MapLocation location : drawCuboid ( center , radius , display_area ) ) {
						if ( location.isOffOfLimits ( ) ) { continue; }
						
						canvas.getCursors ( ).addCursor ( location.x , location.y , location.direction , 
								MapCursor.Type.BLUE_POINTER.getValue ( ) );
					}
				}
			}
		} else {
			BorderTimer border_timer = BorderTimer.getInstance ( );
			if ( border_timer != null && border_timer.getCurrentShrink ( ) != null ) {
				BorderShrink shrink = border_timer.getCurrentShrink ( );
				
				Location      center = border_timer.getBorderCenter ( );
				Location center_safe = shrink.getLocation ( );
				double        radius = border_timer.getBorderRadius ( );
				double   radius_safe = shrink.getRadius ( );
				
				// this is a little weird way to do this, but works and is comfortable.
				for ( int i = 0 ; i < 2 ; i ++ ) {
					// 0 = current border | 1 = safe cuboid.
					if ( i == 1 && border_timer.isBorderStopped ( ) ) {
						// the border is stopped, then the safe zone is redundant at this point.
						break;
					}
					
					Location     the_center = i == 0 ? center : center_safe;
					double       the_radius = i == 0 ? radius : radius_safe;
					MapCursor.Type the_type = i == 0 ? MapCursor.Type.RED_POINTER : MapCursor.Type.BLUE_POINTER;
					
					for ( MapLocation location : drawCuboid ( the_center , the_radius , display_area ) ) {
						if ( location.isOffOfLimits ( ) ) { continue; }
						
						canvas.getCursors ( ).addCursor ( location.x , location.y , location.direction , the_type.getValue ( ) );
					}
				}
			}
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void drawPlayers ( MapCanvas canvas , Area display_area , Player player , boolean edit_mode ) {
		// here we're drawing the watcher player
		MapLocation watcher = project ( player.getLocation ( ) , display_area , true );
		canvas.getCursors ( ).addCursor ( watcher.x , watcher.y , watcher.direction );
		
		// here we're drawing the teammates of the watcher player.
		BRPlayer br_player = BRPlayer.getBRPlayer ( player );
		if ( !edit_mode && br_player.hasTeam ( )  ) {
			for ( Member teammate : br_player.getTeam ( ).getOnlineMembers ( )  ) {
				if ( teammate.getUUID ( ).equals ( player.getUniqueId ( ) ) ) {
					// self filter
					continue;
				}
				
				Player team_mate = teammate.getPlayer ( );
				if ( player.canSee ( team_mate ) ) {
					MapLocation location = project ( team_mate.getLocation ( ) , display_area , true );
					
					// for any reason this method is deprecated, and there is no more ways to do this.
					canvas.getCursors ( ).addCursor ( location.x , location.y , location.direction , 
							MapCursor.Type.GREEN_POINTER.getValue ( ) );
				}
			}
		}
	}
	
	protected Set < MapLocation > drawCuboid ( Location center , double radius , Area display_area ) {
		Set < MapLocation > result = new HashSet < > ( );
		
		double half_radius = radius / 2;
		
		MapLocation corner_a = project ( center.clone ( ).add ( -half_radius , 0.0D , -half_radius ) , display_area , true ); // top-left corner
		MapLocation corner_b = project ( center.clone ( ).add ( half_radius , 0.0D , -half_radius ) , display_area , true );  // top-right corner
		MapLocation corner_c = project ( center.clone ( ).add ( -half_radius , 0.0D , half_radius ) , display_area , true );  // bottom-left corner
		MapLocation corner_d = project ( center.clone ( ).add ( half_radius , 0.0D , half_radius ) , display_area , true );   // bottom-right corner
		
		result.addAll ( connect ( corner_a , corner_b , display_area ) );
		result.addAll ( connect ( corner_c , corner_d , display_area ) );
		result.addAll ( connect ( corner_a , corner_c , display_area ) );
		result.addAll ( connect ( corner_b , corner_d , display_area ) );
		
		return result;
	}
	
	private Set < MapLocation > connect ( MapLocation corner_a , MapLocation corner_b , Area display_area ) {
		Set < MapLocation > result = new HashSet < > ( );
		
		int x_min = Math.min ( corner_a.x , corner_b.x );
		int x_max = Math.max ( corner_a.x , corner_b.x );
		int y_min = Math.min ( corner_a.y , corner_b.y );
		int y_max = Math.max ( corner_a.y , corner_b.y );
		
		double  distance = new Vector2i ( x_max , y_max ).distance ( new Vector2i ( x_min , y_min ) );
		Vector direction = new Vector ( x_max - x_min , 0.0D , y_max - y_min ).normalize ( );
		
		if ( direction.getX ( ) == 0.0D && direction.getZ ( ) == 0.0D ) {
			return result;
		}
		
		int count = (int) distance / DISTANCE_BETWEEN_BORDER_CURSORS;
		for ( int i = 0 ; i <= count ; i ++ ) {
			Vector point = new Vector ( x_min , 0.0D , y_min )
					.add ( direction.clone ( ).multiply ( i * DISTANCE_BETWEEN_BORDER_CURSORS ) );
			byte rotation = (byte) (int) Math
					.round ( (int) DirectionUtil.normalize ( DirectionUtil.getEulerAngles ( direction ) [ 0 ] ) / 22.5 );
			
			if ( (int) point.getX ( ) == 0 && (int) point.getZ ( ) == 0 ) {
				// something very weird happens if we doesn't check this.
				continue;
			}
			
			result.add ( new MapLocation ( (int) point.getX ( ) , (int) point.getZ ( ) , rotation ) );
		}
		return result;
	}
	
	/**
	 * Projects the given {@link Location} to a {@link MapLocation} between a
	 * specified {@link Area}.
	 * <p>
	 * @param location the bukkit location.
	 * @param area     the display area.
	 * @param clip_off_limits whether to clip locations off of limits.
	 * @return the corresponding map location, or null if off of limits and <strong>clip_off_limits</strong> is false.
	 */
	protected MapLocation project ( Location location , Area area , boolean clip_off_limits ) {
		Vector3i   vector = toVector3i ( location );
		Vector3i relative = vector.sub ( area.getMin ( ) );
		
		double d0 = (double) relative.getX ( ) / (double) area.getXSize ( );
		double d1 = (double) relative.getZ ( ) / (double) area.getZSize ( );
		
		int x = -128 + (int) ( 256 * d0 );
		int y = -128 + (int) ( 256 * d1 );
		
		// cliping
		boolean off_limits = false;
		if ( d0 <= 0.0D || d0 >= 1.0D ) {
			x          = d0 <= 0.0D ? -128 : 127;
			off_limits = true;
		}
		
		if ( d1 <= 0.0D || d1 >= 1.0D ) {
			y          = d1 <= 0.0D ? -128 : 127;
			off_limits = true;
		}
		
		if ( off_limits ? clip_off_limits : true ) {
			return new MapLocation ( x , y , (byte) ( 1 + ( DirectionUtil.normalize ( location.getYaw ( ) ) / 360.0F ) * 15 ) );
		} else {
			return null;
		}
	}
	
	/**
	 * Projects the given {@link Location} to a {@link MapLocation} between a
	 * specified {@link Area}.
	 * <p>
	 * @param location the bukkit location.
	 * @param area     the display area.
	 * @return the corresponding map location.
	 */
	protected MapLocation project ( Location location , Area area ) {
		return project ( location , area , false );
	}
	
	/**
	 * Gets the equivalent {@link Vector3i} for the provided {@link Location}.
	 * <p>
	 * @param location the location to convert.
	 * @return the equivalent {@link Vector3i}.
	 */
	protected Vector3i toVector3i ( Location location ) {
		return new Vector3i ( location.getX ( ) , location.getY ( ) , location.getZ ( ) );
	}
	
	/**
	 * Remove all cursors from specified {@link MapCanvas}.
	 * <p>
	 * @param canvas the canvas to remove from.
	 */
	protected void clearCursors ( MapCanvas canvas ) {
		MapCursorCollection cursors = canvas.getCursors ( );
        while ( cursors.size ( ) > 0 ) {
            cursors.removeCursor ( cursors.getCursor ( 0 ) );
        }
	}
	
	/**
	 * Checks the unlimited tracking. <strong>Note that this feature is available
	 * since 1.11.</strong>
	 * <p>
	 * @param view the map view to the enable unlimited tracking.
	 * @throws IllegalStateException if the version of the running server is
	 *                               <strong><code>< 1.11</code></strong>.
	 */
	protected void unlimitedTrackingCheck ( MapCanvas canvas ) {
		try {
			// this will throw an IllegalArgumentException on unsupported server versions.
			MapCursor.Type off_limits_type = MapCursor.Type.valueOf ( "SMALL_WHITE_CIRCLE" );
			
			MapCursorCollection cursors = canvas.getCursors ( );
			for ( int i = 0 ; i < cursors.size ( ) ; i ++ ) {
				MapCursor cursor = cursors.getCursor ( i );
				if ( cursor.getType ( ) != MapCursor.Type.GREEN_POINTER && cursor.getType ( ) != MapCursor.Type.WHITE_POINTER
						&& cursor.getType ( ) != off_limits_type ) {
					// we're ignoring cursors that doesn't represent a player.
					continue;
				}
				
				byte             x = cursor.getX ( );
				byte             y = cursor.getY ( );
				boolean off_limits = false;
				
				if ( x <= -128 || x >= 127 ) {
					x          = (byte) ( x <= -128 ? -128 : 127 );
					off_limits = true;
				}
				
				if ( y <= -128 || y >= 127 ) {
					y          = (byte) ( y <= -128 ? -128 : 127 );
					off_limits = true;
				}
				
				if ( off_limits ) {
					cursor.setX ( x );
					cursor.setY ( y );
					cursor.setType ( MapCursor.Type.valueOf ( "SMALL_WHITE_CIRCLE" ) );
				}
			}
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalStateException ( "unsupported server version!" );
		}
	}
}