package br.com.javamoon.integration.service;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_DESCRIPTION;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedDrawLists;
import static br.com.javamoon.util.TestDataCreator.getPersistedGroupUserList;
import static br.com.javamoon.util.TestDataCreator.newArmy;
import static br.com.javamoon.util.TestDataCreator.newDrawList;
import static br.com.javamoon.util.TestDataCreator.newSoldierList;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_ID;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_IS_NOT_ON_THE_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncilRepository;
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
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.exception.SoldierHasExclusionException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.DrawListsDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierToListDTO;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrawListServiceIntegrationTest {

	@Autowired
	private DrawListService victim;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMRepository cjmRepository;

	@Autowired
	private MilitaryOrganizationRepository militaryOrganizationRepository;
	
	@Autowired
	private MilitaryRankRepository militaryRankRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private JusticeCouncilRepository justiceCouncilRepository;
	
	@Autowired
	private AuditorshipRepository auditorshipRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@Autowired
	private DrawRepository drawRepository;

	@Test
	void testListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm, null).size());
		
		// deleted list
		drawLists.get(0).setActive(false);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		List<DrawListDTO> drawListsDB = victim.list(army, cjm, null);
		assertEquals(2, drawListsDB.size());
		assertEquals(drawLists.get(2).getDescription(), drawListsDB.get(0).getDescription());
		assertEquals(drawLists.get(1).getDescription(), drawListsDB.get(1).getDescription());
		
		// different army
		Army army2 = newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "a");
		army2.setAlias(Constants.DEFAULT_ARMY_ALIAS + "a");
		armyRepository.save(army2);
		drawLists.get(0).setActive(true);
		drawLists.get(0).setArmy(army2);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		assertEquals(2, victim.list(army, cjm, null).size());
	}
	
	@Test
	void testListByCjmSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		List<GroupUser> users = getPersistedGroupUserList(groupUserRepository, army, cjm, 2);
		
		List<DrawList> drawLists = newDrawList(army, users.get(0), 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(null, cjm, null).size());
		
		// deleted list
		drawLists.get(0).setActive(false);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		List<DrawListDTO> drawListsDB = victim.list(army, cjm, null);
		assertEquals(2, drawListsDB.size());
		assertEquals(drawLists.get(2).getDescription(), drawListsDB.get(0).getDescription());
		assertEquals(drawLists.get(1).getDescription(), drawListsDB.get(1).getDescription());
		
		// different cjm
		CJM newCjm = TestDataCreator.newCjm();
		newCjm.setAlias("new");
		newCjm.setName("new cjm");
		newCjm.setRegions("r1 r2");
		cjmRepository.saveAndFlush(newCjm);
		users.get(1).setCjm(newCjm);
		groupUserRepository.saveAndFlush(users.get(1));
		
		drawLists.get(0).setActive(true);
		drawLists.get(0).setCreationUser(users.get(1));
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		assertEquals(1, victim.list(null, newCjm, null).size());
	}

	@Test
	void testListByArmyAndCjmAndQuarterSuccessfully() {
		List<DrawList> lists = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				3, 6);
		DrawList drawList = lists.get(0);
		
		List<DrawListDTO> listDTO = victim.list(drawList.getArmy(), drawList.getCreationUser().getCjm(), drawList.getYearQuarter());
		
		assertEquals(3, listDTO.size());
		
		drawList.setYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6)));
		drawListRepository.saveAndFlush(drawList);
		
		listDTO = victim.list(drawList.getArmy(), drawList.getCreationUser().getCjm(), drawList.getYearQuarter());
		
		assertEquals(1, listDTO.size());
		assertEquals(drawList.getId(), listDTO.get(0).getId());
	}
	
	@Test
	void testGetListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm, null).size());
		
		DrawListDTO list = victim.getList(drawLists.get(0).getId(), army, cjm);
		assertNotNull(list);
		assertEquals(list.getDescription(), drawLists.get(0).getDescription());		
	}
	
	@Test
	void testGetListNotFound() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		assertThrows(NullPointerException.class, () -> victim.getList(null, army, cjm));
		assertThrows(DrawListNotFoundException.class, () -> victim.getList(1, army, cjm));
		
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		List<DrawList> drawLists = newDrawList(army, creationUser, 1);
		drawListRepository.saveAllAndFlush(drawLists);
		
		Army army2 = newArmy();
		army2.setName(DEFAULT_ARMY_NAME + "a");
		army2.setAlias(Constants.DEFAULT_ARMY_ALIAS + "a");
		armyRepository.save(army2);
		
		assertThrows(DrawListNotFoundException.class, () -> victim.getList(1, army2, cjm));
	}
	
	@Test
	void testSaveListSuccessfully() {
		DrawList persistedDrawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		
		DrawListDTO newListDTO = EntityMapper.fromEntityToDTO(
				TestDataCreator.newDrawList(persistedDrawList.getArmy(), persistedDrawList.getCreationUser(), 1).get(0));
		
		newListDTO.setDescription(DEFAULT_DRAW_LIST_DESCRIPTION + "__");
		newListDTO.setSelectedSoldiers(
			persistedDrawList.getSoldiers()
			.stream()
			.map(s -> s.getId())
			.collect(Collectors.toList()));
		
		victim.save(
				newListDTO,
				persistedDrawList.getArmy(),
				persistedDrawList.getCreationUser().getCjm(),
				persistedDrawList.getCreationUser());
	}
	
	@Test
	void testSaveListWhenDescriptionIsMissing() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(army, creationUser, 1).get(0);
		drawListDTO.setDescription(null);
		
		DrawListValidationException exception = assertThrows(
			DrawListValidationException.class, () -> victim.save(drawListDTO, army, cjm, creationUser)
		);
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_DESCRIPTION, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveListWhenDescriptionIsDuplicated() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		DrawList drawList = TestDataCreator.newDrawList(army, creationUser, 1).get(0);
		drawListRepository.save(drawList);
		
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(army, creationUser, 1).get(0);
		
		DrawListValidationException exception = assertThrows(
			DrawListValidationException.class, () -> victim.save(drawListDTO, army, cjm, creationUser)
		);
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_DESCRIPTION, DRAW_LIST_DESCRIPTION_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveListWhenQuarterYearIsInvalid() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(army, creationUser, 1).get(0);
		drawListDTO.setYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6).plusDays(1)));
		
		DrawListValidationException exception = assertThrows(
			DrawListValidationException.class, () -> victim.save(drawListDTO, army, cjm, creationUser)
		);
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
			new ValidationError(DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testSaveListWhenSoldierListIsBelowMinRequired() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(army, creationUser, 1).get(0);
		drawListDTO.setSelectedSoldiers(List.of(1, 2, 3, 4));
		
		DrawListValidationException exception = assertThrows(
			DrawListValidationException.class, () -> victim.save(drawListDTO, army, cjm, creationUser)
		);
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
			new ValidationError(DRAW_LIST_SELECTED_SOLDIERS, DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testEditDrawListDescriptionSuccessfully() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
				
		drawList.setDescription(DEFAULT_DRAW_LIST_DESCRIPTION + "x");
		
		victim.save(
			EntityMapper.fromEntityToDTO(drawList),
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		DrawList drawListDB = drawListRepository.findById(drawList.getId()).orElseThrow();
		assertEquals(drawList.getDescription(), drawListDB.getDescription());
	}
	
	@Test
	void testEditDrawListWhenModifyDrawList() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		
		LocalDate newDate = DateUtils.fromYearQuarter(drawList.getYearQuarter()).plusMonths(3).plusDays(1);
		drawList.setYearQuarter(DateUtils.toQuarterFormat(newDate));
		
		victim.save(
			EntityMapper.fromEntityToDTO(drawList),
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		DrawList drawListDB = drawListRepository.findById(drawList.getId()).orElseThrow();
		assertEquals(drawList.getYearQuarter(), drawListDB.getYearQuarter());
	}
	
	@Test
	void testEditDrawListWhenSoldierIsAddedInList() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		MilitaryRank rank = militaryRankRepository.findById(1).orElseThrow();
		MilitaryOrganization organization = militaryOrganizationRepository.findById(1).orElseThrow();
		
		Soldier soldier = newSoldierList(
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			organization,
			rank,
			1).get(0);
		
		soldier.setName(DEFAULT_SOLDIER_NAME + "XX");
		soldier.setEmail(DEFAULT_USER_EMAIL + "XX");
		soldierRepository.saveAndFlush(soldier);
		
		assertEquals(6, soldierRepository.findAllActiveByDrawList(drawList.getId(), null, null).size());
		
		DrawListDTO drawListDTO = EntityMapper.fromEntityToDTO(drawList);
		drawListDTO.getSelectedSoldiers().add(soldier.getId());
		victim.save(
			drawListDTO,
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		assertEquals(7, soldierRepository.findAllActiveByDrawList(drawList.getId(), null, null).size());
	}
	
	@Test
	void testEditDrawListWhenSoldierIsRemovedFromList() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		
		assertEquals(6, soldierRepository.findAllActiveByDrawList(drawList.getId(), null, null).size());
		
		DrawListDTO drawListDTO = EntityMapper.fromEntityToDTO(drawList);
		drawListDTO.getDeselectedSoldiers().add(1);				// remove soldier 1 from list
		victim.save(
			drawListDTO,
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		assertEquals(5, soldierRepository.findAllActiveByDrawList(drawList.getId(), null, null).size());
	}
	
	@Test
	void testDeleteDrawList() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		
		assertTrue(drawListRepository.findActiveByIdAndArmyAndCjm(
				drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm()).isPresent());
		
		victim.delete(drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm());
		
		assertTrue(drawListRepository.findActiveByIdAndArmyAndCjm(
				drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm()).isEmpty());
	}
	
	@Test
	void testDuplicateDrawList() {
		DrawList drawList = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				1, 6).get(0);
		DrawList copyOfDrawList = victim.duplicate(
			drawList.getId(),
			drawList.getArmy(), 
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		assertTrue(drawListRepository.findAllActiveByDescriptionAndArmyAndCjm(
			copyOfDrawList.getDescription(), drawList.getArmy(), drawList.getCreationUser().getCjm()).isPresent());
	}
	
	@Test
	void testMapListByQuarter() {
		List<DrawList> lists = getPersistedDrawLists(
				armyRepository,
				cjmRepository,
				militaryOrganizationRepository,
				militaryRankRepository,
				groupUserRepository,
				soldierRepository,
				drawListRepository,
				5, 10);
		String previousQuarter = DateUtils.toQuarterFormat(LocalDate.now().minusMonths(6));
		String futureQuarter = DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6));
		lists.get(0).setYearQuarter(previousQuarter);
		lists.get(1).setYearQuarter(futureQuarter);
		
		drawListRepository.saveAllAndFlush(lists);
		
		List<DrawListsDTO> drawListsDTO = victim.getListsByQuarter(
				lists.stream()
				.map(l -> EntityMapper.fromEntityToDTO(l))
				.collect(Collectors.toList()));
		
		assertEquals(3, drawListsDTO.size());
		assertEquals(futureQuarter, drawListsDTO.get(0).getYearQuarter());
		assertEquals(previousQuarter, drawListsDTO.get(drawListsDTO.size() - 1).getYearQuarter());
	}
	
	@Test
	void testAddSoldierToListSuccessfully() {
		DrawList list = getPersistedDrawList(5);
		Soldier baseSoldier = list.getSoldiers().stream().findFirst().get();
		
		Soldier newSoldier = getPersistedSoldier(
				baseSoldier.getArmy(),
				baseSoldier.getCjm(),
				baseSoldier.getMilitaryOrganization(),
				baseSoldier.getMilitaryRank());
		
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(newSoldier.getId(), list.getId(), DEFAULT_DRAW_LIST_QUARTER_YEAR);
		
		assertEquals(5, list.getSoldiers().size());
		
		victim.addSoldierToList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy());
		
		assertEquals(6, soldierRepository.findAllActiveByDrawList(list.getId()).size());
	}
	
	@Test
	void testaddSoldierToListWhenSoldierIsAlreadyInList() {
		DrawList list = getPersistedDrawList(5);
		Soldier baseSoldier = list.getSoldiers().stream().findFirst().get();
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(baseSoldier.getId(), list.getId(), DEFAULT_DRAW_LIST_QUARTER_YEAR);
		
		assertThrows(SoldierHasExclusionException.class, 
				() -> victim.addSoldierToList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy()) );
	}
	
	@Test
	void testAddSoldierToListWhenSoldierHasSystemExclusions() {
		Draw draw = getPersistedDraw(DRAW_LIST_QUARTER_YEAR);
		DrawList list = draw.getDrawList();
		Soldier soldier = draw.getSoldiers().get(0);
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(soldier.getId(), list.getId(), DEFAULT_DRAW_LIST_QUARTER_YEAR);
		
		assertThrows(SoldierHasExclusionException.class, 
				() -> victim.addSoldierToList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy()) );
	}
	
	@Test
	void testRemoveSoldierFromListSuccessFully() {
		DrawList list = getPersistedDrawList(5);
		Soldier removeSoldier = list.getSoldiers().stream().findFirst().get();
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(removeSoldier.getId(), list.getId(), DEFAULT_DRAW_LIST_QUARTER_YEAR);
		
		victim.removeSoldierFromList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy());
		
		assertEquals(4, soldierRepository.findAllActiveByDrawList(list.getId()).size());
	}
	
	@Test
	void testRemoveSoldierFromListWhenYearQuarterIsInvalid() {
		DrawList list = getPersistedDrawList(5);
		Soldier removeSoldier = list.getSoldiers().stream().findFirst().get();
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(removeSoldier.getId(), list.getId(), DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS);
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.removeSoldierFromList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy()));
		
		assertEquals(exception.getValidationErrors().getError(0),
				new ValidationError(DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS));
	}
	
	@Test
	void testRemoveSoldierFromListWhenSoldierIsNotOnTheList() {
		DrawList list = getPersistedDrawList(5);
		Soldier baseSoldier = list.getSoldiers().stream().findFirst().get();
		
		Soldier newSoldier = getPersistedSoldier(
				baseSoldier.getArmy(),
				baseSoldier.getCjm(),
				baseSoldier.getMilitaryOrganization(),
				baseSoldier.getMilitaryRank());
		
		SoldierToListDTO soldierToListDTO = 
				new SoldierToListDTO(newSoldier.getId(), list.getId(), DEFAULT_DRAW_LIST_QUARTER_YEAR);
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.removeSoldierFromList(soldierToListDTO, list.getCreationUser().getCjm(), list.getArmy()));
		
		assertEquals(exception.getValidationErrors().getError(0),
				new ValidationError(SOLDIER_ID, SOLDIER_IS_NOT_ON_THE_LIST));
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
	
	private Soldier getPersistedSoldier(Army army, CJM cjm, MilitaryOrganization organization, MilitaryRank rank) {
		Soldier soldier = TestDataCreator.newSoldier();
		soldier.setArmy(army);
		soldier.setCjm(cjm);
		soldier.setMilitaryOrganization(organization);
		soldier.setMilitaryRank(rank);
		return soldierRepository.saveAndFlush(soldier);
	}
	
	private Draw getPersistedDraw(String yearQuarter) {
		DrawList drawList = getPersistedDrawList(10);
		
		return TestDataCreator.getPersistedCPJDraw(
				TestDataCreator.getPersistedJusticeCouncil(justiceCouncilRepository),
				TestDataCreator.getPersistedCJMUser(cjmUserRepository, auditorshipRepository, cjmRepository),
				drawList.getArmy(),
				drawList.getSoldiers().stream().filter(x -> x.getId() > 0).limit(5).collect(Collectors.toList()),
				drawList,
				drawRepository
				);
	}
}
