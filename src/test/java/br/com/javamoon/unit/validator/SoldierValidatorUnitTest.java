package br.com.javamoon.unit.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.STRING_EXCEEDS_MAX_LEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.SoldierValidationException;
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
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setName(null);
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		SoldierDTO soldierDTO1 = TestDataCreator.newSoldierDTO();
		soldierDTO1.setName(StringUtils.rightPad(SOLDIER_NAME, SOLDIER_NAME_MAX_LEN + 1, "x"));
		
		exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO1));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testEmailIsMissingAndExceedMaxLength() {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setEmail(null);
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		SoldierDTO soldierDTO1 = TestDataCreator.newSoldierDTO();
		soldierDTO1.setEmail(StringUtils.rightPad(SOLDIER_EMAIL, SOLDIER_EMAIL_MAX_LEN + 1, "x"));
		
		exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO1));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedName() {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Mockito.when(soldierRepository.findByNameAndArmyAndCjm(
				soldierDTO.getName(), soldierDTO.getArmy(), soldierDTO.getCjm()))
					.thenReturn(Optional.of(List.of(TestDataCreator.newSoldier())));
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedEmail() {
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Mockito.when(soldierRepository.findByEmailAndArmyAndCjm(
				soldierDTO.getEmail(), soldierDTO.getArmy(), soldierDTO.getCjm()))
					.thenReturn(Optional.of(List.of(TestDataCreator.newSoldier())));
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testOrganizationFromDifferentArmy() {
		Army army = TestDataCreator.newArmy();
		
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setArmy(army);
		
		Mockito.when(organizationRepository.findByArmy(army)).thenReturn(Optional.empty());
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testRankFromDifferentArmy() {
		Army army = TestDataCreator.newArmy();
		MilitaryOrganization organization = TestDataCreator.newMilitaryOrganization();
		
		MilitaryRank rank1 = TestDataCreator.newMilitaryRank();
		rank1.setId(1);
		MilitaryRank rank2 = TestDataCreator.newMilitaryRank();
		rank2.setId(2);
		
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setArmy(army);
		soldierDTO.setMilitaryOrganization(organization);
		soldierDTO.setMilitaryRank(rank1);
		
		Mockito.when(organizationRepository.findByArmy(army))				// Necessary to pass in the organization validatior
			.thenReturn(Optional.of(List.of(organization)));
		
		Mockito.when(militaryRankRepository.findAllByArmiesIn(army)).thenReturn(List.of(rank2));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.saveSoldierValidation(soldierDTO));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
}
