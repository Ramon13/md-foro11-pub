package br.com.javamoon.infrastructure.web.security;

import static br.com.javamoon.infrastructure.web.security.SecurityConstants.CORE_LIST_SCOPE_DESCRIPTION;
import static br.com.javamoon.infrastructure.web.security.SecurityConstants.EDIT_LIST_SCOPE_DESCRIPTION;
import static br.com.javamoon.infrastructure.web.security.SecurityConstants.MANAGE_ACCOUNT_SCOPE_DESCRIPTION;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.com.javamoon.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Role {

    public enum GroupRole{
    	GROUP_USER (SecurityConstants.CORE_SCOPE, CORE_LIST_SCOPE_DESCRIPTION),             				// 2^0
    	EDIT_LIST_SCOPE (SecurityConstants.EDIT_LIST_SCOPE, EDIT_LIST_SCOPE_DESCRIPTION),        			// 2^1
        MANAGE_ACCOUNT_SCOPE(SecurityConstants.MANAGE_ACCOUNT_SCOPE, MANAGE_ACCOUNT_SCOPE_DESCRIPTION);   // 2^2
        
    	public final String name;
        public final String description;
    	
        private GroupRole(String name, String description) {
        	this.name = name;
        	this.description = description;
        }
    }
    
    public enum CjmRole{
        CJM_USER
    }
    
    public static void setGroupPermissionRoles(User user){
	    String binString = StringUtils.reverse(Integer.toBinaryString(user.getPermissionLevel()));
	    for (int i = 0; i < binString.length(); i++)
	        if (binString.charAt(i) == '1')
	            user.getPermissionRoles().add(GroupRole.values()[i].toString());
	}
    
    public static void setCjmPermissionRoles(User user){
	    String binString = StringUtils.reverse(Integer.toBinaryString(user.getPermissionLevel()));
	    for (int i = 0; i < binString.length(); i++)
	        if (binString.charAt(i) == '1')
	            user.getPermissionRoles().add(CjmRole.values()[i].toString());
	}
    
    public static int calcPermissionLevel(List<String> selectedRoles) {
        int level = 0;
        for (int i = 0; i < GroupRole.values().length; i++) {
            if (selectedRoles.contains(GroupRole.values()[i].toString()))
                level += Math.pow(2, i);
        }
        
        return level;
    }
}
