package br.com.javamoon.integration.service;

import static br.com.javamoon.util.Constants.CEJ_COUNCIl_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ID;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIL_ID;
import static br.com.javamoon.util.Constants.DEFAULT_CPJ_COUNCIL_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_ID;
import static br.com.javamoon.util.Constants.DEFAULT_PROCESS_NUMBER;
import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.Constants.DEFAULT_USER_PASSWORD;
import static br.com.javamoon.util.Constants.DEFAULT_USER_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.PROCESS_NUMBER_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.repository.ArmyRepository;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.ArmyNotFoundException;
import br.com.javamoon.exception.AuditorshipNotFoundException;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.JusticeCouncilNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
	void testSaveCPJDrawSuccessfully() {
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		DrawDTO drawDTO = new DrawDTO();
		
		drawDTO.setArmy(persistedDraw.getArmy());
		drawDTO.setJusticeCouncil(persistedDraw.getJusticeCouncil());
		drawDTO.setSelectedDrawList(persistedDraw.getDrawList().getId());
		drawDTO.setSoldiers(persistedDraw.getSoldiers().stream().map(s -> EntityMapper.fromEntityToDTO(s)).collect(Collectors.toList()));
		drawDTO.setSelectedYearQuarter(persistedDraw.getDrawList().getYearQuarter());
		drawDTO.setSelectedRanks(persistedDraw.getSoldiers().stream().map(s -> s.getMilitaryRank().getId()).collect(Collectors.toList()));
		
		victim.save(drawDTO, persistedDraw.getCjmUser());
	}
	
	@Test
	void testSaveDrawWhenArmyIsMissing() {
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.getArmy().setId(DEFAULT_ARMY_ID + 1);
		
		assertThrows(ArmyNotFoundException.class, 
				() -> victim.save(drawDTO, persistedDraw.getCjmUser()));
	}
	
	@Test
	void testSaveDrawWhenJusticeCouncilIsMissing() {
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);	
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.getJusticeCouncil().setId(DEFAULT_COUNCIL_ID + 1);

		assertThrows(JusticeCouncilNotFoundException.class, () -> victim.save(drawDTO, persistedDraw.getCjmUser()));
	}
	
	@Test
	void testRandAllWhenProcessNumberAlreadyExists() {
		JusticeCouncil council = TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository);
		council.setAlias(CEJ_COUNCIl_ALIAS);
		justiceCouncilRepository.saveAndFlush(council);
		
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		persistedDraw.setJusticeCouncil(council);
		persistedDraw.setProcessNumber(DEFAULT_PROCESS_NUMBER);
		persistedDraw.getSoldiers().remove(0);						// resize list to be equals to new council size
		drawRepository.saveAndFlush(persistedDraw);
		
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, 
				() -> victim.save(drawDTO, persistedDraw.getCjmUser()));
		
		assertEquals(
			new ValidationError(DRAW_PROCESS_NUMBER, PROCESS_NUMBER_ALREADY_EXISTS),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testSaveDrawWhenProcessNumberAlreadyExists() {
		JusticeCouncil council = TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository);
		council.setAlias(CEJ_COUNCIl_ALIAS);
		justiceCouncilRepository.saveAndFlush(council);
		
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		persistedDraw.setJusticeCouncil(council);
		persistedDraw.setProcessNumber(DEFAULT_PROCESS_NUMBER);
		persistedDraw.getSoldiers().remove(0);						// resize list to be equals to new council size
		drawRepository.saveAndFlush(persistedDraw);
		
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, 
				() -> victim.save(drawDTO, persistedDraw.getCjmUser()));
		
		assertEquals(
			new ValidationError(DRAW_PROCESS_NUMBER, PROCESS_NUMBER_ALREADY_EXISTS),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testSaveDrawWhenTheListIsNotFound() {
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		drawDTO.setSelectedDrawList(DEFAULT_DRAW_LIST_ID + 1);
		
		assertThrows(DrawListNotFoundException.class, 
				() -> victim.save(drawDTO, persistedDraw.getCjmUser()));
	}
	
	@Test
	void testSaveDrawWhenSelectedRankDoesNotBelongsToArmy() {
		Draw persistedDraw = getPersistedListOfDraw(1).get(0);
		DrawDTO drawDTO = EntityMapper.fromEntityToDTO(persistedDraw);
		
		MilitaryRank rank = new MilitaryRank();
		rank.setId(10);
		
		drawDTO.getSelectedRanks().set(0, 10);
		drawDTO.getSoldiers().get(0).setMilitaryRank(rank);
		assertThrows(IllegalStateException.class, 
				() -> victim.save(drawDTO, persistedDraw.getCjmUser()));
	}
	
	@Test
	void testListByAuditorshipSuccessfully() {
		final int listSize = 5;
		List<Draw> listOfDraw = getPersistedListOfDraw(listSize);
		Auditorship auditorship = listOfDraw.get(0).getCjmUser().getAuditorship();
		
		List<Draw> fetchedList = victim.listByAuditorship(auditorship.getId());
		assertEquals(listSize, fetchedList.size());
		assertEquals(listOfDraw.get(0), fetchedList.get(0));
		assertEquals(listOfDraw.get(listSize - 1), fetchedList.get(listSize - 1));
		
		Auditorship newAuditorship = new Auditorship();
		newAuditorship.setCjm(auditorship.getCjm());
		newAuditorship.setName(Constants.DEFAULT_AUDITORSHIP_NAME + "___");
		auditorshipRepository.saveAndFlush(newAuditorship);
		
		fetchedList = victim.listByAuditorship(newAuditorship.getId());
		assertEquals(0, fetchedList.size());
		
		assertThrows(AuditorshipNotFoundException.class, () -> victim.listByAuditorship(-1));
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
	
	/**
	 * 
	 * @param count	Number of draw to be persisted
	 * @return
	 */
	private List<Draw> getPersistedListOfDraw(int count) {
		DrawList drawList = getPersistedDrawList(DEFAULT_CPJ_COUNCIL_SIZE);
		
		Auditorship auditorship = new Auditorship();
		auditorship.setCjm(drawList.getCreationUser().getCjm());
		auditorship.setName(Constants.DEFAULT_AUDITORSHIP_NAME);
		auditorshipRepository.saveAndFlush(auditorship);
		
		CJMUser cjmUser = new CJMUser();
		cjmUser.setUsername(DEFAULT_USER_USERNAME);
		cjmUser.setEmail(DEFAULT_USER_EMAIL);
		cjmUser.setPassword(DEFAULT_USER_PASSWORD);
		cjmUser.setAuditorship(auditorship);
		cjmUserRepository.saveAndFlush(cjmUser);
		
		
		Draw draw;
		List<Draw> listOfDraw = new ArrayList<>();
		while (count-- > 0) {
			draw = new Draw();
    		draw.setArmy(drawList.getArmy());
    		draw.setCjmUser(cjmUser);
    		draw.setDrawList(drawList);
    		draw.getSoldiers().addAll(drawList.getSoldiers());
    		draw.setJusticeCouncil(TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository));
    		listOfDraw.add(draw);
		}
		
		return drawRepository.saveAllAndFlush(listOfDraw);
	}
}
