package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.*;
import static br.com.javamoon.validator.ValidationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
				() -> victim.saveExclusionValidation(exclusion));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_MESSAGE, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		exclusion.setMessage(StringUtils.rightPad(DEFAULT_EXCLUSION_MESSAGE, DRAW_EXCLUSION_MAX_LEN + 1, 'x'));
		exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_MESSAGE, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenDatesIsInTheSameDay() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now());
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_START_DATE, EQUALS_DATES), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenDatesIsInThePast() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now().minusDays(1));
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_START_DATE, IN_THE_PAST), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveExclusionValidationWhenStartDateIsAfterEndDate() {
		DrawExclusionDTO exclusion = EntityMapper.fromEntityToDTO(TestDataCreator.newDrawExclusionList(null, null, 1).get(0));
		exclusion.setStartDate(LocalDate.now().plusDays(1));
		exclusion.setEndDate(LocalDate.now());
		
		DrawExclusionValidationException exception = assertThrows(DrawExclusionValidationException.class, 
				() -> victim.saveExclusionValidation(exclusion));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_EXCLUSION_END_DATE, INCONSISTENT_DATE_PERIOD), exception.getValidationErrors().getError(0));
	}
}
