package br.com.javamoon.report.validator;

import org.springframework.stereotype.Component;

import br.com.javamoon.report.model.AbstractReportData;
import br.com.javamoon.validator.ValidationErrors;

@Component
public class DrawListReportValidator extends AbstractReportValidator {

	@Override
	protected void validate(AbstractReportData reportData, ValidationErrors validationErrors) {}

}
