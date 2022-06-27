package br.com.javamoon.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationConfigProperties {

	private final Integer maxLimit;
	
	public PaginationConfigProperties(
			@Value("${md-foro11.drawList.defaultProperties.soldier.maxLimit}") int maxLimit) {
		this.maxLimit = maxLimit;
	}
	
	public Integer getMaxLimit() {
		return maxLimit;
	}
}
