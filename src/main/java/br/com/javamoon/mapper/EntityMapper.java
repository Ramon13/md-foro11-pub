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
    	soldier.setName(soldierDTO.getName() != null ? soldierDTO.getName().trim() : null);
    	soldier.setEmail(soldierDTO.getEmail() != null ? soldierDTO.getEmail().trim() : null);
    	soldier.setPhone(soldierDTO.getPhone() != null ? soldierDTO.getPhone().trim() : null);
    	soldier.setArmy(soldierDTO.getArmy());
    	soldier.setCjm(soldierDTO.getCjm());
    	soldier.setMilitaryOrganization(soldierDTO.getMilitaryOrganization());
    	soldier.setMilitaryRank(soldierDTO.getMilitaryRank());
    	
    	return soldier;
    }
    
    public static SoldierDTO fromEntityToDTO(Soldier soldier) {
    	SoldierDTO soldierDTO = new SoldierDTO();
    	soldierDTO.setId(soldier.getId());
    	soldierDTO.setName(soldier.getName());
    	soldierDTO.setEmail(soldier.getEmail());
    	soldierDTO.setPhone(soldier.getPhone());
    	soldierDTO.setArmy(soldier.getArmy());
    	soldierDTO.setCjm(soldier.getCjm());
    	soldierDTO.setMilitaryOrganization(soldier.getMilitaryOrganization());
    	soldierDTO.setMilitaryRank(soldier.getMilitaryRank());	
    	
    	return soldierDTO;
    }
}
