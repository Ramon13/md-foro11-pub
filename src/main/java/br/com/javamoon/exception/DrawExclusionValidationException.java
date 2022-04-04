package br.com.javamoon.exception;

import br.com.javamoon.validator.ValidationError;
import br.com.javamoon.validator.ValidationErrors;
import java.util.List;
import lombok.Getter;

@Getter
public class DrawExclusionValidationException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private ValidationErrors validationErrors;
    
    public DrawExclusionValidationException(ValidationErrors validationErrors) {
        super(validationErrors.toString());
        this.validationErrors = validationErrors;
    }
    
    public List<ValidationError> getErrorList() {
    	return validationErrors.getValidationErrors();
    }
}
