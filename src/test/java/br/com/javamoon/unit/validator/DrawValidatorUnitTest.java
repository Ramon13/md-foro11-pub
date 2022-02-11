package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_COUNCIl_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_REPLACE_RANK_ID;
import static br.com.javamoon.util.Constants.DEFAULT_SELECTED_RANKS;
import static br.com.javamoon.util.TestDataCreator.getJusticeCouncil;
import static br.com.javamoon.util.TestDataCreator.newDrawDTO;
import static br.com.javamoon.validator.ValidationConstants.DRAW_JUSTICE_COUNCIL;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_YEAR_QUARTER;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.ValidationConstants;
import br.com.javamoon.validator.ValidationError;

@ExtendWith(MockitoExtension.class)
public class DrawValidatorUnitTest {

	@Mock
	private DrawValidator victim;
	
	@Mock
	private MilitaryRankRepository rankRepository;
	
	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newDrawValidator(rankRepository);
	}
	
	@Test
	void testRandAllSoldiersValidationSuccessfully(){
		DrawDTO drawDTO = newDrawDTO();
		Mockito.when(rankRepository.findAllIdsByArmiesIn(drawDTO.getArmy())).thenReturn(drawDTO.getSelectedRanks());
		
		victim.randAllSoldiersValidation(drawDTO);	
	}
	
	@Test
	void testRandAllSoldiersValidationWhenRankDoesNotBelongsToArmy() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.getSelectedRanks().set(0, -1);
		
		Mockito.when(rankRepository.findAllIdsByArmiesIn(drawDTO.getArmy())).thenReturn(DEFAULT_SELECTED_RANKS);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> victim.randAllSoldiersValidation(drawDTO));
		
		assertEquals(ValidationConstants.INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testValidateRankListSize() {
		DrawDTO drawDTO = newDrawDTO();
		JusticeCouncil justiceCouncil = getJusticeCouncil();
		justiceCouncil.setCouncilSize(DEFAULT_COUNCIl_SIZE - 1);
		drawDTO.setJusticeCouncil(justiceCouncil);
		
		drawDTO.setSelectedRanks(new ArrayList<Integer>(0));
		assertThrows(DrawValidationException.class, () -> victim.randAllSoldiersValidation(drawDTO));
		
		drawDTO.setSelectedRanks(DEFAULT_SELECTED_RANKS);
		DrawValidationException exception = 
				assertThrows(DrawValidationException.class, () -> victim.randAllSoldiersValidation(drawDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		
		assertEquals(
				new ValidationError(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testReplaceSoldierValidationSuccessfully() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setSelectedRanks(DEFAULT_SELECTED_RANKS);
		
		Mockito.when(rankRepository.findAllIdsByArmiesIn(drawDTO.getArmy())).thenReturn(DEFAULT_SELECTED_RANKS);
		
		victim.replaceSoldierValidation(drawDTO, DEFAULT_REPLACE_RANK_ID);
	}
	
	@Test
	void testSaveDrawValidationSuccessfully() {
		DrawDTO drawDTO = newDrawDTO();
		victim.saveDrawValidation(drawDTO);
	}
	
	@Test
	void testSaveDrawValidationWhenJusticeCouncilIsMissing() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setDefaultJusticeCouncil(getJusticeCouncil());
		drawDTO.setJusticeCouncil(null);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		assertEquals(new ValidationError(DRAW_JUSTICE_COUNCIL, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenRankListSizeIsInvalid() {
		DrawDTO drawDTO = newDrawDTO();
		JusticeCouncil justiceCouncil = getJusticeCouncil();
		justiceCouncil.setCouncilSize(DEFAULT_COUNCIl_SIZE - 1);
		drawDTO.setJusticeCouncil(justiceCouncil);
		
		drawDTO.setSelectedRanks(null);
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		assertEquals(new ValidationError(DRAW_SELECTED_RANKS, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		drawDTO.setSelectedRanks(new ArrayList<Integer>(0));
		assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		
		drawDTO.setSelectedRanks(DEFAULT_SELECTED_RANKS);
		exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		
		assertEquals(
				new ValidationError(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenYearQuarterIsEmpty() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setSelectedYearQuarter(null);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		assertEquals(new ValidationError(DRAW_YEAR_QUARTER, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawwhenYearQuarterIsOutOfRange() {
		DrawDTO drawDTO = newDrawDTO();
		
		drawDTO.setSelectedYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6)));
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		assertEquals(
			new ValidationError(DRAW_YEAR_QUARTER, ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS),
			exception.getValidationErrors().getError(0)
		);
		
		drawDTO.setSelectedYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().minusMonths(6)));
		exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO));
		assertEquals(
			new ValidationError(DRAW_YEAR_QUARTER, ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS),
			exception.getValidationErrors().getError(0)
		);
	}
}
