package com.hotmail.AdrianSR.BattleRoyale.game.mode.complex;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.PluginAwareness;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.ImmutableList;
import com.hotmail.AdrianSR.BattleRoyale.exceptions.InvalidDescriptionException;

public final class ComplexBattleModeDescription {
	
    private static final Pattern     VALID_NAME = Pattern.compile("^[A-Za-z0-9 _.-]+$");
    private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            return new Yaml(new SafeConstructor() {
                {
                    yamlConstructors.put(null, new AbstractConstruct() {
                        @Override
                        public Object construct(final Node node) {
                            if (!node.getTag().startsWith("!@")) {
                                // Unknown tag - will fail
                                return SafeConstructor.undefinedConstructor.construct(node);
                            }
                            // Unknown awareness - provide a graceful substitution
                            return new PluginAwareness() {
                                @Override
                                public String toString() {
                                    return node.toString();
                                }
                            };
                        }
                    });
                    for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
                        yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
                            @Override
                            public PluginAwareness.Flags construct(final Node node) {
                                return flag;
                            }
                        });
                    }
                }
            });
        }
    };
    
    String     			 rawName = null;
    private String 			name = null;
    private String          main = null;
    private List<String> authors = null;
	
    public ComplexBattleModeDescription(final InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(stream)));
    }
    
    public String getName() {
    	return name;
    }
    
    public String getMain() {
    	return main;
    }
    
    public List<String> getAuthors() {
    	return Collections.unmodifiableList(authors);
    }
    
    public boolean isValid() {
    	if (StringUtils.isBlank(name) || !VALID_NAME.matcher(name).matches()) {
    		return false;
    	}
    	
    	if (StringUtils.isBlank(main)) {
    		return false;
    	}
    	return true;
    }
    
    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			name = rawName = map.get("name").toString();
			if (!VALID_NAME.matcher(name).matches()) {
				throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
			}

			name = name.replace(' ', '_');
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}

		try {
			main = map.get("main").toString();
//			if (main.startsWith("com.hotmail.AdrianSR")) {
//				throw new InvalidDescriptionException("main may not be within the com.hotmail.AdrianSR namespace");
//			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "main is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "main is of wrong type");
		}
		
		if (map.get("authors") != null) {
			ImmutableList.Builder<String> authorsBuilder = ImmutableList.<String>builder();
			if (map.get("author") != null) {
				authorsBuilder.add(map.get("author").toString());
			}
			try {
				for (Object o : (Iterable<?>) map.get("authors")) {
					authorsBuilder.add(o.toString());
				}
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			} catch (NullPointerException ex) {
				throw new InvalidDescriptionException(ex, "authors are improperly defined");
			}
			authors = authorsBuilder.build();
		} else if (map.get("author") != null) {
			authors = ImmutableList.of(map.get("author").toString());
		} else {
			authors = ImmutableList.<String>of();
		}
    }
    
	private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
		if (object instanceof Map) {
			return (Map<?, ?>) object;
		}
		throw new InvalidDescriptionException(object + " is not properly structured.");
	}
}
