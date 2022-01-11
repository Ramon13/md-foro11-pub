package br.com.javamoon.validator;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidationErrors {

    private List<ValidationError> validationErrors;
    
    public ValidationErrors() {
        validationErrors = new ArrayList<ValidationError>();
    }
    
    public ValidationErrors add(String fieldName, String errorMessage) {
        return add(new ValidationError(fieldName, errorMessage));
    }
    
    public ValidationErrors add(ValidationError validationError) {
        validationErrors.add(validationError);
        return this;
    }
    
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
    
    public int getNumberOfErrors() {
    	return validationErrors.size();
    }
    
    public ValidationError getError(int index) {
    	return validationErrors.get(index);
    }
}
