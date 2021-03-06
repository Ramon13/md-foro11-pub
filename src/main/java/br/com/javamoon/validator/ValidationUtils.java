package br.com.javamoon.validator;

import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_PASSWORD;
import br.com.javamoon.util.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import org.springframework.validation.Errors;

public final class ValidationUtils {

    private ValidationUtils() {}
    
    public static <T extends RuntimeException> void throwOnErrors(Class<T> clazz, ValidationErrors validationErrors){
    	try {
			Constructor<T> constructor = clazz.getConstructor(ValidationErrors.class);
			if (validationErrors.hasErrors())
				throw constructor.newInstance(validationErrors);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
		        | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
    
    public static void rejectValues(Errors errors, ValidationErrors validationErrors) {
        for (ValidationError validationError : validationErrors.getValidationErrors()) {
            errors.rejectValue(validationError.getFieldName(), null, validationError.getErrorMessage());
        }
    }
    
    public static boolean validateRequired(String fieldValue, String fieldName, ValidationErrors validationErrors) {
    	if (StringUtils.isEmpty(fieldValue)) {
    		validationErrors.add(fieldName, ValidationConstants.REQUIRED_FIELD);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validateRequired(Object fieldValue, String fieldName, ValidationErrors validationErrors) {
    	if (Objects.isNull(fieldValue)) {
    		validationErrors.add(fieldName, ValidationConstants.REQUIRED_FIELD);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validateIfHasAnyUpperCase(String fieldValue, String fieldName, ValidationErrors validationErrors) {
        if (!StringUtils.hasAnyUpperCase(fieldValue)) {
            validationErrors.add(fieldName, ValidationConstants.PASSWORD_DOES_NOT_HAVE_UPPERCASE);
            return false;
        }
        
        return true;
    }
    
    public static boolean validateIfHasAnyLowerCase(String fieldValue, String fieldName, ValidationErrors validationErrors) {
        if (!StringUtils.hasAnyLowerCase(fieldValue)) {
            validationErrors.add(fieldName, ValidationConstants.PASSWORD_DOES_NOT_HAVE_LOWERCASE);
            return false;
        }
        
        return true;
    }
    
    public static boolean validateIfHasAnyNumber(String fieldValue, String fieldName, ValidationErrors validationErrors) {
        if (!StringUtils.hasAnyNumber(fieldValue)) {
            validationErrors.add(fieldName, ValidationConstants.PASSWORD_DOES_NOT_HAVE_NUMBER);
            return false;
        }
        
        return true;
    }
    
    public static boolean validateMaxLength(String fieldValue, String fieldName, int max, ValidationErrors validationErrors) {
    	if (!Objects.isNull(fieldValue) && fieldValue.length() > max) {
    		validationErrors.add(fieldName, ValidationConstants.STRING_EXCEEDS_MAX_LEN);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validateMaxLength(Collection<Object> collection, String fieldName, int max, ValidationErrors validationErrors) {
    	if (!Objects.isNull(collection) && collection.size() > max) {
    		validationErrors.add(fieldName, ValidationConstants.STRING_EXCEEDS_MAX_LEN);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validateMinLength(String fieldValue, String fieldName, int min, ValidationErrors validationErrors) {
    	if (!Objects.isNull(fieldValue) && fieldValue.length() < min) {
    		validationErrors.add(fieldName, ValidationConstants.STRING_BELOW_MIN_LEN);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validateMinLength(Collection<Object> collection, String fieldName, int min, ValidationErrors validationErrors) {
    	if (!Objects.isNull(collection) && collection.size() < min) {
    		validationErrors.add(fieldName, ValidationConstants.STRING_BELOW_MIN_LEN);
    		return false;
    	}
    	
    	return true;
    }
    
    public static boolean validatePassword(String password, ValidationErrors validationErrors) {
        return (
            validateIfHasAnyUpperCase(password, ACCOUNT_PASSWORD, validationErrors) &&
            validateIfHasAnyLowerCase(password, ACCOUNT_PASSWORD, validationErrors) &&
            validateIfHasAnyNumber(password, ACCOUNT_PASSWORD, validationErrors)
        );
    }
}
