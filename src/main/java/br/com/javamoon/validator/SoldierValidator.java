package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.INVALID_SOLDIER_ORGANIZATION;
import static br.com.javamoon.validator.ValidationConstants.INVALID_SOLDIER_RANK;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_ID;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_IS_NOT_ON_THE_LIST;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_RANK;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_ORGANIZATION;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_RANK;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.SoldierHasExclusionException;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.infrastructure.web.model.CreateSoldierDTO;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.SoldierDTO;

@Component
public class SoldierValidator {

	private SoldierRepository soldierRepository;
	private MilitaryOrganizationRepository organizationRepository;
	private MilitaryRankRepository militaryRankRepository;

	public SoldierValidator(
			SoldierRepository soldierRepository,
			MilitaryOrganizationRepository organizationRepository,
			MilitaryRankRepository rankRepository) {
		this.soldierRepository = soldierRepository;
		this.organizationRepository = organizationRepository;
		this.militaryRankRepository = rankRepository;
	}

	public void saveSoldierValidation(CreateSoldierDTO createSoldierDTO, Army army, CJM cjm) throws SoldierValidationException {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
			validateName(createSoldierDTO.getName(), validationErrors)
		   ) {
			validateDuplicatedName(createSoldierDTO.getId(), createSoldierDTO.getName(), army, cjm, validationErrors); 
			
			validateMilitaryOrganization(createSoldierDTO.getMilitaryBase(), army.getId(), validationErrors);
			validateMilitaryRank(createSoldierDTO.getMilitaryRank(), army.getId(), validationErrors);
			
			if (!StringUtils.isEmpty(createSoldierDTO.getEmail())) {
				validateEmail(createSoldierDTO.getEmail(), validationErrors);
				validateDuplicatedEmail(createSoldierDTO.getId(), createSoldierDTO.getEmail(), army, cjm, validationErrors);
			}
		}
		
