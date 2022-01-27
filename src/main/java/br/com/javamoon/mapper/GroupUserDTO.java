package br.com.javamoon.mapper;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.infrastructure.web.security.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupUserDTO extends UserDTO{
    
    private Integer id;
    
    private List<RoleDTO> permissionRoles;		//Avaliable roles that user can select
    
    private List<String> userRoles; 
    
    private Army army;
    
    private CJM cjm;
    
    @Size(max = 2)
    private List<String> selectedRoles;			//Selected roles returned by view
    
    public GroupUserDTO() {
    	super();
    	
        permissionRoles = new ArrayList<RoleDTO>();
        selectedRoles = new ArrayList<String>();
        
        permissionRoles.add(new RoleDTO(Role.GroupRole.GROUP_EDIT_LIST_SCOPE));
        permissionRoles.add(new RoleDTO(Role.GroupRole.GROUP_MANAGE_ACCOUNT_SCOPE));
    }
    
    public String prettyPrintUsernameAndEmail() {
    	return String.format("%s<%s>", getUsername(), getEmail());
    }
    
    public String prettyPrintEnabledAccessScopes() {
    	if (!Objects.isNull(userRoles)) {
    		StringBuilder sb = new StringBuilder();
    		boolean first = true;
    		
    		for (String role : userRoles) {
    			if (!first)
    				sb.append(", ");
    			
    			sb.append(Role.GroupRole.valueOf(role).name);
    			first = false;
    		}
    		
    		return sb.toString();
    	}
    	
    	return "";
    }
}