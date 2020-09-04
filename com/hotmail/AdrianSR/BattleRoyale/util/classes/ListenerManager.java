package com.hotmail.AdrianSR.BattleRoyale.util.classes;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.hotmail.adriansr.core.plugin.Plugin;

public class ListenerManager implements Listener {
	
	private final Plugin plugin;
	
	public ListenerManager(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, this.plugin = plugin);
	}

	public void registerEvent(Class<? extends Event> event_class, EventPriority priority, boolean ignoreCancelled, EventTask<Event> task) {
		Bukkit.getPluginManager().registerEvent(event_class, this, priority, new EventExecutor() {
			@Override
			public void execute(Listener listener, Event event) throws EventException {
				task.onEvent(event);
			}
		}, plugin, ignoreCancelled);
	}
	
	public void registerEvent(Class<? extends Event> event_class, EventPriority priority, EventTask<Event> task) {
		registerEvent(event_class, priority, false, task);
	}
	
	public void registerEvent(Class<? extends Event> event_class, EventTask<Event> task) {
		registerEvent(event_class, EventPriority.NORMAL, false, task);
	}
	
	public interface EventTask<T extends Event> {
		
		public void onEvent(T t);
	}
}
