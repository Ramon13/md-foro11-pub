package br.com.javamoon.config.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "md-foro11.draw")
@ConstructorBinding
public class DrawConfigProperties {

	public static final String PROPERTY_ARMY_ALIAS = "armyAlias";
	public static final String PROPERTY_COUNCIL_ALIAS = "councilAlias";
	
	private final Map<String, String> defaultProperties;

	public DrawConfigProperties(Map<String, String> defaultProperties) {
		this.defaultProperties = defaultProperties;
	}
	
	public String getDefaultProperty(String propertyName) {
		return defaultProperties.get(propertyName);
	}
}
