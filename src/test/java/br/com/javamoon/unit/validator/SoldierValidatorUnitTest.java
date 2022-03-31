package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_ARMY_ID;
import static br.com.javamoon.util.Constants.DEFAULT_CPJ_RANKS;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_ID;
import static br.com.javamoon.util.Constants.DEFAULT_RANK_ID;
import static br.com.javamoon.util.Constants.DEFAULT_REPLACE_RANK_ID;
import static br.com.javamoon.util.Constants.DEFAULT_SOLDIER_ID;
import static br.com.javamoon.util.TestDataCreator.newDrawDTO;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.INVALID_SOLDIER_ORGANIZATION;
import static br.com.javamoon.validator.ValidationConstants.INVALID_SOLDIER_RANK;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_ID;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_IS_NOT_ON_THE_LIST;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_ORGANIZATION;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_RANK;
import static br.com.javamoon.validator.ValidationConstants.STRING_EXCEEDS_MAX_LEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.SoldierHasExclusionException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.SoldierValidator;
import br.com.javamoon.validator.ValidationError;

@ExtendWith(MockitoExtension.class)
public class SoldierValidatorUnitTest {

	private SoldierValidator victim;
	
	@Mock
	private SoldierRepository soldierRepository;
	
	@Mock
	private MilitaryRankRepository militaryRankRepository;
	
