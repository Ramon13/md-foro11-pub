package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryOrganization;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryRank;
import static br.com.javamoon.util.TestDataCreator.newDrawExclusionList;
import static br.com.javamoon.util.TestDataCreator.newGroupUserList;
import static br.com.javamoon.util.TestDataCreator.newSoldierList;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_MESSAGE;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.draw_exclusion.DrawExclusionRepository;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.ArmyRepository;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.DrawExclusionNotFoundException;
import br.com.javamoon.exception.DrawExclusionValidationException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawExclusionService;
import br.com.javamoon.service.ServiceConstants;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawExclusionServiceUnitTest {
	
	@Autowired
	private DrawExclusionService victim;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Autowired
	private MilitaryOrganizationRepository organizationRepository;
	
	@Autowired
	private MilitaryRankRepository rankRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private DrawExclusionRepository exclusionRepository;
	
	@Autowired
	private JusticeCouncilRepository justiceCouncilRepository;
	
	@Autowired
	private AuditorshipRepository auditorshipRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@Autowired
	private DrawRepository drawRepository;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Test
	void testListBySoldierSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 2);
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser groupUser = newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(groupUser);
		
		List<DrawExclusion> exclusions = newDrawExclusionList(soldiers.get(0), groupUser, 3);
		exclusions.get(0).setSoldier(soldiers.get(1));
		exclusionRepository.saveAllAndFlush(exclusions);
		
		List<DrawExclusionDTO> exclusionsDTO = victim.listBySoldier(soldiers.get(0));
		assertFalse(exclusionsDTO.isEmpty());
		assertEquals(2, exclusionsDTO.size());
		assertEquals(exclusions.get(2).getMessage(), exclusionsDTO.get(0).getMessage());
		assertEquals(exclusions.get(1).getMessage(), exclusionsDTO.get(1).getMessage());
	}
	
	@Test
	void testSaveExclusionSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		GroupUser groupUser = groupUserRepository.saveAndFlush(TestDataCreator.newGroupUserList(army, cjm, 1).get(0));
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 1);
		soldierRepository.saveAllAndFlush(soldiers);
		
		DrawExclusion exclusion = TestDataCreator.newDrawExclusionList(soldiers.get(0), groupUser, 1).get(0);
		exclusion.setSoldier(null);
		exclusion.setGroupUser(null);
		
		DrawExclusionDTO exclusionDTO = victim.save(EntityMapper.fromEntityToDTO(exclusion), groupUser, soldiers.get(0));
		assertNotNull(exclusionDTO.getId());
		assertEquals(exclusion.getMessage(), exclusionDTO.getMessage());
	}
	
	void testSaveExclusionWhenMessageIsMissing() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		GroupUser groupUser = groupUserRepository.saveAndFlush(TestDataCreator.newGroupUserList(army, cjm, 1).get(0));
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 1);
		soldierRepository.saveAllAndFlush(soldiers);
		
		DrawExclusion exclusion = TestDataCreator.newDrawExclusionList(soldiers.get(0), groupUser, 1).get(0);
		exclusion.setMessage(null);
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class,
				() -> victim.save(EntityMapper.fromEntityToDTO(exclusion), groupUser, soldiers.get(0)));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_MESSAGE, REQUIRED_FIELD),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionWhenSoldierIsFromDifferentArmy() {
		Army army1 = getPersistedArmy(armyRepository);
		Army army2 = TestDataCreator.newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "x");
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		armyRepository.saveAndFlush(army2);
		
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army1, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army1, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army1, cjm, organization, rank, 2);
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser groupUser = newGroupUserList(army2, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(groupUser);
		
		DrawExclusion exclusion = TestDataCreator.newDrawExclusionList(soldiers.get(0), groupUser, 1).get(0);
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.save(EntityMapper.fromEntityToDTO(exclusion), groupUser, soldiers.get(0)));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}

	@Test
	void testDeleteExclusionSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 2);
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser groupUser = newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(groupUser);
		
		List<DrawExclusion> exclusions = newDrawExclusionList(soldiers.get(0), groupUser, 3);
		exclusionRepository.saveAllAndFlush(exclusions);
		
		victim.delete(exclusions.get(0).getId(), groupUser);
		
		List<DrawExclusion> exclusionsDB = exclusionRepository.findAll();
		
		assertFalse(exclusionsDB.isEmpty());
		assertEquals(2, exclusionsDB.size());
		assertEquals(exclusions.get(1).getMessage(), exclusionsDB.get(0).getMessage());
		assertEquals(exclusions.get(2).getMessage(), exclusionsDB.get(1).getMessage());
	}
	
	@Test
	void testDeleteExclusionWhenExclusionDoesNotExists() {
		assertThrows(DrawExclusionNotFoundException.class, () -> victim.delete(1, null));
	}
	
	@Test
	void testDeleteExclusionWhenSoldierIsFromDifferentArmy() {
		Army army1 = getPersistedArmy(armyRepository);
		Army army2 = TestDataCreator.newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "x");
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		armyRepository.saveAndFlush(army2);
		
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army1, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army1, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army1, cjm, organization, rank, 2);
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser groupUser = newGroupUserList(army2, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(groupUser);
		
		List<DrawExclusion> exclusions = newDrawExclusionList(soldiers.get(0), groupUser, 3);
		exclusionRepository.saveAllAndFlush(exclusions);
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.delete(exclusions.get(0).getId(), groupUser));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testListByAnnualQuarterSuccessFully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Soldier soldier = newSoldierList(army, cjm, organization, rank, 1).get(0);
		GroupUser groupUser = TestDataCreator.newGroupUserList(army, cjm, 1).get(0);
		
		groupUserRepository.saveAndFlush(groupUser);
		soldierRepository.saveAndFlush(soldier);
		List<DrawExclusion> exclusionList = TestDataCreator.newDrawExclusionList(soldier, groupUser, 5);
		
		String currentYearQuarter = DateUtils.toQuarterFormat(LocalDate.now());
		LocalDate startQuarterDate = DateUtils.getStartQuarterDate(currentYearQuarter);
		LocalDate endQuarterDate = DateUtils.getEndQuarterDate(currentYearQuarter);
		
		exclusionList.get(0).setStartDate(endQuarterDate);
		exclusionList.get(0).setEndDate(endQuarterDate.plusDays(10));
		
		exclusionList.get(1).setStartDate(startQuarterDate.minusDays(10));
		exclusionList.get(1).setEndDate(startQuarterDate);
		
		exclusionList.get(2).setStartDate(endQuarterDate.minusMonths(1));
		exclusionList.get(2).setEndDate(endQuarterDate.minusMonths(1).plusDays(10));
		
		exclusionList.get(3).setStartDate(startQuarterDate.minusDays(10));
		exclusionList.get(3).setEndDate(startQuarterDate.minusDays(1));
		
		exclusionList.get(4).setStartDate(endQuarterDate.plusDays(1));
		exclusionList.get(4).setEndDate(endQuarterDate.plusDays(10));
		exclusionRepository.saveAllAndFlush(exclusionList);
		
		List<DrawExclusion> foundExclusionList = victim.listByAnnualQuarter(currentYearQuarter, soldier.getId());
		
		assertEquals(3, foundExclusionList.size());
	}
	
	@Test
	void testListBySelectableQuarterPeriod() {
		Draw draw = getPersistedDraw();
		Soldier soldier = draw.getDrawList().getSoldiers().stream().findFirst().get();
		
		List<DrawExclusion> exclusions = victim.listBySelectableQuarterPeriod(soldier.getId());
		
		String expectedMsg = String.format(
			ServiceConstants.GENERATED_SYSTEM_EXCLUSION_MSG, 
			DateUtils.format(draw.getCreationDate()),
			draw.getJusticeCouncil().getAlias(),
			draw.getCjmUser().getAuditorship().getName()
		);
		
		assertEquals(1, exclusions.size());
		assertEquals(expectedMsg, exclusions.get(0).getMessage());
	}
	
	@Test
	void testListByUnfinishedCejDraw() {
		JusticeCouncil council = TestDataCreator.getJusticeCouncil();
		council.setAlias("CEJ");
		justiceCouncilRepository.saveAndFlush(council);
		
		Draw draw = getPersistedDraw();
		draw.setJusticeCouncil(council);
		drawRepository.saveAndFlush(draw);
		
		Soldier soldier = draw.getDrawList().getSoldiers().stream().findFirst().get();
		
		assertEquals(0, victim.generateByUnfinishedCejDraw(soldier.getId()).size());
		
		draw.setFinished(false);
		drawRepository.saveAndFlush(draw);
		
		assertEquals(1, victim.generateByUnfinishedCejDraw(soldier.getId()).size());
	}
	
	private Draw getPersistedDraw() {
		DrawList drawList = TestDataCreator.getPersistedDrawLists(
				armyRepository, cjmRepository, organizationRepository, rankRepository, groupUserRepository, soldierRepository, drawListRepository, 1, 5).get(0);
		
		Draw draw = new Draw();
		draw.setJusticeCouncil(TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository));
		draw.setCjmUser(TestDataCreator.getPersistedCJMUser(cjmUserRepository, auditorshipRepository, cjmRepository));
		draw.setArmy(drawList.getArmy());
		draw.getSoldiers().addAll(drawList.getSoldiers());
		draw.setDrawList(drawList);
		return drawRepository.saveAndFlush(draw);
	}
}
