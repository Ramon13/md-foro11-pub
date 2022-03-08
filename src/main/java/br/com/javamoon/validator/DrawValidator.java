package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_YEAR_QUARTER;
import static br.com.javamoon.validator.ValidationConstants.PROCESS_NUMBER_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_SIZE;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.exception.ArmyNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.JusticeCouncilNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.util.DateUtils;

@Component
public class DrawValidator {
	
	private DrawRepository drawRepository;

	public DrawValidator(DrawRepository drawRepository) {
		this.drawRepository = drawRepository;
	}

	public void saveDrawValidation(DrawDTO drawDTO, CJM cjm) 
			throws DrawValidationException, JusticeCouncilNotFoundException, ArmyNotFoundException{
		ValidationErrors validationErrors = new ValidationErrors();	
		
		JusticeCouncil justiceCouncil = drawDTO.getJusticeCouncil();
		CouncilType councilType = CouncilType.fromAlias(Objects.isNull(justiceCouncil) ? null : justiceCouncil.getAlias());
		
		if (
			validateJusticeCouncil(justiceCouncil, validationErrors) &&
			validateRankList(drawDTO.getSelectedRanks(), councilType.getCouncilSize(), validationErrors) &&
			validateYearQuarter(drawDTO.getSelectedYearQuarter(), validationErrors) &&
			validateProcessNumber(drawDTO.getProcessNumber(), councilType, validationErrors) && 
			validateSoldierListSize(councilType.getCouncilSize(), validationErrors, drawDTO.getSoldiers())
		) {
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
			validateIfProcessNumberExists(drawDTO.getProcessNumber(), councilType, validationErrors);
		}
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void editDrawValidation(DrawDTO drawDTO) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateRankList(drawDTO.getSelectedRanks(), drawDTO.getCouncilType().getCouncilSize(), validationErrors);
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	private boolean validateRankList(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(rankIds, DRAW_SELECTED_RANKS, validationErrors) &&
			validateRankListSize(rankIds, councilSize, validationErrors)
		);
	}
	
	private boolean validateRankListSize(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		if (rankIds.isEmpty() || rankIds.size() != councilSize) {
			validationErrors.add(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE);
			return false;
		}	
		return true;
	}
	
	public void randAllSoldiersValidation(DrawDTO drawDTO) throws DrawValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (validateProcessNumber(drawDTO.getProcessNumber(), drawDTO.getCouncilType(), validationErrors)) {
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
		
    		validateIfProcessNumberExists(drawDTO.getProcessNumber(), drawDTO.getCouncilType(), validationErrors);
		}
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	private boolean validateJusticeCouncil(JusticeCouncil council, ValidationErrors validationErrors) {
		return ValidationUtils.validateRequired(council, ValidationConstants.DRAW_JUSTICE_COUNCIL, validationErrors);
	}
	
	private boolean validateYearQuarter(String yearQuarter, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(yearQuarter, DRAW_YEAR_QUARTER, validationErrors) &&
			validateIfYearQuarterIsInSelectableRange(yearQuarter, validationErrors)
		);
	}
	
	private boolean validateProcessNumber(String processNumber, CouncilType councilType, ValidationErrors validationErrors) {
		return (
			councilType.equals(CouncilType.CPJ) ||
			(
				councilType.equals(CouncilType.CEJ) &&
				ValidationUtils.validateRequired(processNumber, DRAW_PROCESS_NUMBER, validationErrors) &&
				ValidationUtils.validateMaxLength(processNumber, DRAW_PROCESS_NUMBER, 64, validationErrors)
			)
		);
	}
	
	private boolean validateSoldierListSize(int councilSize, ValidationErrors validationErrors, List<SoldierDTO> soldiers) {
		if (soldiers.isEmpty() || soldiers.size() != councilSize) {
			validationErrors.add(DRAW_LIST_SELECTED_SOLDIERS, SOLDIER_LIST_INVALID_SIZE);
			return false;
		}
		
		return true;
	}
	
	private boolean validateIfYearQuarterIsInSelectableRange(String yearQuarter, ValidationErrors validationErrors) {
		if (Objects.nonNull(yearQuarter) && !DateUtils.isSelectableQuarter(yearQuarter)) {
			validationErrors.add(DRAW_YEAR_QUARTER, DRAW_QUARTER_YEAR_OUT_OF_BOUNDS);
			return false;
		}
		
		return true;	
	}
	
	public void validateIfProcessNumberExists(String processNumber, CouncilType councilType, ValidationErrors validationErrors) {
		if (councilType.equals(CouncilType.CEJ) && drawRepository.findByProcessNumber(processNumber).isPresent())
			validationErrors.add(DRAW_PROCESS_NUMBER, PROCESS_NUMBER_ALREADY_EXISTS);
	}
}
