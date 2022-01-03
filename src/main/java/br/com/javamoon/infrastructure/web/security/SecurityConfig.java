package br.com.javamoon.infrastructure.web.security;

import static br.com.javamoon.infrastructure.web.security.Role.CjmRole.CJM_USER;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_USER;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.MANAGE_ACCOUNT_SCOPE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    
	@Bean
	public AuthenticationSuccessHandler authSuccessHandler() {
		return new AuthenticationSuccessHandlerImpl();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/images/**", "/styles/**", "/scripts/**", "/public/**").permitAll()
			//
			//low level access
			.antMatchers("/gp/dw/list").hasRole(GROUP_USER.toString())
			.antMatchers("/gp/dw/list/{listid:\\d+}").hasRole(GROUP_USER.toString())
			.antMatchers("/gp/cjm/**").hasRole(GROUP_USER.toString())
			//
			//edit and creates lists
			.antMatchers("/gp/dw/**").hasRole(EDIT_LIST_SCOPE.toString())
			.antMatchers("/gp/sd/**").hasRole(EDIT_LIST_SCOPE.toString())
			//
			//account management
			.antMatchers("/gp/account/**").hasRole(MANAGE_ACCOUNT_SCOPE.toString())
			.antMatchers("/mngmt/**").hasRole(CJM_USER.toString())
			.antMatchers("/lu/**").hasAnyRole(GROUP_USER.toString(), CJM_USER.toString())
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.failureUrl("/login-error")
				.successHandler(authSuccessHandler())
				.permitAll()
			.and()
				.logout()
				.logoutUrl("/logout")
				.permitAll();
	}
}
