package br.com.javamoon.application.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawListRepository;

@Component
public class InsertDataForTesting {
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	//@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Environment env = event.getApplicationContext().getEnvironment();
		
		if (env.acceptsProfiles(Profiles.of("dev"))) {
			System.out.println("The red fox runs accross the river.");
			
			//fixQuarterYear();
		}
	}
	
	private void fixQuarterYear() {
		List<DrawList> lists = drawListRepository.findAll();
		lists.stream()
			.forEach(l -> {
				String newPattern = InsertDataForTesting.toNewYearQuarterPattern(l.getYearQuarter());
				l.setYearQuarter(newPattern);
				drawListRepository.save(l);
			});
	}
	
	private static String toNewYearQuarterPattern(String oldPattern) {
		return String.format("%s'%s", oldPattern.split("/")[1], oldPattern.split("/")[0]);
	}
}
