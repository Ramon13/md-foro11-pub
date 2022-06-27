package br.com.javamoon.unit.config;

import static br.com.javamoon.config.properties.DrawConfigProperties.PROPERTY_ARMY_ALIAS;
import static br.com.javamoon.config.properties.DrawConfigProperties.PROPERTY_COUNCIL_ALIAS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import br.com.javamoon.config.properties.DrawConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawConfigUnitTest {

	@Autowired
	private DrawConfigProperties victim;
	
	@Value("${md-foro11.draw.defaultProperties.armyAlias}")
	private String armyAlias;
	
	@Value("${md-foro11.draw.defaultProperties.councilAlias}")
	private String councilAlias;
	
	@Test
	void testBindingConfigProperties() {
		assertNotNull(armyAlias);
		assertNotNull(councilAlias);
		
		assertEquals(armyAlias, victim.getDefaultProperty(PROPERTY_ARMY_ALIAS));
		assertEquals(councilAlias, victim.getDefaultProperty(PROPERTY_COUNCIL_ALIAS));
	}
}
