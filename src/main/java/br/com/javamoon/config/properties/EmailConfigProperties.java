package br.com.javamoon.config.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "spring.mail")
@ConstructorBinding
public class EmailConfigProperties {

	public static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String ENABLE_STARTTLS = "mail.smtp.starttls.enable";
	
	private final String host;
	private final String username;
	private final String password;
	private final Map<String, String> properties;
	
	public EmailConfigProperties(String host, String username, String password, Map<String, String> properties) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.properties = properties;
	}

	public String getHost() {
		return host;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getProperty(String property) {
		return properties.get(property);
	}
}
