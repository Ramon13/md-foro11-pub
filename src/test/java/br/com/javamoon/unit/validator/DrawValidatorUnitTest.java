package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.CEJ_COUNCIl_ALIAS;
import static br.com.javamoon.util.Constants.DEFAULT_COUNCIL_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_REPLACE_RANK_ID;
import static br.com.javamoon.util.Constants.DEFAULT_SELECTED_RANKS;
import static br.com.javamoon.util.TestDataCreator.getJusticeCouncil;
import static br.com.javamoon.util.TestDataCreator.newDrawDTO;
import static br.com.javamoon.validator.ValidationConstants.DRAW_JUSTICE_COUNCIL;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_YEAR_QUARTER;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;
import static br.com.javamoon.validator.ValidationConstants.REQUIRED_FIELD;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_SIZE;
import static br.com.javamoon.validator.ValidationConstants.STRING_EXCEEDS_MAX_LEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.DateUtils;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.ValidationConstants;
import br.com.javamoon.validator.ValidationError;
import java.time.LocalDate;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DrawValidatorUnitTest {

	@Mock
	private DrawValidator victim;
	
	@Mock
	private MilitaryRankRepository rankRepository;
	
	@BeforeEach
	void setupEach() {
		victim = TestDataCreator.newDrawValidator(rankRepository, null, null, null, null);
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
		justiceCouncil.setCouncilSize(DEFAULT_COUNCIL_SIZE - 1);
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
	void testSaveDrawValidationWhenJusticeCouncilIsMissing() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setDefaultJusticeCouncil(getJusticeCouncil());
		drawDTO.setJusticeCouncil(null);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_JUSTICE_COUNCIL, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenRankListSizeIsInvalid() {
		DrawDTO drawDTO = newDrawDTO();
		JusticeCouncil justiceCouncil = getJusticeCouncil();
		justiceCouncil.setCouncilSize(DEFAULT_COUNCIL_SIZE - 1);
		drawDTO.setJusticeCouncil(justiceCouncil);
		
		drawDTO.setSelectedRanks(null);
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_SELECTED_RANKS, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
		
		drawDTO.setSelectedRanks(new ArrayList<Integer>(0));
		assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		
		drawDTO.setSelectedRanks(DEFAULT_SELECTED_RANKS);
		exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		
		assertEquals(
				new ValidationError(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenYearQuarterIsEmpty() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.setSelectedYearQuarter(null);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_YEAR_QUARTER, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawwhenYearQuarterIsOutOfRange() {
		DrawDTO drawDTO = newDrawDTO();
		
		drawDTO.setSelectedYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().plusMonths(6)));
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(
			new ValidationError(DRAW_YEAR_QUARTER, DRAW_QUARTER_YEAR_OUT_OF_BOUNDS),
			exception.getValidationErrors().getError(0)
		);
		
		drawDTO.setSelectedYearQuarter(DateUtils.toQuarterFormat(LocalDate.now().minusMonths(6)));
		exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(
			new ValidationError(DRAW_YEAR_QUARTER, DRAW_QUARTER_YEAR_OUT_OF_BOUNDS),
			exception.getValidationErrors().getError(0)
		);
	}
	
	@Test
	void testSaveDrawWhenProcessNumberIsEmpty() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.getJusticeCouncil().setAlias(CEJ_COUNCIl_ALIAS);
		
		drawDTO.setProcessNumber(Strings.EMPTY);
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_PROCESS_NUMBER, REQUIRED_FIELD), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenProcessNumberExceedsMaxLength() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.getJusticeCouncil().setAlias(CEJ_COUNCIl_ALIAS);
		
		drawDTO.setProcessNumber(StringUtils.rightPad("x", 65, "x"));
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_PROCESS_NUMBER, STRING_EXCEEDS_MAX_LEN), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testSaveDrawWhenSoldierListExceedsCouncilSize() {
		DrawDTO drawDTO = newDrawDTO();
		drawDTO.getSoldiers().add(new SoldierDTO());
		
		DrawValidationException exception = assertThrows(DrawValidationException.class, () -> victim.saveDrawValidation(drawDTO, null));
		assertEquals(new ValidationError(DRAW_LIST_SELECTED_SOLDIERS, SOLDIER_LIST_INVALID_SIZE), exception.getValidationErrors().getError(0));
	}
}
