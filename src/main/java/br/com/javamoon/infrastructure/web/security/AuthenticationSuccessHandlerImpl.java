package br.com.javamoon.infrastructure.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import br.com.javamoon.util.SecurityUtils;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler{

	@Autowired
	private RecaptchaValidator recapchaValidator;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		if (!recapchaValidator.validate(request.getParameter("g-recaptcha-response")))
			authentication.setAuthenticated(false);
		Role role = SecurityUtils.loggedUser().getRole();
		
		if (role == Role.CJM_USER)
			response.sendRedirect("mngmt/home");
		else if (role == Role.GROUP_USER)
			response.sendRedirect("gp/dw/list");
		else
			throw new IllegalStateException("Authentication error");
		
		
	}

}