		ValidationUtils.throwOnErrors(SoldierValidationException.class, validationErrors);
	}
	
	public void randAllSoldiersValidation(DrawDTO drawDTO) throws SoldierValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateIfRankBelongsToArmy(drawDTO.getArmy().getId() , drawDTO.getSelectedRanks(), validationErrors);
		
		ValidationUtils.throwOnErrors(SoldierValidationException.class, validationErrors);
	}
	
	public void saveDrawValidation(DrawDTO drawDTO, CJM cjm) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		ValidationUtils.validateRequired(drawDTO.getSoldiers(), DRAW_LIST_SELECTED_SOLDIERS, validationErrors); 
		validateSoldierRank(validationErrors, drawDTO.getSelectedRanks(), drawDTO.getSoldiers());
		validateIfRankBelongsToArmy(drawDTO.getArmy().getId(), drawDTO.getSelectedRanks(), validationErrors);
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void editDrawValidation(DrawDTO drawDTO, CJM cjm) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		ValidationUtils.validateRequired(drawDTO.getSoldiers(), DRAW_LIST_SELECTED_SOLDIERS, validationErrors);
		validateSoldierRank(validationErrors, drawDTO.getSelectedRanks(), drawDTO.getSoldiers());
		validateIfRankBelongsToArmy(drawDTO.getArmy().getId(), drawDTO.getSelectedRanks(), validationErrors);
	
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void replaceSoldierValidation(DrawDTO drawDTO, int replaceIndex) throws SoldierValidationException{
		ValidationErrors validationErrors = new ValidationErrors();
		
		validateMilitaryRank(
			drawDTO.getSelectedRanks().get(replaceIndex),
			drawDTO.getArmy().getId(),
			validationErrors
		);
		
		ValidationUtils.throwOnErrors(SoldierValidationException.class, validationErrors);
	}
	
	public void addToDrawListValidation(SoldierDTO soldierDTO) throws SoldierHasExclusionException{
		validateIfSoldierHasExclusions(soldierDTO);
	}
	
	public void removeFromDrawListValidation(Integer listId, Integer soldierId) {
		ValidationErrors validationErrors = new ValidationErrors();
		validateIfSoldierIsOnList(listId, soldierId, validationErrors);
		
		ValidationUtils.throwOnErrors(SoldierValidationException.class, validationErrors);
	}
	
	private boolean validateName(String name, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(name, SOLDIER_NAME, validationErrors) &&
			ValidationUtils.validateMaxLength(name, SOLDIER_NAME, SOLDIER_NAME_MAX_LEN, validationErrors)
		);
	}
	
	private boolean validateEmail(String email, ValidationErrors validationErrors) {
		return (
			ValidationUtils.validateRequired(email, SOLDIER_EMAIL, validationErrors) &&
			ValidationUtils.validateMaxLength(email, SOLDIER_EMAIL, SOLDIER_EMAIL_MAX_LEN, validationErrors)
		);
	}
	
	private boolean validateMilitaryOrganization(String militaryBaseAlias, Integer armyId, ValidationErrors validationErrors) {
		return validateIfOrganizationBelongsToArmy(militaryBaseAlias, armyId, validationErrors);
	}
	
	private boolean validateMilitaryRank(Integer rankId, Integer armyId, ValidationErrors validationErrors) {
		return validateIfRankBelongsToArmy(armyId, List.of(rankId), validationErrors);
	}
	
	private boolean validateMilitaryRank(String rankAlias, Integer armyId, ValidationErrors validationErrors) {
		return validateIfRankBelongsToArmy(armyId, rankAlias, validationErrors);
	}
	
	private boolean validateIfSoldierIsOnList(Integer listId, Integer soldierId, ValidationErrors validationErrors) {
		if ( soldierRepository.findActiveByDrawList(soldierId, listId).isEmpty() ) {
			validationErrors.add(SOLDIER_ID, SOLDIER_IS_NOT_ON_THE_LIST);
			return false;
		}
		
		return true;
	}
	
	private void validateIfSoldierHasExclusions(SoldierDTO soldierDTO) throws SoldierHasExclusionException{
		if (!soldierDTO.getExclusions().isEmpty())
			throw new SoldierHasExclusionException();
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
	
	private void validateDuplicatedName(Integer id, String name, Army army, CJM cjm, ValidationErrors validationErrors) {
		Optional<Soldier> soldier = soldierRepository.findActiveByNameAndArmyAndCjm(name, army, cjm);
		if (soldier.isPresent() && !soldier.get().getId().equals(id))
			validationErrors.add(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS);
	}
	
	private void validateDuplicatedEmail(Integer id, String email, Army army, CJM cjm, ValidationErrors validationErrors) {
		Optional<Soldier> soldier = soldierRepository.findActiveByEmailAndArmyAndCjm(email, army, cjm);
		if (soldier.isPresent() && !soldier.get().getId().equals(id))
			validationErrors.add(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS);
	}
	
	public boolean validateIfOrganizationBelongsToArmy(String militaryBaseAlias, Integer armyId, ValidationErrors validationErrors) {
		Optional<MilitaryOrganization> selectedOrganization = organizationRepository.findByAlias(militaryBaseAlias);
		List<MilitaryOrganization> organizations = organizationRepository.findByArmy(armyId);
		
		if (
			selectedOrganization.isEmpty() 
			|| organizations.isEmpty() 
			|| !organizations.contains( selectedOrganization.get() )
		) {
			validationErrors.add(SOLDIER_ORGANIZATION, INVALID_SOLDIER_ORGANIZATION);
			return false;
		}
		
		return true;
	}
	
	private boolean validateIfRankBelongsToArmy(Integer armyId, List<Integer> rankIds, ValidationErrors validationErrors) {
		List<Integer> rankIdsByArmy = militaryRankRepository.findAllIdsByArmiesIn(armyId);
		
		if (rankIdsByArmy.isEmpty() || !rankIdsByArmy.containsAll(rankIds)) {
			validationErrors.add(SOLDIER_RANK, INVALID_SOLDIER_RANK);
			return false;
		}
		
		return true;
	}
	
	private boolean validateIfRankBelongsToArmy(Integer armyId, String rankAlias, ValidationErrors validationErrors) {
		Optional<MilitaryRank> rankDb = militaryRankRepository.findByAliasAndbyArmiesIn(rankAlias, armyId);
		
		if (rankDb.isEmpty()) {
			validationErrors.add(SOLDIER_RANK, INVALID_SOLDIER_RANK);
			return false;
		}
		
		return true;
	}
}
