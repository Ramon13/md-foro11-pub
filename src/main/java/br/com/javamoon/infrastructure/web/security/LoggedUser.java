package br.com.javamoon.infrastructure.web.security;

import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class LoggedUser implements UserDetails{

	private User user;
	private List<Role> permissionRoles;
	private Collection<? extends GrantedAuthority> roles;
	
	public LoggedUser(User user) {
		this.user = user;
		
		if (this.user instanceof CJMUser)
		    setPermissionRoles(user.getPermissionLevel(), Role.Roles.cjmRoles, user);
		else if (this.user instanceof GroupUser) {
		    setPermissionRoles(user.getPermissionLevel(), Role.Roles.groupRoles, user);
		}
		else
			throw new IllegalStateException("The user type is invalid");
	}
	
	private void setPermissionRoles(Integer permissionLevel, List<Role> roles, User user){
	    List<SimpleGrantedAuthority> authRoles = new ArrayList<SimpleGrantedAuthority>();
	    permissionRoles = new ArrayList<Role>();
	    
	    String binString = StringUtils.reverse(Integer.toBinaryString(permissionLevel));
	    for (int i = 0; i < binString.length(); i++) {
	        if (binString.charAt(i) == '1') {
	            authRoles.add(new SimpleGrantedAuthority("ROLE_" + roles.get(i).getName()));
	            permissionRoles.add(roles.get(i));
	            user.getPermissionRoles().add(roles.get(i).getName());
	        }
	    }
	    
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
		return true;
	}
	
	public User getUser() {
		return user;
	}
	
	public List<Role> getPermissionRoles() {
        return permissionRoles;
    }
	
	public Role getMainRole() {
	    return permissionRoles.get(0);
	}
}
