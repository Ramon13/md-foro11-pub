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
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.SoldierValidator;
import br.com.javamoon.validator.ValidationError;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByNameAndArmyAndCjm(soldierDTO.getName(), army, cjm))
			.thenReturn(Optional.of(soldierDB));
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedNameInSameSoldier() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setId(1);
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByNameAndArmyAndCjm(soldierDTO.getName(), army, cjm))
			.thenReturn(Optional.of(soldierDB));
		
		Mockito.when(organizationRepository.findByArmy(army))
			.thenReturn(Optional.of(List.of(soldierDTO.getMilitaryOrganization()))); 				// necessary to pass through another
																						     		// validations
		Mockito.when(militaryRankRepository.findAllByArmiesIn(army))
			.thenReturn(List.of(soldierDTO.getMilitaryRank()));
		
		victim.saveSoldierValidation(soldierDTO, army, cjm);
	}
	
	@Test
	void testDuplicatedEmail() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByEmailAndArmyAndCjm(
				soldierDTO.getEmail(), army, cjm))
					.thenReturn(Optional.of(soldierDB));
		
		SoldierValidationException exception = assertThrows(SoldierValidationException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testDuplicatedEmailInSameSoldier() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.setId(1);
		
		Soldier soldierDB = TestDataCreator.newSoldier();
		soldierDB.setId(1);
		
		Mockito.when(soldierRepository.findActiveByEmailAndArmyAndCjm(soldierDTO.getEmail(), army, cjm))
			.thenReturn(Optional.of(soldierDB));
		
		Mockito.when(organizationRepository.findByArmy(army))
			.thenReturn(Optional.of(List.of(soldierDTO.getMilitaryOrganization()))); 				// necessary to pass through another
																						     		// validations
		Mockito.when(militaryRankRepository.findAllByArmiesIn(army))
			.thenReturn(List.of(soldierDTO.getMilitaryRank()));
		
		victim.saveSoldierValidation(soldierDTO, army, cjm);
	}
	
	@Test
	void testOrganizationFromDifferentArmy() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		
		Mockito.when(organizationRepository.findByArmy(army))
			.thenReturn(Optional.empty());
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testRankFromDifferentArmy() {
		Army army = TestDataCreator.newArmy();
		CJM cjm = TestDataCreator.newCjm();
		MilitaryRank rank2 = TestDataCreator.newMilitaryRank();
		rank2.setId(2);
		
		SoldierDTO soldierDTO = TestDataCreator.newSoldierDTO();
		soldierDTO.getMilitaryOrganization().setId(1);
		
		Mockito.when(organizationRepository.findByArmy(army))				// Necessary to pass in the organization validatior
			.thenReturn(Optional.of(List.of(soldierDTO.getMilitaryOrganization())));
		
		Mockito.when(militaryRankRepository.findAllByArmiesIn(army)).thenReturn(List.of(rank2));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.saveSoldierValidation(soldierDTO, army, cjm));
		
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
}
