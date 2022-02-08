package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_COUNCIl_SIZE;
import static br.com.javamoon.util.Constants.DEFAULT_SELECTED_RANKS;
import static br.com.javamoon.util.TestDataCreator.getJusticeCouncil;
import static br.com.javamoon.util.TestDataCreator.newDrawDTO;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.ValidationConstants;
import br.com.javamoon.validator.ValidationError;

@ExtendWith(MockitoExtension.class)
public class DrawValidatorUnitTest {

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
}
