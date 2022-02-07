package br.com.javamoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("br.com.javamoon.config.properties")
public class MdForo11WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdForo11WebApplication.class, args);
	}

}
