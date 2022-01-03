package br.com.javamoon.mapper;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.infrastructure.web.security.Role.GroupRole;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupUserDTO extends UserDTO{
    
    private Integer id;
    
    private List<RoleDTO> permissionRoles;
    
    private Army army;
    
    private CJM cjm;
    
    @Size(max = 2)
    private List<String> selectedRoles;
    
    public GroupUserDTO() {
        permissionRoles = new ArrayList<RoleDTO>();
        selectedRoles = new ArrayList<String>();
        
        permissionRoles.add(
                new RoleDTO(
                        "Listas",
                        MapperConstants.EDIT_LIST_SCOPE_DESCRIPTION,
                        GroupRole.EDIT_LIST_SCOPE.toString()));
        
        permissionRoles.add(
                new RoleDTO(
                        "Contas", 
                        MapperConstants.MANAGE_ACCOUNT_SCOPE_DESCRIPTION,
                        GroupRole.MANAGE_ACCOUNT_SCOPE.toString()));
    }
}