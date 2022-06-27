package br.com.javamoon.config;

import static br.com.javamoon.config.properties.EmailConfigProperties.*;
import br.com.javamoon.config.properties.EmailConfigProperties;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

	private final EmailConfigProperties emailConfigProperties;
	
	public EmailConfiguration(EmailConfigProperties emailConfigProperties) {
		this.emailConfigProperties = emailConfigProperties;
	}
	
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailConfigProperties.getHost());
		mailSender.setUsername(emailConfigProperties.getUsername());
		mailSender.setPassword(emailConfigProperties.getPassword());
		
		Properties properties = mailSender.getJavaMailProperties();
		properties.put(TRANSPORT_PROTOCOL, emailConfigProperties.getProperty(TRANSPORT_PROTOCOL));
		properties.put(SMTP_PORT, emailConfigProperties.getProperty(SMTP_PORT));
		properties.put(SMTP_AUTH, emailConfigProperties.getProperty(SMTP_AUTH));
		properties.put(ENABLE_STARTTLS, emailConfigProperties.getProperty(ENABLE_STARTTLS));
		
		return mailSender;
	}
}
