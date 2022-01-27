package br.com.javamoon.service;

import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_USER;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.UserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.exception.AccountNotFoundException;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.mapper.CJMUserDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.GroupUserDTO;
import br.com.javamoon.validator.UserAccountValidator;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

	public static final Integer DEFAULT_CJM_USER_PERMISSION_LEVEL = 1;
	
    private UserAccountValidator userAccountValidator;
    private GroupUserRepository groupUserRepository;
    private UserRepository userRepository;
    
	public UserAccountService(UserAccountValidator userAccountValidator, GroupUserRepository groupUserRepository,
		UserRepository userRepository) {
		this.userAccountValidator = userAccountValidator;
		this.groupUserRepository = groupUserRepository;
		this.userRepository = userRepository;
	}

	@Transactional
    public GroupUserDTO createGroupUserAccount(GroupUserDTO userDTO, Army army, CJM cjm) throws AccountValidationException {
        userAccountValidator.validateCreateGroupUserAccount(userDTO);
        
        userDTO.getSelectedRoles().add(GROUP_USER.toString());
        
        GroupUser user = EntityMapper.fromDTOToEntity(userDTO);
        user.setPermissionLevel(Role.calcPermissionLevel(userDTO.getSelectedRoles()));
        user.setArmy(army);
        user.setCjm(cjm);
        user.encryptPassword();
        
        userRepository.save(user);
        return EntityMapper.fromEntityToDTO(user);
    }
    
    public CJMUserDTO createCJMUserAccount(CJMUserDTO userDTO, Auditorship auditorship) throws AccountValidationException {
    	userAccountValidator.validateCreateCJMUserAccount(userDTO);
    	CJMUser user = EntityMapper.fromDTOToEntity(userDTO);
    	
    	user.setAuditorship(auditorship);
    	user.encryptPassword();
    	
    	userRepository.save(user);
    	return EntityMapper.fromEntityToDTO(user);
    }
    
    public List<GroupUser> listGroupUserAccounts(Army army, CJM cjm){
    	return groupUserRepository.findActiveByArmyAndCjm(army, cjm);
    }
    
    @Transactional
    public void deleteUserAccount(Integer accountID, Army army, CJM cjm) {
    	if (groupUserRepository.findById(accountID).isEmpty())
    		throw new AccountNotFoundException("account not found: " + accountID);
    	
    	userAccountValidator.validateDeleteGroupAccount(accountID, army, cjm);
    	
    	groupUserRepository.delete(accountID);
    }
    
    @Transactional
	public void editPassword(User loggedUser, String newPassword) throws AccountValidationException{
		userAccountValidator.editPasswordValidate(newPassword);
		
		loggedUser.setCredentialsExpired(!loggedUser.getCredentialsExpired());
		loggedUser.setPassword(newPassword);
		loggedUser.encryptPassword();
		userRepository.save(loggedUser);
	}
}
