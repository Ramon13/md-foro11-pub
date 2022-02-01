package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedGroupUserList;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryOrganization;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryRank;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
public class DrawListServiceUnitTest {

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
	
	@Test
	void testListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm).size());
		
		// deleted list
		drawLists.get(0).setActive(false);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		List<DrawListDTO> drawListsDB = victim.list(army, cjm);
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
		
		assertEquals(2, victim.list(army, cjm).size());
	}
	
	@Test
	void testListByCjmSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		List<GroupUser> users = getPersistedGroupUserList(groupUserRepository, army, cjm, 2);
		
		List<DrawList> drawLists = newDrawList(army, users.get(0), 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.listByCjm(cjm).size());
		
		// deleted list
		drawLists.get(0).setActive(false);
		drawListRepository.saveAndFlush(drawLists.get(0));
		
		List<DrawListDTO> drawListsDB = victim.list(army, cjm);
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
		
		assertEquals(1, victim.listByCjm(newCjm).size());
	}
	
	@Test
	void testGetListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
	
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawListRepository.saveAllAndFlush(drawLists);
		
		assertEquals(3, victim.list(army, cjm).size());
		
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
		getPersistedDrawList();
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
		drawListDTO.setQuarterYear(DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6).plusDays(1)));
		
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
		DrawList drawList = getPersistedDrawList();
		drawList.setDescription(Constants.DEFAULT_DRAW_LIST_DESCRIPTION + "x");
		
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
		DrawList drawList = getPersistedDrawList();
		
		LocalDate newDate = DateUtils.fromQuarterYear(drawList.getQuarterYear()).plusMonths(3).plusDays(1);
		drawList.setQuarterYear(DateUtils.toQuarterFormat(newDate));
		
		victim.save(
			EntityMapper.fromEntityToDTO(drawList),
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		DrawList drawListDB = drawListRepository.findById(drawList.getId()).orElseThrow();
		assertEquals(drawList.getQuarterYear(), drawListDB.getQuarterYear());
	}
	
	@Test
	void testEditDrawListWhenSoldierIsAddedInList() {
		DrawList drawList = getPersistedDrawList();
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
		
		assertEquals(6, soldierRepository.findAllActiveByDrawList(drawList.getId()).size());
		
		DrawListDTO drawListDTO = EntityMapper.fromEntityToDTO(drawList);
		drawListDTO.getSelectedSoldiers().add(soldier.getId());
		victim.save(
			drawListDTO,
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		assertEquals(7, soldierRepository.findAllActiveByDrawList(drawList.getId()).size());
	}
	
	@Test
	void testEditDrawListWhenSoldierIsRemovedFromList() {
		DrawList drawList = getPersistedDrawList();
		
		assertEquals(6, soldierRepository.findAllActiveByDrawList(drawList.getId()).size());
		
		DrawListDTO drawListDTO = EntityMapper.fromEntityToDTO(drawList);
		drawListDTO.getDeselectedSoldiers().add(1);				// remove soldier 1 from list
		victim.save(
			drawListDTO,
			drawList.getArmy(),
			drawList.getCreationUser().getCjm(),
			drawList.getCreationUser()
		);
		
		assertEquals(5, soldierRepository.findAllActiveByDrawList(drawList.getId()).size());
	}
	
	@Test
	void testDeleteDrawList() {
		DrawList drawList = getPersistedDrawList();
		
		assertTrue(drawListRepository.findActiveByIdAndArmyAndCjm(
				drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm()).isPresent());
		
		victim.delete(drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm());
		
		assertTrue(drawListRepository.findActiveByIdAndArmyAndCjm(
				drawList.getId(), drawList.getArmy(), drawList.getCreationUser().getCjm()).isEmpty());
	}
	
	@Test
	void testDuplicateDrawList() {
		DrawList drawList = getPersistedDrawList();
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
	void testMapListByQuater() {
		List<DrawList> lists = getDrawLists(5, 10);
		
		LocalDate nextQuarter = LocalDate.now().plusMonths(DateUtils.MONTHS_IN_QUARTER);
		lists.get(0).setQuarterYear(DateUtils.toQuarterFormat(nextQuarter));
		
		Map<String, List<DrawList>> drawListsMap = victim.mapListByQuarter(lists);
		
		assertEquals(lists.get(0).getQuarterYear(), drawListsMap.keySet().stream().findFirst().get());
	}
	
	private List<DrawList> getDrawLists(int listSize, int numberOfSoldiers){
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, militaryOrganizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, militaryRankRepository, armyRepository);
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<Soldier> soldiers = TestDataCreator.newSoldierList(army, cjm, organization, rank, numberOfSoldiers);
		soldierRepository.saveAllAndFlush(soldiers);
		
		List<DrawList> lists = TestDataCreator.newDrawList(army, creationUser, listSize);
		
		lists.stream().forEach(list -> list.getSoldiers().addAll(soldiers));
		return lists;
	}
	
	private DrawList getPersistedDrawList() {
		 DrawList list = getDrawLists(1, 6).get(0);
		
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(list.getArmy(), list.getCreationUser(), 1).get(0);
		drawListDTO.setSelectedSoldiers(
			list.getSoldiers().stream()
			.map(r -> r.getId())
			.collect(Collectors.toList())
		);
		
		return victim.save(drawListDTO, list.getArmy(), list.getCreationUser().getCjm(), list.getCreationUser());
	}
}
