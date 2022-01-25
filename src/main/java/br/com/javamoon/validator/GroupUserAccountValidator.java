package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_PASSWORD;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.NO_PERMISSION;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.mapper.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class GroupUserAccountValidator {

    private GroupUserRepository groupUserRepository;
    
    public GroupUserAccountValidator(GroupUserRepository groupUserRepository) {
        this.groupUserRepository = groupUserRepository;
    }
    
    public void createAccountValidate(UserDTO userDTO){
        ValidationErrors validationErrors = new ValidationErrors();
        
        if (validatePassword(userDTO.getPassword(), validationErrors)) {
        	validateUsername(userDTO.getUsername(), validationErrors);
        	validateEmail(userDTO.getEmail(), validationErrors);
        }
        
        if (validationErrors.hasErrors())
            throw new AccountValidationException(validationErrors); 
    }
    
    public void deleteAccountValidate(Integer accountID, Army army, CJM cjm) {
    	GroupUser userDB = groupUserRepository.findById(accountID).orElseThrow();
    	if (!userDB.getArmy().equals(army) || !userDB.getCjm().equals(cjm))
    		throw new IllegalStateException(NO_PERMISSION);
    }
    
    public void editPasswordValidate(String password) throws AccountValidationException {
        ValidationErrors validationErrors = new ValidationErrors();
        
    	validatePassword(password, validationErrors);
    	
        if (validationErrors.hasErrors())
            throw new AccountValidationException(validationErrors);
    }
    
    private void validateUsername(String username, ValidationErrors validationErrors) {
        if (groupUserRepository.findActiveByUsername(username).isPresent())
            validationErrors.add(ACCOUNT_USERNAME, ACCOUNT_USERNAME_ALREADY_EXISTS);
    }
    
    private void validateEmail(String email, ValidationErrors validationErrors) {
        if (groupUserRepository.findByEmail(email).isPresent())
            validationErrors.add(ACCOUNT_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS);
    }
    
    private boolean validatePassword(String password, ValidationErrors validationErrors) {
        return (
            ValidationUtils.validateIfHasAnyUpperCase(password, ACCOUNT_PASSWORD, validationErrors) &&
            ValidationUtils.validateIfHasAnyLowerCase(password, ACCOUNT_PASSWORD, validationErrors) &&
            ValidationUtils.validateIfHasAnyNumber(password, ACCOUNT_PASSWORD, validationErrors)
        );
    }
}
