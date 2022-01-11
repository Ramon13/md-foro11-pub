package br.com.javamoon.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.repository.UserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.user.User;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.infrastructure.web.security.Role.GroupRole;
import br.com.javamoon.mapper.GroupUserDTO;
import br.com.javamoon.mapper.GroupUserMapper;
import br.com.javamoon.validator.GroupUserAccountValidator;

@Service
public class UserAccountService {

    private GroupUserAccountValidator accountValidator;
    private GroupUserRepository groupUserRepository;
    private UserRepository userRepository;
    
    public UserAccountService(
    		GroupUserAccountValidator accountValidator, 
    		GroupUserRepository groupUserRepository,
    		UserRepository userRepository
		) {
    	
        this.accountValidator = accountValidator;
        this.groupUserRepository = groupUserRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public GroupUserDTO createGroupUserAccount(GroupUserDTO userDTO, Army army, CJM cjm) throws AccountValidationException {
        accountValidator.createAccountValidate(userDTO);
        
        userDTO.getSelectedRoles().add(GroupRole.GROUP_USER.toString());
        
        GroupUser user = GroupUserMapper.fromDTOToEntity(userDTO);
        user.setPermissionLevel(Role.calcPermissionLevel(userDTO.getSelectedRoles()));
        user.setArmy(army);
        user.setCjm(cjm);
        user.encryptPassword();
        
        groupUserRepository.save(user);
        return GroupUserMapper.fromEntityToDTO(user);
    }
    
    public List<GroupUser> listGroupUserAccounts(Army army, CJM cjm){
    	return groupUserRepository.findActiveByArmyAndCjm(army, cjm);
    }
    
    @Transactional
	public void editPassword(User loggedUser, String newPassword) throws AccountValidationException{
		accountValidator.editPasswordValidate(newPassword);
		
		loggedUser.setCredentialsExpired(!loggedUser.getCredentialsExpired());
		loggedUser.setPassword(newPassword);
		loggedUser.encryptPassword();
		userRepository.save(loggedUser);
	}
}
