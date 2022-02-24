package br.com.javamoon.config;

import br.com.javamoon.domain.VerifyPasswordRecoveryTokenFilter;
import br.com.javamoon.service.UserAccountService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

	private UserAccountService userAccountService;

	public FilterConfiguration(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@Bean
	public FilterRegistrationBean<VerifyPasswordRecoveryTokenFilter> verifyPasswordRecoveryToken(){
		FilterRegistrationBean<VerifyPasswordRecoveryTokenFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		
		filterRegistrationBean.setFilter(new VerifyPasswordRecoveryTokenFilter(userAccountService));
		filterRegistrationBean.addUrlPatterns("/sorteio/credentials/*");
		return filterRegistrationBean;
	}
}
