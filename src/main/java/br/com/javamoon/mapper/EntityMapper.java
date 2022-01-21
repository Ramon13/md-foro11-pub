package br.com.javamoon.mapper;

import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.entity.DrawList;
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
    	soldier.setId(soldierDTO.getId());
    	soldier.setName(soldierDTO.getName() != null ? soldierDTO.getName().trim() : null);
    	soldier.setEmail(soldierDTO.getEmail() != null ? soldierDTO.getEmail().trim() : null);
    	soldier.setPhone(soldierDTO.getPhone() != null ? soldierDTO.getPhone().trim() : null);
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
    	soldierDTO.setActive(soldier.getActive());
    	soldierDTO.setMilitaryOrganization(soldier.getMilitaryOrganization());
    	soldierDTO.setMilitaryRank(soldier.getMilitaryRank());	
    	
    	return soldierDTO;
    }
    
    public static DrawExclusionDTO fromEntityToDTO(DrawExclusion drawExclusion) {
    	DrawExclusionDTO drawExclusionDTO = new DrawExclusionDTO();
    	drawExclusionDTO.setId(drawExclusion.getId());
    	drawExclusionDTO.setStartDate(drawExclusion.getStartDate());
    	drawExclusionDTO.setEndDate(drawExclusion.getEndDate());
    	drawExclusionDTO.setMessage(drawExclusion.getMessage());
    	drawExclusionDTO.setSoldier(drawExclusion.getSoldier());
    	drawExclusionDTO.setGroupUser(drawExclusion.getGroupUser());
    	return drawExclusionDTO;
    }
    
    public static DrawExclusion fromDTOToEntity(DrawExclusionDTO drawExclusionDTO) {
    	DrawExclusion drawExclusion = new DrawExclusion();
    	drawExclusion.setMessage(drawExclusionDTO.getMessage());
    	drawExclusion.setStartDate(drawExclusionDTO.getStartDate());
    	drawExclusion.setEndDate(drawExclusionDTO.getEndDate());
    	drawExclusion.setSoldier(drawExclusionDTO.getSoldier());
    	drawExclusion.setGroupUser(drawExclusionDTO.getGroupUser());
    	return drawExclusion;
    }
    
    public static DrawListDTO fromEntityToDTO(DrawList drawList) {
    	DrawListDTO drawListDTO = new DrawListDTO();
    	drawListDTO.setId(drawList.getId());
    	drawListDTO.setDescription(drawList.getDescription());
    	drawListDTO.setQuarterYear(drawList.getQuarterYear());
    	drawListDTO.setCreationDate(drawList.getCreationDate());
    	drawListDTO.setUpdateDate(drawList.getUpdateDate());
    	drawListDTO.setEnableForDraw(drawList.getEnableForDraw());
    	return drawListDTO;
    }
    
    public static DrawList fromDTOToEntity(DrawListDTO drawListDTO) {
    	DrawList drawList = new DrawList();
    	drawList.setDescription(drawListDTO.getDescription());
    	drawList.setQuarterYear(drawListDTO.getQuarterYear());
    	drawList.setEnableForDraw(drawListDTO.getEnableForDraw());
    	return drawList;
    }
}
