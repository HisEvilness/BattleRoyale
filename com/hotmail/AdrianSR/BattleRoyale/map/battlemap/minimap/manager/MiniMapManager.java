package com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.manager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;

import com.hotmail.AdrianSR.BattleRoyale.game.item.BattleItems;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.MiniMapRenderer;
import com.hotmail.AdrianSR.BattleRoyale.map.battlemap.minimap.MiniMapZoomPreferences;
import com.hotmail.adriansr.core.handler.PluginHandler;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 01 September, 2020 / 06:26 PM
 */
public final class MiniMapManager extends PluginHandler {

	public static final MiniMapRenderer RENDERER = new MiniMapRenderer ( );
	
	public MiniMapManager ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void addBattleMapRender ( MapInitializeEvent event ) {
		for ( MapRenderer old : event.getMap ( ).getRenderers ( ) ) {
			event.getMap ( ).removeRenderer ( old );
		}
		
		event.getMap ( ).addRenderer ( RENDERER );
	}

	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onToggleMiniMapZoom ( PlayerInteractEvent event ) {
		if ( !BattleItems.MINI_MAP.isThis ( event.getItem ( ) ) ) {
			return;
		}
		
		MiniMapZoomPreferences.getPreferences ( event.getPlayer ( ) ).toggleZoom ( );
		event.setCancelled ( true );
		/* toggle zoom for player */
		// FIXME: 
//		getRenderer().toggleZoom(event.getPlayer());
	}

	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}