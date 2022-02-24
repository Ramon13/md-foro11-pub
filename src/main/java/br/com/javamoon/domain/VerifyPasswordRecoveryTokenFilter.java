package br.com.javamoon.domain;

import br.com.javamoon.domain.entity.User;
import br.com.javamoon.service.UserAccountService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class VerifyPasswordRecoveryTokenFilter extends GenericFilterBean{

	private UserAccountService userAccountService;

	public VerifyPasswordRecoveryTokenFilter(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
	        throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		String recoveryToken = request.getParameter("recoveryToken");
		Optional<User> user = userAccountService.findUserByRecoveryToken(recoveryToken);
	
		if (user.isPresent())
			chain.doFilter(request, response);
	}
}
