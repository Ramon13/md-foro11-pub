package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_EXCLUSION_MESSAGE;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_END_DATE;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_MESSAGE;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_START_DATE;
import static br.com.javamoon.validator.ValidationConstants.EQUALS_DATES;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATE_PERIOD;
import static br.com.javamoon.validator.ValidationConstants.IN_THE_PAST;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.STRING_EXCEEDS_MAX_LEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.DrawExclusionValidationException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawExclusionValidator;
import br.com.javamoon.validator.ValidationError;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DrawExclusionValidatorUnitTest {

	private DrawExclusionValidator victim;

	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newExclusionValidator();
	}
	
	@Test
	void testMessageIsMissingAndExceedMaxLength() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setMessage(null);
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_MESSAGE, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		exclusion.setMessage(StringUtils.rightPad(DEFAULT_EXCLUSION_MESSAGE, DRAW_EXCLUSION_MAX_LEN + 1, 'x'));
		exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_MESSAGE, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenDatesIsInTheSameDay() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now());
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_START_DATE, EQUALS_DATES), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenDatesIsInThePast() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now().minusDays(1));
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_START_DATE, IN_THE_PAST), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenStartDateIsAfterEndDate() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now().plusDays(1));
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_END_DATE, INCONSISTENT_DATE_PERIOD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenSoldierIsFromDifferentArmy() {
		Army army1 = TestDataCreator.newArmy();
		army1.setId(1);
		Army army2 = TestDataCreator.newArmy();
		army1.setId(2);
		
		Soldier soldier = TestDataCreator.newSoldier();
		soldier.setArmy(army1);
		
		GroupUser groupUser = TestDataCreator.newGroupUserList(army2, null, 1).get(0);
		
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(soldier, groupUser, 1).get(0));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.saveExclusionValidation(exclusion, groupUser));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testDeleteExclusionValidationWhenSoldierIsFromDifferentArmy() {
		Army army1 = TestDataCreator.newArmy();
		army1.setId(1);
		Army army2 = TestDataCreator.newArmy();
		army1.setId(2);
		
		Soldier soldier = TestDataCreator.newSoldier();
		soldier.setArmy(army1);
		
		GroupUser groupUser = TestDataCreator.newGroupUserList(army2, null, 1).get(0);
		
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(soldier, groupUser, 1).get(0));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.deleteExclusionValidation(exclusion, groupUser));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
}
