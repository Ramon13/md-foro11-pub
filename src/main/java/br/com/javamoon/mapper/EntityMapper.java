package br.com.javamoon.mapper;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.entity.Soldier;
import br.com.javamoon.domain.entity.User;
import br.com.javamoon.infrastructure.web.security.Role;
import java.util.stream.Collectors;

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
    
    public static UserDTO fromEntityToDTO(User user) {
    	UserDTO userDTO = new UserDTO();
    	userDTO.setUsername(user.getUsername());
    	userDTO.setEmail(user.getEmail());
    	userDTO.setRecoveryToken(user.getRecoveryToken());
    	return userDTO;
    }
    
    public static CJMUser fromDTOToEntity(CJMUserDTO userDTO) {
    	CJMUser user = new CJMUser();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setAuditorship(userDTO.getAuditorship());
        return user;
    }
    
    public static GroupUserDTO fromEntityToDTO(GroupUser user) {
        GroupUserDTO userDTO = new GroupUserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setCjm(user.getCjm());
        userDTO.setArmy(user.getArmy());
        
        Role.setGroupPermissionRoles(user);
        userDTO.setUserRoles(user.getPermissionRoles());
        
        return userDTO;
    }
    
    public static CJMUserDTO fromEntityToDTO(CJMUser user) {
        CJMUserDTO userDTO = new CJMUserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setAuditorship(user.getAuditorship());        
        return userDTO;
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
    	soldierDTO.setArmy(soldier.getArmy());
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
    	drawListDTO.setYearQuarter(drawList.getYearQuarter());
    	drawListDTO.setCreationDate(drawList.getCreationDate());
    	drawListDTO.setUpdateDate(drawList.getUpdateDate());
    	drawListDTO.setEnableForDraw(drawList.getEnableForDraw());
    	drawListDTO.setArmy(drawList.getArmy());
    	drawListDTO.setCreationUser(drawList.getCreationUser());
    	return drawListDTO;
    }
    
    public static DrawList fromDTOToEntity(DrawListDTO drawListDTO) {
    	DrawList drawList = new DrawList();
    	drawList.setId(drawListDTO.getId());
    	drawList.setDescription(drawListDTO.getDescription());
    	drawList.setYearQuarter(drawListDTO.getYearQuarter());
    	drawList.setEnableForDraw(drawListDTO.getEnableForDraw());
    	return drawList;
    }
    
    public static DrawDTO fromEntityToDTO(Draw draw) {
    	DrawDTO drawDTO = new DrawDTO();
    	drawDTO.setId(draw.getId());
    	drawDTO.setCreationDate(draw.getCreationDate());
    	drawDTO.setUpdateDate(draw.getUpdateDate());
    	drawDTO.setFinished(draw.getFinished());
    	drawDTO.setProcessNumber(draw.getProcessNumber());
    	drawDTO.setArmy(draw.getArmy());
    	drawDTO.setJusticeCouncil(draw.getJusticeCouncil());
    	drawDTO.setSelectedDrawList(draw.getDrawList().getId());
    	drawDTO.setSelectedYearQuarter(draw.getDrawList().getYearQuarter());
    	drawDTO.setSoldiers(draw.getSoldiers().stream().map(s -> fromEntityToDTO(s)).collect(Collectors.toList()));
    	drawDTO.setSelectedRanks(draw.getSoldiers().stream().map(s -> s.getMilitaryRank().getId()).collect(Collectors.toList()));
    	drawDTO.setSubstitute(draw.getSubstitute());
    	drawDTO.getDrawnSoldiers().addAll(draw.getSoldiers().stream().map(s -> s.getId()).collect(Collectors.toList()));
    	return drawDTO;
    }
    
    public static Draw fromDTOToEntity(DrawDTO drawDTO) {
    	Draw draw = new Draw();
    	draw.setId(drawDTO.getId());
    	draw.setArmy(drawDTO.getArmy());
    	draw.setJusticeCouncil(drawDTO.getJusticeCouncil());
    	draw.setSoldiers(drawDTO.getSoldiers().stream().map(s -> fromDTOToEntity(s)).collect(Collectors.toList()));
    	draw.setProcessNumber(drawDTO.getProcessNumber());
    	return draw;
    }
    
    public static MilitaryRankDTO fromEntityToDTO(MilitaryRank rank) {
    	return new MilitaryRankDTO(rank.getId(), rank.getName());
    }
}
