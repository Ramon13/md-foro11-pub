package br.com.javamoon.infrastructure.web.security;

import static br.com.javamoon.infrastructure.web.security.Role.CjmRole.CJM_MANAGE_ACCOUNT_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.CjmRole.CJM_USER;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_MANAGE_ACCOUNT_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_USER;
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
			// Group user core role
			.antMatchers(
				"/gp/dw/list",
				"/gp/dw/list/{listid:\\d+}",
				"/gp/sd/list/home",
				"/gp/cjm/**",
				"/gp/accounts/password/reset/**"
			).hasRole(GROUP_USER.toString())
			//
			// CJM user core role
			.antMatchers("/cjm/**").hasRole(CJM_USER.toString())
			//
			// Edit and creates lists
			.antMatchers(
				"/gp/dw/**",
				"/gp/sd/**"
			).hasRole(GROUP_EDIT_LIST_SCOPE.toString())
			//
			// Account management
			.antMatchers("/gp/accounts/**").hasRole(GROUP_MANAGE_ACCOUNT_SCOPE.toString())
			.antMatchers("/cjm/accounts/**").hasRole(CJM_MANAGE_ACCOUNT_SCOPE.toString())
			//
			//
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
