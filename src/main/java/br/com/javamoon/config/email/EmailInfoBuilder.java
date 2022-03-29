package br.com.javamoon.config.email;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.javamoon.config.email.model.EmailInfo;

@Component
public class EmailInfoBuilder {
	private final String DEFAULT_PASSWORD_SENDER = "no-reply@srvforo11.com";
	
	private final String FORGOT_PASSWORD_SUBJECT = "Recuperação de senha";
	private final String FORGOT_PASSWORD_HTML_TEMPLATE = "email/password-recovery";
	private final String GENERATED_PASSWORD_HTML_TEMPLATE = "email/password-generated";
	private final String FORGOT_PASSWORD_RECOVERY_ENDPOINT = "/credentials/forgot-password/new";
	
	private final String GENERATED_PASSWORD_SUBJECT = "Acesso cadastrado com sucesso!";
	
	@Value("${md-foro11.server.dns}")
	private String SERVER_DNS;
	
	@Value("${server.servlet.context-path}")
	private String CONTEXT_PATH;
	
	public EmailInfo createEmailInfo(String from, String email, String subject, String template, Map<String, Object> templateData) {
		return new EmailInfo(from, List.of(email), null, null, subject, template, templateData);
	}
	
	public EmailInfo getRedefinePasswordEmailInfo(String username, String email, String recoveryToken) {
		return createEmailInfo(
			DEFAULT_PASSWORD_SENDER,
			email,
			FORGOT_PASSWORD_SUBJECT,
			FORGOT_PASSWORD_HTML_TEMPLATE,
			Map.of("recoveryAddress", getRecoverAddress(username, recoveryToken))
		);
	}
	
	public EmailInfo getGeneratedPasswordEmailInfo(String generatedPassword, String emailTo) {
		return createEmailInfo(
			DEFAULT_PASSWORD_SENDER,
			emailTo,
			GENERATED_PASSWORD_SUBJECT,
			GENERATED_PASSWORD_HTML_TEMPLATE,
			Map.of("generatedPassword", generatedPassword, "appEndpoint", getBaseApplicationEndpoint())
		);
	}
	
	private String getRecoverAddress(String username, String recoveryToken){
		return String.format( "%s%s%s?recoveryToken=%s",
				SERVER_DNS, CONTEXT_PATH, FORGOT_PASSWORD_RECOVERY_ENDPOINT, recoveryToken);
	}
	
	private String getBaseApplicationEndpoint() {
		return String.format("%s/%s", SERVER_DNS, CONTEXT_PATH);
	}
}
