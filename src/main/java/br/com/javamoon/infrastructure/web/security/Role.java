package br.com.javamoon.infrastructure.web.security;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Role {

    private String name;
    
    public enum GroupRole{
        GROUP_USER,             //access to all basic functionalities   2^0
        EDIT_LIST_SCOPE,        //                                      2^1
        MANAGE_ACCOUNT_SCOPE    //                                      2^2
    }
    
    public enum CjmRole{
        CJM_USER
    }
    
    public static int calcPermissionLevel(List<String> selectedRoles) {
        int level = 0;
        for (int i = 0; i < GroupRole.values().length; i++) {
            if (selectedRoles.contains(GroupRole.values()[i].toString()))
                level += Math.pow(2, i);
        }
        
        return level;
    }
    
    public static class Roles{
        
        public static List<Role> groupRoles;
        public static List<Role> cjmRoles;
        
        static {
            groupRoles = new ArrayList<Role>();
            cjmRoles = new ArrayList<Role>();
            
            for (GroupRole role : GroupRole.values())
                groupRoles.add(new Role(role.toString()));
            
            for (CjmRole role : CjmRole.values())
                cjmRoles.add(new Role(role.toString()));
        }
    }
}
