package com.hotmail.AdrianSR.BattleRoyale.game.scoreboard.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import com.hotmail.adriansr.core.scoreboard.SimpleScoreboard;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.yaml.YamlUtil;

public class ScoreboardConfiguration {

	public static final String     NAME_KEY = "name";
	public static final String ELEMENTS_KEY = "elements";

	protected       String       name = null;
	protected final String[] elements = new String[SimpleScoreboard.MAX_ELEMENTS];
	protected final String[] defaults = new String[SimpleScoreboard.MAX_ELEMENTS];
	
	public ScoreboardConfiguration(String name, String[] elements, String[] default_elements) {
		this.name = name;
		for (int i = 0; ( i < Math.min(this.elements.length, elements.length) ); i++) {
			this.elements[i] = elements[i];
		}
		
		for (int i = 0; ( i < Math.min(this.defaults.length, default_elements.length) ); i++) {
			this.defaults[i] = default_elements[i];
		}
	}
	
	public ScoreboardConfiguration(String name, String... elements) {
		this(name, elements, elements);
	}
	
	public String getName() {
		return name;
	}
	
	public ScoreboardConfiguration setName ( String name ) {
		this.name = name;
		return this;
	}
	
	public String[] getElements() {
		return elements;
	}
	
	public String[] getDefaultElements() {
		return Arrays.copyOfRange(defaults, 0, defaults.length);
	}
	
	public ScoreboardConfiguration insertDefaultsIntoElements ( ) {
		for ( int i = 0 ; i < Math.min ( elements.length , defaults.length ) ; i ++ ) {
			elements [ i ] = defaults [ i ];
		}
		return this;
	}
	
	public ScoreboardConfiguration save(ConfigurationSection section) {
		section.set(NAME_KEY, StringUtil.translateAlternateColorCodes(StringUtil.defaultString(name, "")));
		section.set(ELEMENTS_KEY, StringUtil.translateAlternateColorCodes(excludeNulls(elements)));
		return this;
	}

	/**
	 * <li> 0 = no defaults was saved.
	 * <li> 1 = default name saved.
	 * <li> 2 = default elements saved.
	 * <li> 3 = default name and elements saved.
	 * <p>
	 * @param section
	 * @return
	 */
	public int saveDefaults ( ConfigurationSection section ) {
		boolean     name = false;
		boolean elements = false;
		
		// we are adding the header.
		header ( section );
		
		// we are saving the name if not set.
		name = YamlUtil.setNotSet ( section , NAME_KEY , 
				StringUtil.translateAlternateColorCodes ( StringUtil.defaultString ( this.name , "" ) ) );
		
		// we are saving the default elements if not set.
		if ( !section.isSet ( ELEMENTS_KEY ) ) {
			section.set ( ELEMENTS_KEY , StringUtil.translateAlternateColorCodes ( excludeNulls ( defaults ) ) );
			elements = true;
		}
		
		if ( name == elements ) {
			return name == false ? 0 : 3;
		} else {
			return name ? 1 : ( elements ? 2 : 0 );
		}
	}
	
	protected void header(ConfigurationSection section) {
		ConfigurationOptions options = section.getRoot().options();
		if (!(options instanceof YamlConfigurationOptions)) {
			return;
		}
		
		((YamlConfigurationOptions) options).header(
				"---------------------------- A header here ---------------------------- #");
		((YamlConfigurationOptions) options).copyHeader(true);
	}

	public ScoreboardConfiguration load ( ConfigurationSection section ) {
		this.name = section.getString ( NAME_KEY,  " " );

		/* clear before loading */
		Arrays.fill ( elements , null );

		/* load elements */
		List < String > current_list = section.getStringList ( ELEMENTS_KEY );
		for ( int i = 0 ; i < Math.min ( elements.length , current_list.size ( ) ) ; i ++ ) {
			elements [ i ] = current_list.get ( i );
		}
		return this;
	}
	
	protected String[] excludeNulls(String[] array) {
		List<String> collection = Arrays.asList(array)
				.stream()
				.filter(element -> element != null)
				.collect(Collectors.toList());
		return collection.toArray(new String[collection.size()]);
	}
}