package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Wednesday 02 September, 2020 / 09:56 AM
 */
public class MiniMapZoomPreferences {
	
	private static final Map < UUID , MiniMapZoomPreferences > ZOOM_PREFERENCES = new HashMap < > ( );
	
	public static MiniMapZoomPreferences getPreferences ( UUID id ) {
		if ( ZOOM_PREFERENCES.containsKey ( id ) ) {
			return ZOOM_PREFERENCES.get ( id );
		} else {
			MiniMapZoomPreferences preferences = new MiniMapZoomPreferences ( id );
			ZOOM_PREFERENCES.put ( id , preferences );
			return preferences;
		}
	}
	
	public static MiniMapZoomPreferences getPreferences ( Player player ) {
		return getPreferences ( player.getUniqueId ( ) );
	}
	
	public static MiniMapZoomPreferences getPreferences ( BRPlayer player ) {
		return getPreferences ( player.getUUID ( ) );
	}

	protected final UUID          id;
	protected       MiniMapZoom zoom;
	
	protected MiniMapZoomPreferences ( UUID id ) {
		this.id   = id;
		this.zoom = MiniMapZoom.NORMAL;
	}
	
	public MiniMapZoomPreferences ( Player player ) {
		this ( player.getUniqueId ( ) );
	}
	
	public MiniMapZoomPreferences ( BRPlayer player ) {
		this ( player.getUUID ( ) );
	}
	
	public BRPlayer getPlayer ( ) {
		return BRPlayer.getBRPlayer ( id );
	}
	
	public MiniMapZoom getZoom ( ) {
		return zoom;
	}
	
	public void setZoom ( MiniMapZoom zoom ) {
		Validate.notNull ( zoom , "zoom cannot be null!");
		this.zoom = zoom;
	}
	
	public void toggleZoom ( ) {
		int index = ArrayUtils.indexOf ( MiniMapZoom.values ( ) , zoom );
		if ( ( index + 1 ) < MiniMapZoom.values ( ).length ) {
			zoom = MiniMapZoom.values ( ) [ index + 1 ];
		} else {
			zoom = MiniMapZoom.values ( ) [ 0 ];
		}
	}
}