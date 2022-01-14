package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ARMY_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_ORGANIZATION_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_NAME;
import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.TestDataCreator.getPersistedArmy;
import static br.com.javamoon.util.TestDataCreator.getPersistedCJM;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryOrganization;
import static br.com.javamoon.util.TestDataCreator.getPersistedMilitaryRank;
import static br.com.javamoon.util.TestDataCreator.newSoldierList;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

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
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		SoldierDTO soldierDTO = newSoldierList(army, cjm, organization, rank, 1).get(0);
		
		soldierDTO = victim.save(soldierDTO, army, cjm);
		
		Optional<Soldier> soldier = soldierRepository.findById(soldierDTO.getId());
		assertTrue(soldier.isPresent());
		assertEquals(soldierDTO.getName().toUpperCase(), soldier.get().getName());
		assertEquals(soldierDTO.getEmail(), soldier.get().getEmail());
	}
	
	@Test
	void testSaveSoldierWithDuplicatedNameAndEmail() throws ValidationException {
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, organization, rank, 2);
		soldiers.get(0).setName(DEFAULT_SOLDIER_NAME);
		soldiers.get(1).setName(DEFAULT_SOLDIER_NAME);
		soldiers.get(0).setEmail(DEFAULT_USER_EMAIL);
		soldiers.get(1).setEmail(DEFAULT_USER_EMAIL);
		
		victim.save(soldiers.get(0), army, cjm);
		
		SoldierValidationException exception = Assertions.assertThrows(SoldierValidationException.class, 
				() -> victim.save(soldiers.get(1), army, cjm));
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
		
		SoldierDTO soldierDTO = newSoldierList(army2, cjm, organization2, rank, 1).get(0);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(soldierDTO, army, cjm));
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
		
		SoldierDTO soldierDTO = newSoldierList(army2, cjm, organization, rank2, 1).get(0);
		
		assertThrows(IllegalStateException.class, 
				() -> victim.save(soldierDTO, army, cjm));	
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
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, organization, rank, 3);
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
		Army army = getPersistedArmy(armyRepository);
		CJM cjm = getPersistedCJM(cjmRepository);
		
		MilitaryOrganization organization = getPersistedMilitaryOrganization(army, organizationRepository);
		MilitaryRank rank = getPersistedMilitaryRank(army, rankRepository, armyRepository);
		
		Army army2 = TestDataCreator.newArmy();
		army2.setAlias(DEFAULT_ARMY_ALIAS + "x");
		army2.setName(DEFAULT_ARMY_NAME + "x");
		armyRepository.saveAndFlush(army2);
		
		List<SoldierDTO> soldiers = newSoldierList(army, cjm, organization, rank, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		for (SoldierDTO soldierDTO : soldiers)
			soldierRepository.saveAndFlush(EntityMapper.fromDTOToEntity(soldierDTO));
		
		SoldierDTO soldier = victim.getSoldierDTO(2, army, cjm);
		assertNotNull(soldier);
		assertEquals(army, soldier.getArmy());
		assertEquals(cjm, soldier.getCjm());
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
		
		List<SoldierDTO> soldiers = newSoldierList(army2, cjm, organization, rank, 3);
		soldiers.get(0).setArmy(army2);									// setup one soldier with another army
		
		for (SoldierDTO soldierDTO : soldiers)
			soldierRepository.saveAndFlush(EntityMapper.fromDTOToEntity(soldierDTO));
		
		SoldierNotFoundException exception = assertThrows(SoldierNotFoundException.class, 
				() -> victim.getSoldier(1, army, cjm));
		
		assertEquals("soldier not found: " + 1, exception.getMessage());
	}
}

