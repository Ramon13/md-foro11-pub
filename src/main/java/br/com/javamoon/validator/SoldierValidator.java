package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.SoldierValidationException;
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

	public void saveSoldierValidation(SoldierDTO soldierDTO) throws SoldierValidationException {
		ValidationErrors validationErrors = new ValidationErrors();
		
		if (
				validateName(soldierDTO.getName(), validationErrors) &&
				validateEmail(soldierDTO.getEmail(), validationErrors)
		   ) {
			
			validateDuplicatedName(soldierDTO.getName(), soldierDTO.getArmy(), soldierDTO.getCjm(), validationErrors);
			validateDuplicatedEmail(soldierDTO.getEmail(), soldierDTO.getArmy(), soldierDTO.getCjm(), validationErrors);
		}
		
		throwOnErrors(validationErrors);
		
		validateIfOrganizationBelongsToArmy(soldierDTO.getMilitaryOrganization(), soldierDTO.getArmy(), validationErrors);
		validateIfRankBelongsToArmy(soldierDTO.getMilitaryRank(), soldierDTO.getArmy(), validationErrors);
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
	
	private void validateDuplicatedName(String name, Army army, CJM cjm, ValidationErrors validationErrors) {
		Optional<List<Soldier>> soldiers = soldierRepository.findByNameAndArmyAndCjm(name, army, cjm);
		if (soldiers.isPresent() && !soldiers.get().isEmpty())
			validationErrors.add(SOLDIER_NAME, SOLDIER_NAME_ALREADY_EXISTS);
	}
	
	private void validateDuplicatedEmail(String email, Army army, CJM cjm, ValidationErrors validationErrors) {
		Optional<List<Soldier>> soldiers = soldierRepository.findByEmailAndArmyAndCjm(email, army, cjm);
		if (soldiers.isPresent() && !soldiers.get().isEmpty())
			validationErrors.add(SOLDIER_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS);
	}
	
	private void validateIfOrganizationBelongsToArmy(MilitaryOrganization organization, Army army, ValidationErrors validationErrors) {
		Optional<List<MilitaryOrganization>> organizations = organizationRepository.findByArmy(army);
		if (organizations.isEmpty() || !organizations.get().contains(organization))
			throw new IllegalStateException(INCONSISTENT_DATA);
	}
	
	private void validateIfRankBelongsToArmy(MilitaryRank rank, Army army, ValidationErrors validationErrors) {
		List<MilitaryRank> ranks = militaryRankRepository.findAllByArmiesIn(army);
		if (ranks.isEmpty() || !ranks.contains(rank))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
	
	private void throwOnErrors(ValidationErrors validationErrors) {
		if (validationErrors.hasErrors())
            throw new SoldierValidationException(validationErrors);
	}
}
