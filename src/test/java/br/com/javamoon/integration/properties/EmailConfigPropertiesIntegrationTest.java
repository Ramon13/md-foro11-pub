package br.com.javamoon.integration.properties;

import static br.com.javamoon.config.properties.EmailConfigProperties.ENABLE_STARTTLS;
import static br.com.javamoon.config.properties.EmailConfigProperties.SMTP_AUTH;
import static br.com.javamoon.config.properties.EmailConfigProperties.SMTP_PORT;
import static br.com.javamoon.config.properties.EmailConfigProperties.TRANSPORT_PROTOCOL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import br.com.javamoon.config.properties.EmailConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmailConfigPropertiesIntegrationTest {

	@Autowired
	private EmailConfigProperties victim;
	
	@Value("${spring.mail.host}")
	private String host;
	
	@Value("${spring.mail.username}")
	private String username;
	
	@Value("${spring.mail.password}")
	private String password;
	
	@Value("${spring.mail.properties.mail.transport.protocol}")
	private String protocol;
	
	@Value("${spring.mail.properties.mail.smtp.port}")
	private String smtpPort;
	
	@Value("${spring.mail.properties.mail.smtp.auth}")
	private String smtpAuth;
	
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String tlsEnabled;
	
	@Test
	void testProperties() {
		assertNotNull(host);
		assertEquals(host, victim.getHost());
		
		System.out.println(username);
		assertNotNull(username);
		assertEquals(username, victim.getUsername());
		
		assertNotNull(password);
		assertEquals(password, victim.getPassword());
		
		assertNotNull(protocol);
		assertEquals(protocol, victim.getProperty(TRANSPORT_PROTOCOL));
		
		assertNotNull(smtpPort);
		assertEquals(smtpPort, victim.getProperty(SMTP_PORT));
		
		assertNotNull(smtpAuth);
		assertEquals(smtpAuth, victim.getProperty(SMTP_AUTH));
		
		assertNotNull(tlsEnabled);
		assertEquals(tlsEnabled, victim.getProperty(ENABLE_STARTTLS));
	}
}
