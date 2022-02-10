package br.com.javamoon.exception;

import br.com.javamoon.validator.ValidationErrors;
import lombok.Getter;

@Getter
public class DrawValidationException extends BaseValidationException{

    private static final long serialVersionUID = 1L;
        
    public DrawValidationException(ValidationErrors validationErrors) {
        super(validationErrors);
    }
}
