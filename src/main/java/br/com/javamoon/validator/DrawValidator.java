package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;

@Component
public class DrawValidator {

	private MilitaryRankRepository militaryRankRepository;

	public DrawValidator(MilitaryRankRepository rankRepository) {
		this.militaryRankRepository = rankRepository;
	}

	public void randAllSoldiersValidation(DrawDTO drawDTO) throws DrawValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateRanks(drawDTO.getSelectedRanks(), drawDTO.getJusticeCouncil().getCouncilSize(), validationErrors);
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
		validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
	}
	
	private boolean validateRanks(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		return validateRankListSize(rankIds, councilSize, validationErrors);
	}
	
	private boolean validateRankListSize(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		if (rankIds.isEmpty() || rankIds.size() != councilSize) {
			validationErrors.add(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE);
			return false;
		}
		
		return true;
	}
	
	private void validateIfRankBelongsToArmy(Army army, List<Integer> rankIds) {
		List<Integer> rankIdsByArmy = militaryRankRepository.findAllIdsByArmiesIn(army);
		
		if (rankIdsByArmy.isEmpty() || !rankIdsByArmy.containsAll(rankIds))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
}
