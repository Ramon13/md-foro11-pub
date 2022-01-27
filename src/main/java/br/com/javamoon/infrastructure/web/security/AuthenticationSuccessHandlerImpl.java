package br.com.javamoon.infrastructure.web.security;

import static br.com.javamoon.infrastructure.web.security.Role.CjmRole.CJM_MANAGE_ACCOUNT_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_MANAGE_ACCOUNT_SCOPE;
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
	
	@Value("${md-foro11.endpoint.cjm.index}")
	private  String cjmIndexPage;
	
	@Value("${md-foro11.endpoint.group.index}")
	private String groupIndexPage;
	
	@Value("${md-foro11.endpoint.auth.reset.index}")
	private String resetPasswordIndexPage;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		if (!recapchaValidator.validate(request.getParameter("g-recaptcha-response"))) {
			authentication.setAuthenticated(false);
			return;
		}
		
		HttpSession session = request.getSession();
		setSessionTimeOut(session);
		
		LoggedUser loggedUser = SecurityUtils.loggedUser();
		
		if (!loggedUser.getUser().getCredentialsExpired())
			sendToHomePage(loggedUser, response, session);
		else
			sendToPasswordRedefinition(response);
	}
		
	private void sendToPasswordRedefinition(HttpServletResponse response) throws IOException {
		response.sendRedirect(applicationContext + resetPasswordIndexPage);
	}
	
	public void sendToHomePage(LoggedUser loggedUser, HttpServletResponse response, HttpSession session) throws IOException {
		setSessionScopes(session, loggedUser.getUser().getPermissionRoles());
		String mainRole = loggedUser.getMainRole();
		
		if (mainRole.equals(Role.CjmRole.CJM_USER.toString())) {
			response.sendRedirect(applicationContext + cjmIndexPage);
			
		}else if (mainRole.equals(Role.GroupRole.GROUP_USER.toString())) {
			response.sendRedirect(applicationContext + groupIndexPage);
		
		}else {
			throw new IllegalStateException("Authentication error");
		}
	}
	
	/**
	 * Session parameters used to display view components
	 */
	private void setSessionScopes(HttpSession session, List<String> roles) {
	    if (roles.contains(GROUP_MANAGE_ACCOUNT_SCOPE.toString())
	    		|| roles.contains(CJM_MANAGE_ACCOUNT_SCOPE.toString()))
	        session.setAttribute("accountScope", true);
	    
	    if (roles.contains(GROUP_EDIT_LIST_SCOPE.toString()))
            session.setAttribute("listScope", true);
	}

	private void setSessionTimeOut(HttpSession session) {
		session.setMaxInactiveInterval(30 * 60);
	}
}
