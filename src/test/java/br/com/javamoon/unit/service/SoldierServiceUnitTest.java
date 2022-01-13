package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.*;
import static br.com.javamoon.validator.ValidationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.SoldierNotFoundException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.SoldiersPagination;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
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
	
	@Test
	void testSaveSoldierSuccessfully() throws ValidationException {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army);
		MilitaryRank rank = getPersistedMilitaryRank(army);
		SoldierDTO soldierDTO = createPersistenceReadySoldierDTO(army, cjm, organization, rank);
		
		soldierDTO = victim.save(soldierDTO, army, cjm);
		
		Optional<Soldier> soldier = soldierRepository.findById(soldierDTO.getId());
		assertTrue(soldier.isPresent());
		assertEquals(DEFAULT_SOLDIER_NAME.toUpperCase(), soldier.get().getName());
		assertEquals(DEFAULT_USER_EMAIL, soldier.get().getEmail());
	}
	
	@Test
	void testSaveSoldierWithDuplicatedNameAndEmail() throws ValidationException {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army);
		MilitaryRank rank = getPersistedMilitaryRank(army);
		SoldierDTO soldierDTO1 = createPersistenceReadySoldierDTO(army, cjm, organization, rank);
		SoldierDTO soldierDTO2 = createPersistenceReadySoldierDTO(army, cjm, organization, rank);
		
		victim.save(soldierDTO1, army, cjm);
		
		SoldierValidationException exception = Assertions.assertThrows(SoldierValidationException.class, 
				() -> victim.save(soldierDTO2, army, cjm));
		assertEquals(2, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
		assertEquals(new ValidationError(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS), exception.getValidationErrors().getError(1));
	}
	
	@Test
	void testSaveSoldierWithInvalidOrganization() throws ValidationException {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		getPersistedMilitaryOrganization(army);
		MilitaryRank rank = getPersistedMilitaryRank(army);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		MilitaryOrganization organization2 = TestDataCreator.newMilitaryOrganization();
		organization2.setAlias(DEFAULT_ORGANIZATION_ALIAS + "x");
		organization2.setName(DEFAULT_ORGANIZATION_NAME + "x");
		organization2.setArmy(army2);
		organizationRepository.saveAndFlush(organization2);
		
		SoldierDTO soldierDTO = createPersistenceReadySoldierDTO(army, cjm, organization2, rank);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(soldierDTO, army, cjm));
	}
	
	@Test
	void testSaveSoldierWithInvalidRank() throws ValidationException {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army);
		getPersistedMilitaryRank(army);
		
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
		
		SoldierDTO soldierDTO = createPersistenceReadySoldierDTO(army, cjm, organization, rank2);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(soldierDTO, army, cjm));	
	}
	
	@Test
	void testListPaginationSuccessfully() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		for (SoldierDTO soldierDTO : soldiers)
			soldierRepository.saveAndFlush(EntityMapper.fromDTOToEntity(soldierDTO));
		
		SoldiersPagination listPagination = victim.listPagination(army, cjm, TestDataCreator.newPaginationFilter());
		
		assertNotNull(listPagination);
		assertEquals(2, listPagination.getSoldiers().size());
		assertEquals(2, listPagination.getTotal());
		assertEquals(soldiers.get(1).getName(), listPagination.getSoldiers().get(0).getName());
		assertEquals(soldiers.get(2).getName(), listPagination.getSoldiers().get(1).getName());
	}
	
	@Test
	void testGetSoldierSuccessfully() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		for (SoldierDTO soldierDTO : soldiers)
			soldierRepository.saveAndFlush(EntityMapper.fromDTOToEntity(soldierDTO));
		
		SoldierDTO soldier = victim.getSoldier(2, army, cjm);
		assertNotNull(soldier);
		assertEquals(army, soldier.getArmy());
		assertEquals(cjm, soldier.getCjm());
	}
	
	@Test
	void testGetSoldierWhenIdDoesNotBelongsToArmy() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		for (SoldierDTO soldierDTO : soldiers)
			soldierRepository.saveAndFlush(EntityMapper.fromDTOToEntity(soldierDTO));
		
		SoldierNotFoundException exception = assertThrows(SoldierNotFoundException.class, 
				() -> victim.getSoldier(1, army, cjm));
		
		assertEquals("soldier not found: " + 1, exception.getMessage());
	}
	
	private MilitaryOrganization getPersistedMilitaryOrganization(Army army) {
		MilitaryOrganization organization = TestDataCreator.newMilitaryOrganization();
		organization.setArmy(army);
		return organizationRepository.saveAndFlush(organization);	
	}
	
	private MilitaryRank getPersistedMilitaryRank(Army army) {
		MilitaryRank rank = TestDataCreator.newMilitaryRank();
		army.getMilitaryRanks().add(rank);
		rankRepository.saveAndFlush(rank);
		armyRepository.saveAndFlush(army);
		
		return rank;
	}
	
	private SoldierDTO createPersistenceReadySoldierDTO(Army army, CJM cjm, MilitaryOrganization organization, MilitaryRank rank) {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setMilitaryRank(rank);
		soldierDTO.setMilitaryOrganization(organization);
		
		return soldierDTO;
	}
	
	private List<SoldierDTO> newSoldierList(Army army, CJM cjm, int listSize) {
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army);
		MilitaryRank rank = getPersistedMilitaryRank(army);
		
		List<SoldierDTO> soldiers = new ArrayList<SoldierDTO>();
		SoldierDTO soldierDTO;
		while (listSize-- > 0) {
			soldierDTO = TestDataCreator.newSoldierDTO();
			soldierDTO.setName(StringUtils.rightPad(DEFAULT_SOLDIER_NAME, SOLDIER_NAME_MAX_LEN - listSize, 'x'));
			soldierDTO.setEmail(StringUtils.rightPad(DEFAULT_USER_EMAIL, SOLDIER_EMAIL_MAX_LEN - listSize, 'x'));
			soldierDTO.setMilitaryRank(rank);
			soldierDTO.setMilitaryOrganization(organization);
			soldierDTO.setArmy(army);
			soldierDTO.setCjm(cjm);
			soldiers.add(soldierDTO);
		}
		
		return soldiers;
	}
}

