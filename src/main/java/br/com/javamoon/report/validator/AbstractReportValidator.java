package br.com.javamoon.report.validator;

import br.com.javamoon.report.model.AbstractReportData;
import br.com.javamoon.validator.ValidationErrors;
import br.com.javamoon.validator.ValidationUtils;

public abstract class AbstractReportValidator {

	public <T extends RuntimeException> void validate(Class<T> clazz, AbstractReportData abstractReportData) {
		var validationErrors = new ValidationErrors();
		validate(abstractReportData, validationErrors);
		ValidationUtils.throwOnErrors(clazz, validationErrors);
	}
	
	protected abstract void validate(AbstractReportData reportData, ValidationErrors validationErrors);
}
