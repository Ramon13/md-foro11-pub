package br.com.javamoon.exception;

import br.com.javamoon.validator.ValidationErrors;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class BaseValidationException extends RuntimeException{
	
	private ValidationErrors validationErrors;

	public BaseValidationException(ValidationErrors validationErrors) {
		super(validationErrors.toString());
		this.validationErrors = validationErrors;
	}
	
	public BaseValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseValidationException(String message) {
		super(message);
	}

	public BaseValidationException(Throwable cause) {
		super(cause);
	}
}
