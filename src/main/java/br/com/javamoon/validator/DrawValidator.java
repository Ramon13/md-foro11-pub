package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_YEAR_QUARTER;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.service.SoldierService;
import br.com.javamoon.util.DateUtils;

@Component
public class DrawValidator {

	private final MilitaryRankRepository militaryRankRepository;
	private final DrawRepository drawRepository;
	private final JusticeCouncilService justiceCouncilService;
	private final ArmyService armyService;
	private final SoldierService soldierService;

	public DrawValidator(
		MilitaryRankRepository militaryRankRepository,
		DrawRepository drawRepository,
		JusticeCouncilService justiceCouncilService,
		ArmyService amryService,
		SoldierService soldierService) {
		
		this.militaryRankRepository = militaryRankRepository;
		this.drawRepository = drawRepository;
		this.justiceCouncilService = justiceCouncilService;
		this.armyService = amryService;
		this.soldierService = soldierService;
	}

	public void saveDrawValidation(DrawDTO drawDTO) {
		ValidationErrors validationErrors = new ValidationErrors();
		CouncilType councilType = CouncilType.fromAlias(drawDTO.getJusticeCouncil().getAlias());
		
		if (
			validateJusticeCouncil(drawDTO.getJusticeCouncil(), validationErrors) &&
			validateRanks(drawDTO.getSelectedRanks(), drawDTO.getJusticeCouncil().getCouncilSize(), validationErrors) &&
			validateYearQuarter(drawDTO.getSelectedYearQuarter(), validationErrors) &&
			validateProcessNumber(drawDTO.getProcessNumber(), councilType, validationErrors)
			) {
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
			justiceCouncilService.getJusticeCouncil(drawDTO.getId());
			armyService.getArmy(drawDTO.getArmy().getId());
			validateIfProcessNumberExists(drawDTO.getProcessNumber(), councilType, validationErrors);	
		}
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void randAllSoldiersValidation(DrawDTO drawDTO) throws DrawValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateRanks(drawDTO.getSelectedRanks(), drawDTO.getJusticeCouncil().getCouncilSize(), validationErrors);
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
		validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks().toArray(new Integer[0]));
	}
	
	public void replaceSoldierValidation(DrawDTO drawDTO, int replaceIndex) throws DrawValidationException{
		
		validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks().get(replaceIndex));
	}
	
	private boolean validateJusticeCouncil(JusticeCouncil council, ValidationErrors validationErrors) {
		return ValidationUtils.validateRequired(council, ValidationConstants.DRAW_JUSTICE_COUNCIL, validationErrors);
	}
	
	private boolean validateRanks(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(rankIds, DRAW_SELECTED_RANKS, validationErrors) &&
			validateRankListSize(rankIds, councilSize, validationErrors)
		);
	}
	
	private boolean validateYearQuarter(String yearQuarter, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(yearQuarter, DRAW_YEAR_QUARTER, validationErrors) &&
			validateIfYearQuarterIsInSelectableRange(yearQuarter, validationErrors)
		);
	}
	
	private boolean validateProcessNumber(String processNumber, CouncilType councilType, ValidationErrors validationErrors) {
		return (
			councilType.equals(CouncilType.CEJ) &&
			ValidationUtils.validateRequired(processNumber, DRAW_PROCESS_NUMBER, validationErrors) &&
			ValidationUtils.validateMaxLength(processNumber, DRAW_PROCESS_NUMBER, 64, validationErrors)
		);
	}
	
	private boolean validateRankListSize(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		if (rankIds.isEmpty() || rankIds.size() != councilSize) {
			validationErrors.add(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE);
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
	
	public void validateIfRankBelongsToArmy(Army army, Integer...rankIds) {
		List<Integer> rankIdsByArmy = militaryRankRepository.findAllIdsByArmiesIn(army);
		
		if (rankIdsByArmy.isEmpty() || !rankIdsByArmy.containsAll(List.of(rankIds)))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
	
	public void validateIfProcessNumberExists(String processNumber, CouncilType councilType, ValidationErrors validationErrors) {
		if (drawRepository.findByProcessNumber(processNumber).isPresent())
			validationErrors.add(DRAW_PROCESS_NUMBER, ValidationConstants.PROCESS_NUMBER_ALREADY_EXISTS);
	}
}
