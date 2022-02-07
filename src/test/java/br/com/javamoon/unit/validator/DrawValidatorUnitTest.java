package br.com.javamoon.unit.validator;

import static br.com.javamoon.util.Constants.DEFAULT_SELECTED_RANKS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.DrawValidator;
import br.com.javamoon.validator.ValidationConstants;

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
		DrawDTO drawDTO = TestDataCreator.newDrawDTO();
		Mockito.when(rankRepository.findAllIdsByArmiesIn(drawDTO.getArmy())).thenReturn(drawDTO.getSelectedRanks());
		
		victim.randAllSoldiersValidation(drawDTO);	
	}
	
	@Test
	void testRandAllSoldiersValidationWhenRankDoesNotBelongsToArmy() {
		DrawDTO drawDTO = TestDataCreator.newDrawDTO();
		drawDTO.getSelectedRanks().add(-1);
		
		Mockito.when(rankRepository.findAllIdsByArmiesIn(drawDTO.getArmy())).thenReturn(DEFAULT_SELECTED_RANKS);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> victim.randAllSoldiersValidation(drawDTO));
		
		assertEquals(ValidationConstants.INCONSISTENT_DATA, exception.getMessage());
	}
}
