package br.com.javamoon.exception;

import br.com.javamoon.validator.ValidationError;
import br.com.javamoon.validator.ValidationErrors;
import java.util.List;
import lombok.Getter;

@Getter
public class SoldierValidationException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private ValidationErrors validationErrors;
    
    public SoldierValidationException(ValidationErrors validationErrors) {
        super(validationErrors.toString());
        this.validationErrors = validationErrors;
    }
    
    public List<ValidationError> getErrorList() {
    	return validationErrors.getValidationErrors();
    }
}
