package br.com.javamoon.unit.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import br.com.javamoon.config.properties.DrawConfigProperties;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawDTOUnitTest {

	private DrawDTO victim;
	
	@Mock
	private ArmyService armyService;
	
	@Mock
	private JusticeCouncilService councilService;
	
	@Autowired
	private DrawConfigProperties drawConfigProperties;
	
	@Value("${md-foro11.draw.defaultProperties.armyAlias}")
	private String armyAlias;
	
	@Value("${md-foro11.draw.defaultProperties.councilAlias}")
	private String councilAlias;
	
	@BeforeEach
	void setupEach() {
		Mockito.when(armyService.getByAlias(armyAlias)).thenReturn(TestDataCreator.newArmy());
		Mockito.when(councilService.getByAlias(councilAlias)).thenReturn(TestDataCreator.getJusticeCouncil());
		victim = new DrawDTO(armyService, councilService, drawConfigProperties);
	}
	
	@Test
	void testConstructorInitialization() {
		assertEquals(armyAlias, victim.getArmy().getAlias());
		assertEquals(councilAlias, victim.getJusticeCouncil().getAlias());
		assertEquals(DateUtils.toQuarterFormat(LocalDate.now()), victim.getSelectedYearQuarter());
	}
	
	@Test
	void testPrettyPrintingQuarterYear() {
		String yearQuarter = victim.getSelectedYearQuarter();
		assertEquals(
			String.format("%s/%s", yearQuarter.split("'")[1], yearQuarter.split("'")[0]),
			victim.prettyPrintQuarterYear(yearQuarter)
		);
	}

	@Test
	void testIsCPJ() {
		assertTrue(victim.isCPJ());
	}
	
	@Test
	void testSetJusticeCouncil() {
		assertNotNull(victim.getJusticeCouncil());
		assertEquals(5, victim.getSelectedRanks().size());
		
		JusticeCouncil newJusticeCouncil = TestDataCreator.getJusticeCouncil();
		newJusticeCouncil.setCouncilSize(4);
		
		victim.setJusticeCouncil(newJusticeCouncil);
		assertEquals(4, victim.getSelectedRanks().size());
	}
	
	@Test
	void testIsAlreadyBeenDrawn() {
		assertTrue(victim.isNeverDrawn());
		
		victim.setSelectedDrawList(1);
		victim.getSoldiers().addAll(TestDataCreator.newSoldierList(null, null, null, null, 3));
		
		assertFalse(victim.isNeverDrawn());
	}
}
