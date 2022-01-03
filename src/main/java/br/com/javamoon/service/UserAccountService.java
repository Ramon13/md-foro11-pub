package br.com.javamoon.service;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.repository.UserRepository;
import br.com.javamoon.domain.user.User;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.infrastructure.web.security.Role.GroupRole;
import br.com.javamoon.mapper.GroupUserDTO;
import br.com.javamoon.mapper.GroupUserMapper;
import br.com.javamoon.validator.UserAccountValidator;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    private UserAccountValidator accountValidator;
    private GroupUserRepository groupUserRepository;
    private UserRepository userRepository;
    
    public UserAccountService(
    		UserAccountValidator accountValidator, 
    		GroupUserRepository groupUserRepository,
    		UserRepository userRepository) {
        this.accountValidator = accountValidator;
        this.groupUserRepository = groupUserRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public GroupUserDTO createGroupUserAccount(GroupUserDTO userDTO) throws AccountValidationException {
        accountValidator.createAccountValidate(userDTO);
        
        userDTO.getSelectedRoles().add(GroupRole.GROUP_USER.toString());
        
        GroupUser user = GroupUserMapper.fromDTOToEntity(userDTO);
        user.setPermissionLevel(Role.calcPermissionLevel(userDTO.getSelectedRoles()));
        user.encryptPassword();
        
        user = groupUserRepository.save(user);
        
        return GroupUserMapper.fromEntityToDTO(user);
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
