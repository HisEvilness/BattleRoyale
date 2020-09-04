package com.hotmail.AdrianSR.BattleRoyale.game.item;

import java.util.Arrays;
import java.util.List;

public class SimpleItemConfiguration extends ItemConfiguration {

	SimpleItemConfiguration(String default_name, String... default_lore) {
		/* default ConfigItems */
		this.add(new ConfigItem<String>(BattleItems.NAME_KEY, default_name));
		this.add(new ConfigItem<List<String>>(BattleItems.LORE_KEY, Arrays.asList(default_lore)));
	}
}
