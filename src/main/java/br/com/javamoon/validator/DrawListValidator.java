package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_DESCRIPTION_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.exception.DrawListValidationException;
import br.com.javamoon.mapper.DrawListDTO;
import br.com.javamoon.util.DateUtils;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DrawListValidator {

	private DrawListRepository drawListRepository;
	
	public DrawListValidator(DrawListRepository drawListRepository) {
		this.drawListRepository = drawListRepository;
	}

	public void saveListValidation(DrawListDTO drawListDTO, Army army, CJM cjm) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
		    validateDescription(drawListDTO.getDescription(), validationErrors) &&
		    validateQuarterYear(drawListDTO.getYearQuarter(), validationErrors)
		) {
			validateDuplicatedDescription(drawListDTO.getDescription(), drawListDTO.getId(), army, cjm, validationErrors);
			validateSelectableQuarter(drawListDTO.getYearQuarter(), validationErrors);
			validateMinSoldierListSize(drawListDTO.getId(), drawListDTO.getSelectedSoldiers().size(), validationErrors);
		}
		
		ValidationUtils.throwOnErrors(DrawListValidationException.class, validationErrors);
	}
	
	private boolean validateDescription(String description, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(description, DRAW_LIST_DESCRIPTION, validationErrors) &&
			ValidationUtils.validateMaxLength(description, DRAW_LIST_DESCRIPTION, DRAW_LIST_DESCRIPTION_MAX_LEN, validationErrors)
		);
	}
	
	private boolean validateQuarterYear(String quarterYear, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(quarterYear, DRAW_LIST_QUARTER_YEAR, validationErrors) &&
			ValidationUtils.validateMaxLength(quarterYear, DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_MAX_LEN, validationErrors)
		);
	}
	
	private void validateDuplicatedDescription(String description, Integer listId, Army army, CJM cjm, ValidationErrors validationErrors) {
		Optional<DrawList> drawList = drawListRepository.findAllActiveByDescriptionAndArmyAndCjm(description, army, cjm);
		if (!drawList.isEmpty() && !drawList.get().getId().equals(listId))
			validationErrors.add(DRAW_LIST_DESCRIPTION, DRAW_LIST_DESCRIPTION_ALREADY_EXISTS);
	}
	
	private void validateSelectableQuarter(String quarterYear, ValidationErrors validationErrors) {
		if (!DateUtils.isSelectableQuarter(quarterYear))
			validationErrors.add(DRAW_LIST_QUARTER_YEAR, DRAW_LIST_QUARTER_YEAR_OUT_OF_BOUNDS);
	}
	
	private void validateMinSoldierListSize(Integer listId, Integer listSize, ValidationErrors validationErrors) {
		if (Objects.isNull(listId) && listSize < ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS_MIN_LEN)
			validationErrors.add(ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS, DRAW_LIST_SELECTED_SOLDIERS_BELOW_MIN_LEN);
	}
}