	@Mock
	private MilitaryOrganizationRepository organizationRepository;
	
	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newSoldierValidator(soldierRepository, organizationRepository, militaryRankRepository);
	}
	
	@Test
	void testNameIsMissingAndExceedMaxLength() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setName(null);
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		SoldierDTO soldierDTO1 = TestDataCreator.newSoldierDTO();
		soldierDTO1.setName(StringUtils.rightPad(SOLDIER_NAME, SOLDIER_NAME_MAX_LEN + 1, "x"));
		
		exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO1, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testEmailIsMissingAndExceedMaxLength() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setEmail(null);
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		SoldierDTO soldierDTO1 = TestDataCreator.newSoldierDTO();
		soldierDTO1.setEmail(StringUtils.rightPad(SOLDIER_EMAIL, SOLDIER_EMAIL_MAX_LEN + 1, "x"));
		
		exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO1, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedName() {
		Army army = getArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when( soldierRepository.findActiveByNameAndArmyAndCjm(soldierDTO.getName(), army, cjm) )
			.thenReturn( Optional.of(soldierDB) );

		mockDuplicatedEmailValidation();
		mockMilitaryOrganizationValidation( soldierDTO.getMilitaryOrganization() );
		mockMilitaryRankValidation( soldierDTO.getMilitaryRank() );
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedNameInSameSoldier() {
		Army army = getArmy();
		
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setId(1);
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByNameAndArmyAndCjm(soldierDTO.getName(), army, cjm))
			.thenReturn( Optional.of(soldierDB) );
		
		mockDuplicatedEmailValidation();
		mockMilitaryOrganizationValidation( soldierDTO.getMilitaryOrganization() );
		mockMilitaryRankValidation( soldierDTO.getMilitaryRank() );
		
		victim.saveSoldierValidation(soldierDTO, army, cjm);
	}
	
	@Test
	void testDuplicatedEmail() {
		Army army = getArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when( soldierRepository.findActiveByEmailAndArmyAndCjm( soldierDTO.getEmail(), army, cjm) )
			.thenReturn( Optional.of(soldierDB) );
		
		mockDuplicatedNameValidation();
		mockMilitaryOrganizationValidation( soldierDTO.getMilitaryOrganization() );
		mockMilitaryRankValidation( soldierDTO.getMilitaryRank() );
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
			new ValidationError(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS), 
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testDuplicatedEmailInSameSoldier() {
		Army army = getArmy();
		CJM cjm = TestDataCreator.newCjm();
		
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setId(1);
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByEmailAndArmyAndCjm(soldierDTO.getEmail(), army, cjm))
			.thenReturn(Optional.of(soldierDB));
		
		mockDuplicatedNameValidation();
		mockMilitaryOrganizationValidation( soldierDTO.getMilitaryOrganization() );
		mockMilitaryRankValidation( soldierDTO.getMilitaryRank() );
		
		victim.saveSoldierValidation(soldierDTO, army, cjm);
	}
	
	@Test
	void testOrganizationFromDifferentArmy() {
		Army army = getArmy();
		
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Mockito.when( organizationRepository.findByArmy( army.getId() ) )
			.thenReturn( Collections.emptyList() );
		
		mockDuplicatedNameValidation();
		mockDuplicatedEmailValidation();
		mockMilitaryRankValidation( soldierDTO.getMilitaryRank() );
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getErrorList().size() );
		assertEquals(
			new ValidationError(SOLDIER_ORGANIZATION, INVALID_SOLDIER_ORGANIZATION),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testRankFromDifferentArmy() {
		Army army = getArmy();
		CJM cjm = TestDataCreator.newCjm();
		
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.getMilitaryOrganization().setId(1);
		
		Mockito.when( militaryRankRepository.findAllIdsByArmiesIn( army.getId() ) )
			.thenReturn( List.of(DEFAULT_RANK_ID + 1) );
		
		mockDuplicatedNameValidation();
		mockDuplicatedEmailValidation();
		mockMilitaryOrganizationValidation( soldierDTO.getMilitaryOrganization() );
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getErrorList().size() );
		assertEquals(
			new ValidationError(SOLDIER_RANK, INVALID_SOLDIER_RANK),
			exception.getErrorList().get(0)
		);
	}
	
	@Test
	void testReplaceSoldierValidationSuccessfully() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setSelectedRanks(DEFAULT_CPJ_RANKS);
		
		Mockito.when( militaryRankRepository.findAllIdsByArmiesIn( drawDTO.getArmy().getId() ) )
			.thenReturn(DEFAULT_CPJ_RANKS);
		
		victim.replaceSoldierValidation(drawDTO, DEFAULT_REPLACE_RANK_ID);
	}
	
	@Test
	void testAddToDrawListValidationSuccessfully() {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.getExclusions().clear();
		
		victim.addToDrawListValidation(soldierDTO);
	}
	
	@Test
	void testAddToDrawListValidationWhenSoldierHasExclusions() {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.getExclusions().add(new DrawExclusionDTO());
		
		assertThrows(SoldierHasExclusionException.class, () -> victim.addToDrawListValidation(soldierDTO));
	}
	
	@Test
	void testRemoveFromDrawListValidationSuccessfully() {
		Mockito.when( soldierRepository.findActiveByDrawList(DEFAULT_SOLDIER_ID, DEFAULT_DRAW_LIST_ID) )
			.thenReturn(Optional.of(TestDataCreator.newSoldier()));
		
		victim.removeFromDrawListValidation(DEFAULT_DRAW_LIST_ID, DEFAULT_SOLDIER_ID);
	}
	
	@Test
	void testRemoveFromDrawListValidationWhenSoldierIsNotOnTheList() {
		Mockito.when( soldierRepository.findActiveByDrawList(DEFAULT_SOLDIER_ID, DEFAULT_DRAW_LIST_ID) )
		.thenReturn(Optional.empty());
	
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.removeFromDrawListValidation(DEFAULT_DRAW_LIST_ID, DEFAULT_SOLDIER_ID));
		
		assertEquals(exception.getValidationErrors().getError(0), new ValidationError(SOLDIER_ID, SOLDIER_IS_NOT_ON_THE_LIST));
	}
	
	private void mockDuplicatedNameValidation() {
		Mockito.when( soldierRepository.findActiveByNameAndArmyAndCjm( any(), any(), any()) )
			.thenReturn( Optional.empty() );
	}
	
	private void mockDuplicatedEmailValidation() {
		Mockito.when(
			soldierRepository.findActiveByEmailAndArmyAndCjm( any(), any(), any() )
		).thenReturn( Optional.empty() );
	}
	
	private void mockMilitaryOrganizationValidation(MilitaryOrganization militaryOrganization) {
		Mockito.when( organizationRepository.findById(any()) )
			.thenReturn( Optional.of(militaryOrganization) );
		
		Mockito.when( organizationRepository.findByArmy( any() ) )
			.thenReturn( List.of(militaryOrganization) );
	}
	
	private void mockMilitaryRankValidation(MilitaryRank rank) {
		Mockito.when( militaryRankRepository.findAllIdsByArmiesIn( any() ) )
			.thenReturn( List.of(rank.getId()) );
	}
	
	private Army getArmy() {
		Army army = TestDataCreator.newArmy();
		army.setId(DEFAULT_ARMY_ID);
		return army;
	}
}
