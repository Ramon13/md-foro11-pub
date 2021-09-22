package br.com.javamoon.application.test;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class InsertDataForTesting {
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Environment env = event.getApplicationContext().getEnvironment();
		
		if (env.acceptsProfiles(Profiles.of("dev"))) {}
	}	
}
