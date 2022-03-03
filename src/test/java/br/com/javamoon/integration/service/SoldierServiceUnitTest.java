package br.com.javamoon.integration.service;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_CJM_REGIONS;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_EMAIL;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_EMAIL_NON_CONTAINING_KEY;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME_NON_CONTAINING_KEY;
import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedGroupUserList;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryOrganization;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryRank;
import static br.com.javamoon.util.TestDataCreator.newDrawList;
import static br.com.javamoon.util.TestDataCreator.newSoldierList;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.ArmyRepository;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.service.ValidationException;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SoldierServiceUnitTest {
	
	@Autowired
	private SoldierService victim;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Autowired
	private MilitaryOrganizationRepository organizationRepository;
	
	@Autowired
	private MilitaryRankRepository rankRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Test
	void testSaveSoldierSuccessfully() throws ValidationException {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		Soldier soldier = newSoldierList(army, cjm, organization, rank, 1).get(0);
		
		Integer soldierId = victim.save(EntityMapper.fromEntityToDTO(soldier), army, cjm).getId();
		
		Optional<Soldier> soldierDB = soldierRepository.findById(soldierId);
		assertTrue(soldierDB.isPresent());
		assertEquals(soldier.getName().toUpperCase(), soldierDB.get().getName());
		assertEquals(soldier.getEmail(), soldierDB.get().getEmail());
	}
	
	@Test
	void testEditSoldierNameSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		
		soldierRepository.saveAllAndFlush(soldiers);
		
		Soldier soldier = soldiers.get(0);
		soldier.setName(DEFAULT_SOLDIER_NAME + "xxxxX");
		
		victim.edit(EntityMapper.fromEntityToDTO(soldier), army, cjm);
		
		Optional<Soldier> soldierDB = soldierRepository.findById(soldier.getId());
		
		assertTrue(soldierDB.isPresent());
		assertEquals(soldier.getName().toUpperCase(), soldierDB.get().getName());
	}
	
	@Test
	void testDeleteSoldierSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		
		soldierRepository.saveAllAndFlush(soldiers);
		
		assertEquals(3, soldierRepository.findAllActiveByArmyAndCjm(army, cjm, null).size());
		
		victim.delete(soldiers.get(0).getId(), army, cjm);
		
		List<Soldier> soldiersDB = soldierRepository.findAllActiveByArmyAndCjm(army, cjm, null);
		assertEquals(2, soldiersDB.size());
		assertEquals(soldiers.get(1).getName(), soldiersDB.get(0).getName());
		assertEquals(soldiers.get(2).getName(), soldiersDB.get(1).getName());
	}
	
	@Test
	void testSaveSoldierWithDuplicatedNameAndEmail() throws ValidationException {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 2);
		soldiers.get(0).setName(soldiers.get(1).getName().toUpperCase());
		soldiers.get(0).setEmail(soldiers.get(1).getEmail());
		
		soldierRepository.saveAndFlush(soldiers.get(0));
		
		SoldierValidationException exception = Assertions.assertThrows(SoldierValidationException.class, 
				() -> victim.save(EntityMapper.fromEntityToDTO(soldiers.get(1)), army, cjm));
		assertEquals(2, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
		assertEquals(new ValidationError(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS), exception.getValidationErrors().getError(1));
	}
	
	@Test
	void testSaveSoldierWithInvalidOrganization() throws ValidationException {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		MilitaryOrganization organization2 = TestDataCreator.newMilitaryOrganization();
		organization2.setAlias(DEFAULT_ORGANIZATION_ALIAS + "x");
		organization2.setName(DEFAULT_ORGANIZATION_NAME + "x");
		organization2.setArmy(army2);
		organizationRepository.saveAndFlush(organization2);
		
		Soldier soldier = newSoldierList(army2, cjm, organization2, rank, 1).get(0);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(EntityMapper.fromEntityToDTO(soldier), army, cjm));
	}
	
	@Test
	void testSaveSoldierWithInvalidRank() throws ValidationException {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		MilitaryRank rank2 = TestDataCreator.newMilitaryRank();
		rank2.setAlias(DEFAULT_RANK_ALIAS + "x");
		rank2.setName(DEFAULT_RANK_NAME + "x");
		army2.getMilitaryRanks().add(rank2);
		rankRepository.saveAndFlush(rank2);
		armyRepository.saveAndFlush(army2);
		
		Soldier soldier = newSoldierList(army2, cjm, organization, rank2, 1).get(0);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(EntityMapper.fromEntityToDTO(soldier), army, cjm));	
	}
	
	@Test
	void testListPaginationSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		soldierRepository.saveAllAndFlush(soldiers);
		
		SoldiersPagination listPagination = victim.listPagination(army, cjm, TestDataCreator.newPaginationFilter());
		
		assertNotNull(listPagination);
		assertEquals(2, listPagination.getSoldiers().size());
		assertEquals(2, listPagination.getTotal());
		assertEquals(soldiers.get(1).getName(), listPagination.getSoldiers().get(0).getName());
		assertEquals(soldiers.get(2).getName(), listPagination.getSoldiers().get(1).getName());
	}
	
	@Test
	void testListPaginationByDrawListSuccessfully() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		soldierRepository.saveAllAndFlush(soldiers);
		
		GroupUser creationUser = getPersistedGroupUserList(groupUserRepository, army, cjm, 1).get(0);
		
		List<DrawList> drawLists = newDrawList(army, creationUser, 3);
		drawLists.get(0).setSoldiers(soldiers.stream().collect(Collectors.toSet()));
		drawListRepository.saveAllAndFlush(drawLists);
		
		SoldiersPagination soldiersPagination = victim.listPagination(drawLists.get(0).getId(), TestDataCreator.newPaginationFilter());
		assertEquals(3, soldiersPagination.getSoldiers().size());
		
		soldiersPagination = victim.listPagination(drawLists.get(1).getId(), TestDataCreator.newPaginationFilter());
		assertTrue(soldiersPagination.getSoldiers().isEmpty());
		
		soldiers.get(0).setActive(false);
		soldierRepository.saveAndFlush(soldiers.get(0));
		soldiersPagination = victim.listPagination(drawLists.get(0).getId(), TestDataCreator.newPaginationFilter());
		assertEquals(2, soldiersPagination.getSoldiers().size());
	}
	
	@Test
	void testGetSoldierWhithDifferentArmies() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<Soldier> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		soldierRepository.saveAllAndFlush(soldiers);
		
		Soldier soldierDB = victim.getSoldierOrElseThrow(soldiers.get(1).getId(), army, cjm);
		assertNotNull(soldierDB);
		assertEquals(army, soldierDB.getArmy());
		assertEquals(cjm, soldierDB.getCjm());
	}
	
	@Test
	void testGetSoldierByCjmSucessfully() {
		List<Soldier> soldiers = TestDataCreator.getPersistedSoldierList(
				soldierRepository, armyRepository, organizationRepository, rankRepository, cjmRepository, 3);
		Soldier persistedSoldier = soldiers.get(0);
		CJM cjm = persistedSoldier.getCjm();
		
		assertEquals(persistedSoldier, victim.getSoldierByCjm(persistedSoldier.getId(), cjm));
		
		CJM newCjm = TestDataCreator.newCjm();
		newCjm.setAlias(DEFAULT_CJM_ALIAS + "__");
		newCjm.setName(DEFAULT_CJM_NAME + "__");
		newCjm.setRegions((DEFAULT_CJM_REGIONS + "__"));
		cjmRepository.saveAndFlush(newCjm);
		
		persistedSoldier.setCjm(newCjm);
		soldierRepository.saveAndFlush(persistedSoldier);
		
		assertEquals(persistedSoldier, victim.getSoldierByCjm(persistedSoldier.getId(), newCjm));
	}
	
	@Test
	void testGetSoldierByCjmWhenIdDoesNotExists() {
		CJM cjm = getPersistedCJM(cjmRepository);
		assertThrows(SoldierNotFoundException.class, () -> victim.getSoldierByCjm(1, cjm));
	}
	
	@Test
	void tesstGetSoldierCjmWhenSoldierBelongsToAnotherCjm() {
		List<Soldier> soldiers = TestDataCreator.getPersistedSoldierList(
				soldierRepository, armyRepository, organizationRepository, rankRepository, cjmRepository, 3);
		Soldier persistedSoldier = soldiers.get(0);
		CJM cjm = persistedSoldier.getCjm();
		
		assertEquals(persistedSoldier, victim.getSoldierByCjm(persistedSoldier.getId(), cjm));
		
		CJM newCjm = TestDataCreator.newCjm();
		newCjm.setAlias(DEFAULT_CJM_ALIAS + "__");
		newCjm.setName(DEFAULT_CJM_NAME + "__");
		newCjm.setRegions((DEFAULT_CJM_REGIONS + "__"));
		cjmRepository.saveAndFlush(newCjm);
		
		assertThrows(SoldierNotFoundException.class, () -> victim.getSoldierByCjm(persistedSoldier.getId(), newCjm));
	}
	
	@Test
	void testGetSoldierWhenIdDoesNotBelongsToArmy() {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<Soldier> soldiers = newSoldierList(army2, cjm, organization, rank, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		soldierRepository.saveAllAndFlush(soldiers);
		
		SoldierNotFoundException exception = assertThrows(SoldierNotFoundException.class, 
				() -> victim.getSoldier(soldiers.get(0).getId(), army, cjm));
		
		assertEquals("soldier not found: " + soldiers.get(0).getId(), exception.getMessage());
	}
	
	@Test
	void testListSoldierContainingWhenSoldierNameContainsKey() {
		List<Soldier> persistedSoldiers = getPersistedSoldiers(10);
		Army army = persistedSoldiers.get(0).getArmy();
		CJM cjm = persistedSoldiers.get(0).getCjm();
		
		List<Soldier> containingSoldiers = victim.listSoldierContaining(DEFAULT_SOLDIER_NAME, army, cjm);
		
		assertNotNull(containingSoldiers);
		assertEquals(persistedSoldiers.size(), containingSoldiers.size());
		
		containingSoldiers = victim.listSoldierContaining(DEFAULT_SOLDIER_NAME_NON_CONTAINING_KEY, army, cjm);
		assertEquals(0, containingSoldiers.size());
	}
	
	@Test
	void testListSoldierContainingWhenSoldierEmailContainsKey() {
		List<Soldier> persistedSoldiers = getPersistedSoldiers(10);
		Army army = persistedSoldiers.get(0).getArmy();
		CJM cjm = persistedSoldiers.get(0).getCjm();
		
		List<Soldier> containingSoldiers = victim.listSoldierContaining(DEFAULT_SOLDIER_EMAIL, army, cjm);
		
		assertNotNull(containingSoldiers);
		assertEquals(persistedSoldiers.size(), containingSoldiers.size());
		
		containingSoldiers = victim.listSoldierContaining(DEFAULT_SOLDIER_EMAIL_NON_CONTAINING_KEY, army, cjm);
		assertEquals(0, containingSoldiers.size());
	}
	
	private List<Soldier> getPersistedSoldiers(int listSize){
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<Soldier> soldiers = TestDataCreator.newSoldierList(army, cjm, organization, rank, listSize);
		return soldierRepository.saveAllAndFlush(soldiers);
	}
}

