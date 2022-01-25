package br.com.javamoon.util;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.infrastructure.web.security.LoggedUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

	public static LoggedUser loggedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth instanceof AnonymousAuthenticationToken)
			return null;
		
		return (LoggedUser) auth.getPrincipal();
	}
	
	public static CJMUser cjmUser() {
		LoggedUser loggedUser = loggedUser();
		
		if (loggedUser == null)
			throw new IllegalStateException("there is no user logged in");
		if (!(loggedUser.getUser() instanceof CJMUser))
			throw new IllegalStateException("the logged user is not a CJM_USER");
		
		return (CJMUser) loggedUser.getUser();
	}
	
	public static GroupUser groupUser() {
		LoggedUser loggedUser = loggedUser();
		
		if (loggedUser == null)
			throw new IllegalStateException("there is no user logged in");
		if (!(loggedUser.getUser() instanceof GroupUser))
			throw new IllegalStateException("the logged user is not a GROUP_USER");
		
		return (GroupUser) loggedUser.getUser();
	}
}
