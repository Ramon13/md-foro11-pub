package br.com.javamoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ConfigurationPropertiesScan("br.com.javamoon.config.properties")
public class MdForo11WebApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(MdForo11WebApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		builder.profiles("prod_intranet");
		return builder.sources(MdForo11WebApplication.class);
	}
}
