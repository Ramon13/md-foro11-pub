package br.com.javamoon.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupUserDTO extends UserDTO{
    
    private Integer id;
    
    private List<RoleDTO> permissionRoles;		//Avaliable roles that user can select
    
    private Army army;
    
    private CJM cjm;
    
	private List<String> userRoles;
    
    public GroupUserDTO() {
    	super();
    	
        permissionRoles = new ArrayList<RoleDTO>();
        
        permissionRoles.add(new RoleDTO(Role.GroupRole.GROUP_EDIT_LIST_SCOPE));
        permissionRoles.add(new RoleDTO(Role.GroupRole.GROUP_MANAGE_ACCOUNT_SCOPE));
    }
    
    public String prettyPrintEnabledAccessScopes() {
    	if (!Objects.isNull(userRoles)) {
    		return StringUtils.concatenate(
    					",", 
    					userRoles.stream()
    						.map(r -> Role.GroupRole.valueOf(r).name)
    						.collect(Collectors.toList())
    				);
    	}
    	
    	return "";
    }
}