package br.com.javamoon.infrastructure.web.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecaptchaResult {

	private boolean success;
	private String challenge_ts;
	private String hostname;
	private Float score;
	private String action;
}
