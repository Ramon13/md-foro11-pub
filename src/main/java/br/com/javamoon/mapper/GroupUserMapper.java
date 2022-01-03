package br.com.javamoon.mapper;

import br.com.javamoon.domain.group_user.GroupUser;

public final class GroupUserMapper {

    public GroupUserMapper() {}
    
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
        
        return groupUserDTO;
    }
}
