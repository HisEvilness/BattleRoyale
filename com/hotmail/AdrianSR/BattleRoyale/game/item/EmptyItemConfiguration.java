package com.hotmail.AdrianSR.BattleRoyale.game.item;

public class EmptyItemConfiguration extends ItemConfiguration {
	
	@Override
	public ItemConfiguration add(ConfigItem<?> config) {
		return this;
	}
	
	@Override
	public ItemConfiguration remove(ConfigItem<?> config) {
		return this;
	}
	
	@Override
	public ConfigItem<?> getFromKey(String path) {
		return null;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
}