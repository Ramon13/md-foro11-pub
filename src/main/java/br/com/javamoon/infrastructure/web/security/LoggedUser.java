package br.com.javamoon.infrastructure.web.security;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class LoggedUser implements UserDetails{

	private User user;
	private Collection<? extends GrantedAuthority> roles;
	
	public LoggedUser(User user) {
		this.user = user;
		
		if (this.user instanceof CJMUser)
			Role.setCjmPermissionRoles(user);
		else if (this.user instanceof GroupUser) {
			Role.setGroupPermissionRoles(user);
		}
		else
			throw new IllegalStateException("The user type is invalid");
		
		setAuthorityRoles(user.getPermissionRoles());
	}
	
	private void setAuthorityRoles(List<String> permissionRoles) {
		List<SimpleGrantedAuthority> authRoles = new ArrayList<SimpleGrantedAuthority>();
		for (String role : permissionRoles)
            authRoles.add(new SimpleGrantedAuthority("ROLE_" + role));
		
		this.roles = authRoles;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getActive();
	}
	
	public User getUser() {
		return user;
	}
	
	public String getMainRole() {
	    return user.getPermissionRoles().get(0);
	}
}
