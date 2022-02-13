package br.com.javamoon.integration.service;

import static br.com.javamoon.util.Constants.CEJ_COUNCIl_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ID;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIL_ID;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIL_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_ID;
import static br.com.javamoon.util.Constants.DEFAULT_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.PROCESS_NUMBER_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.ArmyNotFoundException;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.JusticeCouncilNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawServiceIntegrationTest {
	
	@Autowired
	private DrawService victim;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@Autowired
	private AuditorshipRepository auditorshipRepository;

	@Autowired
	private MilitaryOrganizationRepository militaryOrganizationRepository;
	
	@Autowired
	private MilitaryRankRepository militaryRankRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private JusticeCouncilRepository justiceCouncilRepository;
	
	@Autowired
	private DrawRepository drawRepository;
	
	@Test
	void testSaveDrawWhenArmyIsMissing() {
		Draw persistedDraw = getPersistedDraw();
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.getArmy().setId(DEFAULT_ARMY_ID + 1);
		
		assertThrows(ArmyNotFoundException.class, 
				() -> victim.save(drawDTO, persistedDraw.getDrawList().getCreationUser().getCjm()));
	}
	
	@Test
	void testSaveDrawWhenJusticeCouncilIsMissing() {
		Draw persistedDraw = getPersistedDraw();	
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.getJusticeCouncil().setId(DEFAULT_COUNCIL_ID + 1);

		assertThrows(JusticeCouncilNotFoundException.class, () -> victim.save(drawDTO, null));
	}
	
	@Test
	void testSaveDrawWhenProcessNumberAlreadyExists() {
		JusticeCouncil council = TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository);
		council.setAlias(CEJ_COUNCIl_ALIAS);
		justiceCouncilRepository.saveAndFlush(council);
		
		Draw persistedDraw = getPersistedDraw();
		persistedDraw.setJusticeCouncil(council);
		persistedDraw.setProcessNumber(DEFAULT_PROCESS_NUMBER);
		persistedDraw.getSoldiers().remove(0);						// resize list to be equals to new council size
		drawRepository.saveAndFlush(persistedDraw);
		
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, 
				() -> victim.save(drawDTO, persistedDraw.getDrawList().getCreationUser().getCjm()));
		
		assertEquals(
			new ValidationError(DRAW_PROCESS_NUMBER, PROCESS_NUMBER_ALREADY_EXISTS),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testSaveDrawWhenTheListIsNotFound() {
		Draw persistedDraw = getPersistedDraw();
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.setSelectedDrawList(DEFAULT_DRAW_LIST_ID + 1);
		
		assertThrows(DrawListNotFoundException.class, 
				() -> victim.save(drawDTO, persistedDraw.getDrawList().getCreationUser().getCjm()));
	}
	
	@Test
	void testSaveDrawWhenSelectedRankDoesNotBelongsToArmy() {
		Draw persistedDraw = getPersistedDraw();
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		
		MilitaryRank rank = new MilitaryRank();
		rank.setId(10);
		
		drawDTO.getSelectedRanks().set(0, 10);
		drawDTO.getSoldiers().get(0).setMilitaryRank(rank);
		assertThrows(IllegalStateException.class, 
				() -> victim.save(drawDTO, persistedDraw.getDrawList().getCreationUser().getCjm()));
	}
	
	private DrawList getPersistedDrawList(int numOfSoldiers) {
		return TestDataCreator.getPersistedDrawLists(
			armyRepository,
			cjmRepository,
			militaryOrganizationRepository,
			militaryRankRepository,
			groupUserRepository,
			soldierRepository,
			drawListRepository,
			1,
			numOfSoldiers).get(0);
	}
	
	private Draw getPersistedDraw() {
		DrawList drawList = getPersistedDrawList(DEFAULT_COUNCIL_SIZE);
		
		Draw draw = new Draw();
		draw.setArmy(drawList.getArmy());
		draw.setCjmUser(TestDataCreator.getPersistedCJMUser(cjmUserRepository, auditorshipRepository, cjmRepository));
		draw.setDrawList(drawList);
		draw.getSoldiers().addAll(drawList.getSoldiers());
		draw.setJusticeCouncil(TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository));
		
		return drawRepository.saveAndFlush(draw);
	}
}
