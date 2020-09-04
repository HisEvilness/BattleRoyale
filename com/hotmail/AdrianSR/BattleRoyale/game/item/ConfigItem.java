package com.hotmail.AdrianSR.BattleRoyale.game.item;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.adriansr.core.util.StringUtil;

public class ConfigItem<T> {

	private final String      key;
	private final T default_value;

	ConfigItem(String key, T default_value) {
		this.key           = key;
		this.default_value = default_value;
	}

	public String getKey() {
		return key;
	}
	
	public T getDefaultValue() {
		return default_value;
	}
	
	@SuppressWarnings("unchecked")
	public void setDefaults(ConfigurationSection section) {
		Validate.notNull(section, "The configuration section cannot be null!");
		if (section != null) {
			/* check default value instance */
			if (getDefaultValue() instanceof String) {
				section.set(getKey(), StringUtil.untranslateAlternateColorCodes((String) getDefaultValue()));
			} else if (getDefaultValue() instanceof List) {
				final List<?> list = (List<?>) getDefaultValue();
				if (!list.isEmpty()) {
					if (list.get(0) instanceof String) {
						section.set(getKey(), StringUtil.untranslateAlternateColorCodes((List<String>) getDefaultValue()));
					} else {
						section.set(getKey(), getDefaultValue());
					}
				}
			} else {
				section.set(getKey(), getDefaultValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T getValueInConfig(ConfigurationSection section) {
		try {
			return (T) section.get(getKey());
		} catch(Throwable t) {
			return null;
		}
	}

	public T getSafeValue(ConfigurationSection section) {
		if (getValueInConfig(section) != null) {
			return getValueInConfig(section);
		}
		return getDefaultValue();
	}
	
	public boolean isSet(ConfigurationSection section) {
		return section != null && section.isSet(getKey());
	}
}