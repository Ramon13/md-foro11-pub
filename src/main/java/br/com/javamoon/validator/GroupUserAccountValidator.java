package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME_ALREADY_EXISTS;
import br.com.javamoon.domain.group_user.GroupUserRepository;
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
    
    public void editPasswordValidate(String password) throws AccountValidationException {
        ValidationErrors validationErrors = new ValidationErrors();
        
    	validatePassword(password, validationErrors);
    	
        if (validationErrors.hasErrors())
            throw new AccountValidationException(validationErrors);
    }
    
    private void validateUsername(String username, ValidationErrors validationErrors) {
        if (groupUserRepository.findByUsername(username).isPresent())
            validationErrors.add(ACCOUNT_USERNAME, ACCOUNT_USERNAME_ALREADY_EXISTS);
    }
    
    private void validateEmail(String email, ValidationErrors validationErrors) {
        if (groupUserRepository.findByEmail(email).isPresent())
            validationErrors.add(ValidationConstants.ACCOUNT_EMAIL, ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS);
    }
    
    private boolean validatePassword(String password, ValidationErrors validationErrors) {
        return (
            ValidationUtils.validateIfHasAnyUpperCase(password, ValidationConstants.ACCOUNT_PASSWORD, validationErrors) &&
            ValidationUtils.validateIfHasAnyLowerCase(password, ValidationConstants.ACCOUNT_PASSWORD, validationErrors) &&
            ValidationUtils.validateIfHasAnyNumber(password, ValidationConstants.ACCOUNT_PASSWORD, validationErrors)
        );
    }
}
