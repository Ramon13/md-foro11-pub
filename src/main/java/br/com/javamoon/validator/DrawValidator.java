package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_PROCESS_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.DRAW_QUARTER_YEAR_OUT_OF_BOUNDS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SELECTED_RANKS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_YEAR_QUARTER;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.PROCESS_NUMBER_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.RANK_LIST_INVALID_SIZE;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_RANK;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_SIZE;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.CouncilType;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.repository.DrawRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.ArmyNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.JusticeCouncilNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.ArmyService;
import br.com.javamoon.service.DrawListService;
import br.com.javamoon.service.JusticeCouncilService;
import br.com.javamoon.util.DateUtils;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DrawValidator {

	private MilitaryRankRepository militaryRankRepository;
	private DrawRepository drawRepository;
	private JusticeCouncilService justiceCouncilService;
	private ArmyService armyService;
	private DrawListService drawListService;

	public DrawValidator(
		MilitaryRankRepository militaryRankRepository,
		DrawRepository drawRepository,
		JusticeCouncilService justiceCouncilService,
		ArmyService armyService,
		DrawListService drawListService) {
		
		this.militaryRankRepository = militaryRankRepository;
		this.drawRepository = drawRepository;
		this.justiceCouncilService = justiceCouncilService;
		this.armyService = armyService;
		this.drawListService = drawListService;
	}

	public void saveDrawValidation(DrawDTO drawDTO, CJM cjm) 
			throws DrawValidationException, JusticeCouncilNotFoundException, ArmyNotFoundException{
		ValidationErrors validationErrors = new ValidationErrors();	
		
		JusticeCouncil justiceCouncil = drawDTO.getJusticeCouncil();
		CouncilType councilType = CouncilType.fromAlias(Objects.isNull(justiceCouncil) ? null : justiceCouncil.getAlias());
		
		if (
			validateJusticeCouncil(justiceCouncil, validationErrors) &&
			validateRanks(drawDTO.getSelectedRanks(), councilType.getCouncilSize(), validationErrors) &&
			validateYearQuarter(drawDTO.getSelectedYearQuarter(), validationErrors) &&
			validateProcessNumber(drawDTO.getProcessNumber(), councilType, validationErrors) && 
			validateSoldiers(drawDTO.getSoldiers(), drawDTO.getSelectedRanks(), councilType, validationErrors)
			) {
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
			armyService.getArmy(drawDTO.getArmy().getId());
			justiceCouncilService.getJusticeCouncil(justiceCouncil.getId());
			drawListService.getList(drawDTO.getSelectedDrawList(), cjm);
			
			validateIfProcessNumberExists(drawDTO.getProcessNumber(), councilType, validationErrors);
			validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
		}
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void randAllSoldiersValidation(DrawDTO drawDTO) throws DrawValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateRanks(drawDTO.getSelectedRanks(), drawDTO.getJusticeCouncil().getCouncilSize(), validationErrors);
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			
		validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
	}
	
	public void replaceSoldierValidation(DrawDTO drawDTO, int replaceIndex) throws DrawValidationException{
		validateIfRankBelongsToArmy(drawDTO.getArmy(), List.of(drawDTO.getSelectedRanks().get(replaceIndex)));
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
			councilType.equals(CouncilType.CPJ) ||
			(
				councilType.equals(CouncilType.CEJ) &&
				ValidationUtils.validateRequired(processNumber, DRAW_PROCESS_NUMBER, validationErrors) &&
				ValidationUtils.validateMaxLength(processNumber, DRAW_PROCESS_NUMBER, 64, validationErrors)
			)
		);
	}
	
	private boolean validateSoldiers(List<SoldierDTO> soldiers, List<Integer> rankIds, CouncilType councilType, ValidationErrors validationErrors) {
		return(
			ValidationUtils.validateRequired(soldiers, DRAW_LIST_SELECTED_SOLDIERS, validationErrors) &&
			validateSoldierListSize(councilType.getCouncilSize(), validationErrors, soldiers) &&
			validateSoldierRank(validationErrors, rankIds, soldiers)
		);
	}
	
	private boolean validateRankListSize(List<Integer> rankIds, int councilSize, ValidationErrors validationErrors) {
		if (rankIds.isEmpty() || rankIds.size() != councilSize) {
			validationErrors.add(DRAW_SELECTED_RANKS, RANK_LIST_INVALID_SIZE);
			return false;
		}
		
		return true;
	}
	
	private boolean validateSoldierListSize(int councilSize, ValidationErrors validationErrors, List<SoldierDTO> soldiers) {
		if (soldiers.isEmpty() || soldiers.size() != councilSize) {
			validationErrors.add(DRAW_LIST_SELECTED_SOLDIERS, SOLDIER_LIST_INVALID_SIZE);
			return false;
		}
		
		return true;
	}
	
	private boolean validateSoldierRank(ValidationErrors validationErrors, List<Integer> rankIds, List<SoldierDTO> soldiers) {
		for (int i = 0; i < soldiers.size(); i++) {
			if (soldiers.get(i).getMilitaryRank().getId().equals(rankIds.get(i)) == Boolean.FALSE) {
				validationErrors.add(DRAW_LIST_SELECTED_SOLDIERS, SOLDIER_LIST_INVALID_RANK);
				return false;
			}
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
	
	public void validateIfRankBelongsToArmy(Army army, List<Integer> rankIds) {
		List<Integer> rankIdsByArmy = militaryRankRepository.findAllIdsByArmiesIn(army);
		
		if (rankIdsByArmy.isEmpty() || !rankIdsByArmy.containsAll(rankIds))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
	
	public void validateIfProcessNumberExists(String processNumber, CouncilType councilType, ValidationErrors validationErrors) {
		if (councilType.equals(CouncilType.CEJ) && drawRepository.findByProcessNumber(processNumber).isPresent())
			validationErrors.add(DRAW_PROCESS_NUMBER, PROCESS_NUMBER_ALREADY_EXISTS);
	}
}
