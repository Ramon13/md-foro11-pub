package br.com.javamoon.validator;

import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.mapper.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserAccountValidator {

    private GroupUserRepository groupUserRepository;
    
    public UserAccountValidator(GroupUserRepository groupUserRepository) {
        this.groupUserRepository = groupUserRepository;
    }
    
    public void createAccountValidate(UserDTO userDTO) throws AccountValidationException {
        ValidationErrors validationErrors = new ValidationErrors();
        
        validatePassword(userDTO.getPassword(), validationErrors);
        validateUsername(userDTO.getUsername(), validationErrors);
        validateEmail(userDTO.getEmail(), validationErrors);        
        
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
        if (groupUserRepository.findByUsernameAndCredentialsExpired(username, true).isPresent())
            validationErrors.add(ValidationConstants.ACCOUNT_USERNAME, ValidationConstants.ACCOUNT_USERNAME_ALREADY_EXISTS);
    }
    
    private void validateEmail(String email, ValidationErrors validationErrors) {
        if (groupUserRepository.findByEmailAndCredentialsExpired(email, true).isPresent())
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
