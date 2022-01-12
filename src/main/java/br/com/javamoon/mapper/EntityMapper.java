package br.com.javamoon.mapper;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.infrastructure.web.security.Role;

public final class EntityMapper {

    public EntityMapper() {}
    
    public static GroupUser fromDTOToEntity(GroupUserDTO userDTO) {
        GroupUser user = new GroupUser();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setArmy(userDTO.getArmy());
        user.setCjm(userDTO.getCjm());
        return user;
    }
    
    public static GroupUserDTO fromEntityToDTO(GroupUser groupUser) {
        GroupUserDTO groupUserDTO = new GroupUserDTO();
        groupUserDTO.setId(groupUser.getId());
        groupUserDTO.setUsername(groupUser.getUsername());
        groupUserDTO.setEmail(groupUser.getEmail());
        groupUserDTO.setUsername(groupUser.getUsername());
        groupUserDTO.setCjm(groupUser.getCjm());
        groupUserDTO.setArmy(groupUser.getArmy());
        
        Role.setGroupPermissionRoles(groupUser);
        groupUserDTO.setUserRoles(groupUser.getPermissionRoles());
        
        return groupUserDTO;
    }
    
    public static Soldier fromDTOToEntity(SoldierDTO soldierDTO) {
    	Soldier soldier = new Soldier();
    	soldier.setName(soldierDTO.getName());
    	soldier.setEmail(soldierDTO.getEmail());
    	soldier.setPhone(soldierDTO.getPhone());
    	soldier.setMilitaryOrganization(soldierDTO.getMilitaryOrganization());
    	soldier.setMilitaryRank(soldierDTO.getMilitaryRank());
    	
    	return soldier;
    }
}
