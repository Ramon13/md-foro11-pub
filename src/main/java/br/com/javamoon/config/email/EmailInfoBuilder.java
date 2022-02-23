package br.com.javamoon.config.email;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.javamoon.config.email.model.EmailInfo;

@Component
public class EmailInfoBuilder {

	public EmailInfo createEmailInfo(String from, String email, String subject, String template, Map<String, Object> templateData) {
		return new EmailInfo(from, List.of(email), null, null, subject, template, templateData);
	}
}
