package br.com.javamoon.infrastructure.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"dev", "test"})
@Component
public class RecaptchaValidatorDev implements RecaptchaValidator{

	@Value("${google.recaptchav3.verify.endpoint}")
	private String endpoint;
	
	@Value("${google.recaptchav3.verify.secret}")
	private String secret;
	
	@Override
	public boolean validate(String gRecapchaResponse) {
		System.out.println("INFO: recaptcha validation dev");
		return true;
	}

}
