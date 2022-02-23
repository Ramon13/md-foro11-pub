package br.com.javamoon.integration.service;

import br.com.javamoon.config.email.EmailSender;
import br.com.javamoon.config.email.model.EmailInfo;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SendEmailIntegrationTest {

	private static final String FROM = "no-reply@srvforo11.com";
	private static final String EMAIL_ADDRESS = "ramoncosta1209@gmail.com";
	private static final String SUBJECT = "Integration test subject";
	private static final String template = "email/template-test.html";
	
	@Autowired
	private EmailSender victim;
	
	@Test
	void testSendEmail() {
		EmailInfo emailInfo = new EmailInfo(
			FROM,
			List.of(EMAIL_ADDRESS),
			null,
			null,
			SUBJECT,
			template,
			Map.of("planet", "earth")
		);
		
		victim.send(emailInfo);
	}
}
