package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_EMAIL_MAX_LEN;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.SOLDIER_NAME_MAX_LEN;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.SoldierValidationException;
import br.com.javamoon.mapper.SoldierDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

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
		validateIfRankBelongsToArmy(soldierDTO.getMilitaryRank(), army, validationErrors);
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
	
	private void validateIfRankBelongsToArmy(MilitaryRank rank, Army army, ValidationErrors validationErrors) {
		List<MilitaryRank> ranks = militaryRankRepository.findAllByArmiesIn(army);
		if (ranks.isEmpty() || !ranks.contains(rank))
			throw new IllegalStateException(INCONSISTENT_DATA);		
	}
}
