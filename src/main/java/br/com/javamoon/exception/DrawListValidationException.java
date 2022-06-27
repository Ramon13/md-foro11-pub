package br.com.javamoon.exception;

import java.util.List;

import br.com.javamoon.validator.ValidationError;
import br.com.javamoon.validator.ValidationErrors;
import lombok.Getter;

@Getter
public class DrawListValidationException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private ValidationErrors validationErrors;
    
    public DrawListValidationException(ValidationErrors validationErrors) {
        super(validationErrors.toString());
        this.validationErrors = validationErrors;
    }
    
    public List<ValidationError> getErrorList() {
    	return validationErrors.getValidationErrors();
    }
}
