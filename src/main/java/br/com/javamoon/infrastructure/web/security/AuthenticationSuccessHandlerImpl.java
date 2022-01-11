package br.com.javamoon.infrastructure.web.security;

import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.MANAGE_ACCOUNT_SCOPE;
import br.com.javamoon.util.SecurityUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler{

	@Autowired
	private RecaptchaValidator recapchaValidator;
	
	@Value("${server.servlet.context-path}")
	private String applicationContext;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		if (!recapchaValidator.validate(request.getParameter("g-recaptcha-response")))
			authentication.setAuthenticated(false);
		
		HttpSession session = request.getSession();
		setSessionTimeOut(session, 30 * 60);
		
		LoggedUser loggedUser = SecurityUtils.loggedUser();
		
		if (!loggedUser.getUser().getCredentialsExpired())
			sendToHomePage(loggedUser, response, session);
		else
			sendToPasswordRedefinition(response);
	}
		
	private void sendToPasswordRedefinition(HttpServletResponse response) throws IOException {
		response.sendRedirect("account/password/reset");
	}
	
	public void sendToHomePage(LoggedUser loggedUser, HttpServletResponse response, HttpSession session) throws IOException {
		String mainRole = loggedUser.getMainRole();
		
		if (mainRole.equals(Role.CjmRole.CJM_USER.toString())) {
			response.sendRedirect(applicationContext + "/mngmt/dw-list/list");
			
		}else if (mainRole.equals(Role.GroupRole.GROUP_USER.toString())) {
		    setSessionScopes(session);
			response.sendRedirect(applicationContext + "/gp/dw/list");
		
		}else {
			throw new IllegalStateException("Authentication error");
		}
	}
	
	private void setSessionScopes(HttpSession session) {
	    List<String> roles = SecurityUtils.loggedUser().getUser().getPermissionRoles();
	    
	    if (roles.contains(MANAGE_ACCOUNT_SCOPE.toString()))
	        session.setAttribute("accountScope", true);
	    
	    if (roles.contains(EDIT_LIST_SCOPE.toString()))
            session.setAttribute("listScope", true);
	}

	private void setSessionTimeOut(HttpSession session, int seconds) {
		session.setMaxInactiveInterval(seconds);
	}
}
