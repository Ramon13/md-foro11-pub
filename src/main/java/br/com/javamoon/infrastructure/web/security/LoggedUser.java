package br.com.javamoon.infrastructure.web.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.user.User;
import br.com.javamoon.util.CollectionUtils;

@SuppressWarnings("serial")
public class LoggedUser implements UserDetails{

	private User user;
	private Role role;
	private Collection<? extends GrantedAuthority> roles;
	
	public LoggedUser(User user) {
		this.user = user;
		Role role;
		
		if (this.user instanceof CJMUser)
			role = Role.CJM_USER;
		else if (this.user instanceof GroupUser)
			role = Role.GROUP_USER;
		else
			throw new IllegalStateException("The user type is invalid");
		
		this.role = role;
		this.roles = CollectionUtils.listOf(new SimpleGrantedAuthority("ROLE_" + role));
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
		return true;
	}

	public Role getRole() {
		return role;
	}
	
	public User getUser() {
		return user;
	}
}
