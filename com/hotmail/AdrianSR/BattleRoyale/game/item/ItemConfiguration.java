package com.hotmail.AdrianSR.BattleRoyale.game.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

public class ItemConfiguration {
	
	protected final List<ConfigItem<?>> handler = new ArrayList<>();

	public ItemConfiguration add(ConfigItem<?> config) {
		handler.add(config);
		return this;
	}
	
	public ItemConfiguration remove(ConfigItem<?> config) {
		handler.remove(config);
		return this;
	}
	
	public ConfigItem<?> getFromKey(String key) {
		/* find matches */
		for (ConfigItem<?> config : handler) {
			if (config != null && Objects.equal(key, config.getKey())) {
				return config;
			}
		}
		return null;
	}
	
	public boolean has(String key) {
		return getFromKey(key) != null;
	}
	
	public List<ConfigItem<?>> getConfigItems() {
		return Collections.unmodifiableList(handler);
	}
	
	public boolean isEmpty() {
		return handler.isEmpty();
	}
}