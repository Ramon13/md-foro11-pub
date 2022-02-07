package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_DESCRIPTION;
import static br.com.javamoon.util.Constants.DEFAULT_DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.STRING_EXCEEDS_MAX_LEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawListValidator;
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
public class DrawListValidatorUnitTest {

	private DrawListValidator victim;

	@Mock
	private DrawListRepository drawListRepository;
	
	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newDrawListValidator(drawListRepository);
	}
	
	@Test
	void testSaveValidatorSuccessfully() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		Mockito.when(DateUtils.isSelectableQuarter(drawListDTO.getYearQuarter())).thenReturn(true);
		
		victim.saveListValidation(drawListDTO, null, null);
	}
	
	@Test
	void testSaveValidatorWhenDescriptionIsMissing() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		drawListDTO.setDescription(null);
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_DESCRIPTION, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidatorWhenDescriptionExceedsMaxLen() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		drawListDTO.setDescription(StringUtils.rightPad(DEFAULT_DRAW_LIST_DESCRIPTION, DRAW_LIST_DESCRIPTION_MAX_LEN + 1, 'x'));

		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_DESCRIPTION, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidatorWhenQuarterYearIsMissing() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		drawListDTO.setYearQuarter(null);
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_QUARTER_YEAR, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidatorWhenQuarterYearExceedsMaxLen() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		drawListDTO.setYearQuarter(StringUtils.rightPad(DEFAULT_DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_MAX_LEN + 1, 'x'));
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_QUARTER_YEAR, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidatorWhenDescriptionAlreadyExists() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		DrawList drawList = TestDataCreator.newDrawList(null, null, 1).get(0);
		drawList.setId(1);

		Mockito.when(DateUtils.isSelectableQuarter(drawListDTO.getYearQuarter())).thenReturn(true);
		
		Mockito.when(drawListRepository.findAllActiveByDescriptionAndArmyAndCjm(drawListDTO.getDescription(), null, null))
			.thenReturn(Optional.of(drawList));
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_DESCRIPTION, DRAW_LIST_DESCRIPTION_ALREADY_EXISTS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidationWhenAnnualQuarterIsOutOfBounds() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
	
		Mockito.when(drawListRepository.findAllActiveByDescriptionAndArmyAndCjm(drawListDTO.getDescription(), null, null))
		.thenReturn(Optional.empty());
		
		Mockito.when(DateUtils.isSelectableQuarter(drawListDTO.getYearQuarter())).thenReturn(false);
		
		DrawListValidationException exception = assertThrows(DrawListValidationException.class, 
				() -> victim.saveListValidation(drawListDTO, null, null));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveValidationWhenSelectedSoldiersIsBelowMinValue() {
		DrawListDTO drawListDTO = TestDataCreator.newDrawListDTO(null, null, 1).get(0);
		drawListDTO.setSelectedSoldiers(List.of(1, 2, 3, 4));
		
		Mockito.when(drawListRepository.findAllActiveByDescriptionAndArmyAndCjm(drawListDTO.getDescription(), null, null))
		.thenReturn(Optional.empty());
		
		Mockito.when(DateUtils.isSelectableQuarter(drawListDTO.getYearQuarter())).thenReturn(true);
		
		DrawListValidationException exception = assertThrows(
			DrawListValidationException.class, 
			() -> victim.saveListValidation(drawListDTO, null, null)
		);
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
			new ValidationError(DRAW_LIST_SELECTED_SOLDIERS, DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN),
			exception.getValidationErrors().getError(0)
		);
	}
}
