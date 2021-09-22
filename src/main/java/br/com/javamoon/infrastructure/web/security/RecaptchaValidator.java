package br.com.javamoon.infrastructure.web.security;

public interface RecaptchaValidator {

	public boolean validate(String gRecapchaResponse);
}
