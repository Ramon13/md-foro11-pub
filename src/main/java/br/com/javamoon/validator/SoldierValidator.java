package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.DRAW_LIST_SELECTED_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_LIST_INVALID_RANK;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.exception.SoldierValidationException;
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

	public void saveSoldierValidation(SoldierDTO soldierDTO, Army army, CJM cjm) throws SoldierValidationException {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
				validateName(soldierDTO.getName(), validationErrors) &&
				validateEmail(soldierDTO.getEmail(), validationErrors)
		   ) {
			
			validateDuplicatedName(soldierDTO.getId(), soldierDTO.getName(), army, cjm, validationErrors);
			validateDuplicatedEmail(soldierDTO.getId(), soldierDTO.getEmail(), army, cjm, validationErrors);
		}
		
		ValidationUtils.throwOnErrors(SoldierValidationException.class, validationErrors);
		
		validateIfOrganizationBelongsToArmy(soldierDTO.getMilitaryOrganization(), army, validationErrors);
		validateIfRankBelongsToArmy(army, List.of(soldierDTO.getMilitaryRank().getId()));
	}
	
	public void randAllSoldiersValidation(DrawDTO drawDTO) throws DrawValidationException{
		validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
	}
	
	public void saveDrawValidation(DrawDTO drawDTO, CJM cjm) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (ValidationUtils.validateRequired(drawDTO.getSoldiers(), DRAW_LIST_SELECTED_SOLDIERS, validationErrors) &&
			validateSoldierRank(validationErrors, drawDTO.getSelectedRanks(), drawDTO.getSoldiers()) ) {
			
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
		}
		
		ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
	}
	
	public void editDrawValidation(DrawDTO drawDTO, CJM cjm) {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
			ValidationUtils.validateRequired(drawDTO.getSoldiers(), DRAW_LIST_SELECTED_SOLDIERS, validationErrors) &&
			validateSoldierRank(validationErrors, drawDTO.getSelectedRanks(), drawDTO.getSoldiers())
		) {
			ValidationUtils.throwOnErrors(DrawValidationException.class, validationErrors);
			validateIfRankBelongsToArmy(drawDTO.getArmy(), drawDTO.getSelectedRanks());
		}
	}
	
	public void replaceSoldierValidation(DrawDTO drawDTO, int replaceIndex) throws DrawValidationException{
		validateIfRankBelongsToArmy(drawDTO.getArmy(), List.of(drawDTO.getSelectedRanks().get(replaceIndex)));
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
	
	private void validateIfOrganizationBelongsToArmy(MilitaryOrganization organization, Army army, ValidationErrors validationErrors) {
		Optional<List<MilitaryOrganization>> organizations = organizationRepository.findByArmy(army);
		if (organizations.isEmpty() || !organizations.get().contains(organization))
			throw new IllegalStateException(INCONSISTENT_DATA);
	}
	
	public void validateIfRankBelongsToArmy(Army army, List<Integer> rankIds) {
		List<Integer> rankIdsByArmy = militaryRankRepository.findAllIdsByArmiesIn(army);
		
		if (rankIdsByArmy.isEmpty() || !rankIdsByArmy.containsAll(rankIds))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
}
