package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_END_DATE;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_MESSAGE;
import static br.com.javamoon.validator.ValidationConstants.DRAW_EXCLUSION_START_DATE;
import static br.com.javamoon.validator.ValidationConstants.EQUALS_DATES;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATE_PERIOD;
import static br.com.javamoon.validator.ValidationConstants.IN_THE_PAST;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.exception.DrawExclusionValidationException;
import br.com.javamoon.mapper.DrawExclusionDTO;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DrawExclusionValidator {
	
	public void saveExclusionValidation(DrawExclusionDTO exclusionDTO, GroupUser groupUser) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
			validateMessage(exclusionDTO.getMessage(), validationErrors) &
			validateDates(exclusionDTO.getStartDate(), exclusionDTO.getEndDate(), validationErrors)
		) {
			validateSoldierArmy(exclusionDTO.getSoldier(), groupUser.getArmy(), validationErrors);
		}
		
		ValidationUtils.throwOnErrors(DrawExclusionValidationException.class, validationErrors);
	}
	
	public void deleteExclusionValidation(DrawExclusionDTO exclusionDTO, GroupUser groupUser) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateSoldierArmy(exclusionDTO.getSoldier(), groupUser.getArmy(), validationErrors);
		ValidationUtils.throwOnErrors(DrawExclusionValidationException.class, validationErrors);
	}
	
	public void getExclusionValidation(DrawExclusionDTO exclusionDTO, GroupUser groupUser) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateSoldierArmy(exclusionDTO.getSoldier(), groupUser.getArmy(), validationErrors);
		ValidationUtils.throwOnErrors(DrawExclusionValidationException.class, validationErrors);
	}
	
	private boolean validateMessage(String message, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(message, DRAW_EXCLUSION_MESSAGE, validationErrors) &&
			ValidationUtils.validateMaxLength(message, DRAW_EXCLUSION_MESSAGE, DRAW_EXCLUSION_MAX_LEN, validationErrors)	
		);
	}
	
	private void validateSoldierArmy(Soldier soldier, Army army, ValidationErrors validationErrors) {
		if (!soldier.getArmy().equals(army))
			throw new IllegalStateException(INCONSISTENT_DATA);
	}
	
	private boolean validateDates(LocalDate startDate, LocalDate endDate, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(startDate, DRAW_EXCLUSION_START_DATE, validationErrors) &&
			ValidationUtils.validateRequired(endDate, DRAW_EXCLUSION_END_DATE, validationErrors) &&
			validateDatesEqualsDay(startDate, endDate, validationErrors) &&
			validateDatesInThePast(startDate, endDate, validationErrors) &&
			validateInconsistentDates(startDate, endDate, validationErrors)
		);
	}
	
	private boolean validateDatesEqualsDay(LocalDate startDate, LocalDate endDate, ValidationErrors validationErrors) {
		if (startDate.atStartOfDay().equals(endDate.atStartOfDay())) {
			validationErrors.add(DRAW_EXCLUSION_START_DATE, EQUALS_DATES);
			return false;
		}
		
		return true;
	}
	
	private boolean validateDatesInThePast(LocalDate startDate, LocalDate endDate, ValidationErrors validationErrors) {
		LocalDate now = LocalDate.now();
		if (startDate.isBefore(now) || endDate.isBefore(now)) {
			validationErrors.add(DRAW_EXCLUSION_START_DATE, IN_THE_PAST);
			return false;
		}
		
		return true;
	}
	
	private boolean validateInconsistentDates(LocalDate startDate, LocalDate endDate, ValidationErrors validationErrors) {
		if (startDate.isAfter(endDate)) {
			validationErrors.add(DRAW_EXCLUSION_END_DATE, INCONSISTENT_DATE_PERIOD);
			return false;
		}
		
		return true;
	}
}
