package br.com.javamoon.infrastructure.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Profile("prod")
@Component
public class RecaptchaValidatorProd implements RecaptchaValidator{

	@Value("${google.recaptchav3.verify.endpoint}")
	private String endpoint;
	
	@Value("${google.recaptchav3.verify.secret}")
	private String secret;
	
	@Override
	public boolean validate(String gRecapchaResponse) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("secret", secret);
		map.add("response", gRecapchaResponse);
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);		
		RestTemplate restTemplate = new RestTemplate();
		String resp = restTemplate.postForObject(endpoint, entity, String.class);
		return new Gson().fromJson(resp, RecaptchaResult.class).isSuccess();
	}

}
