package br.com.javamoon.exception;

import br.com.javamoon.validator.ValidationErrors;
import lombok.Getter;

@Getter
public class AccountValidationException extends Exception{

    private static final long serialVersionUID = 1L;
    
    private ValidationErrors validationErrors;
    
    public AccountValidationException(ValidationErrors validationErrors) {
        super(validationErrors.toString());
        this.validationErrors = validationErrors;
    }
}
