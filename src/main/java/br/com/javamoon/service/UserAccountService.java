package br.com.javamoon.service;

import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_USER;
import br.com.javamoon.config.email.EmailInfoBuilder;
import br.com.javamoon.config.email.EmailSender;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.repository.UserRepository;
import br.com.javamoon.exception.AccountNotFoundException;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.exception.EmailNotFoundException;
import br.com.javamoon.infrastructure.web.security.Role;
import br.com.javamoon.mapper.CJMUserDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.GroupUserDTO;
import br.com.javamoon.validator.UserAccountValidator;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

	public static final Integer DEFAULT_CJM_USER_PERMISSION_LEVEL = 1;
	private static final Integer GENERATED_PASSWORD_LENGTH = 10;
	private final List<String> defaultRoles = List.of( GROUP_USER.toString(), GROUP_EDIT_LIST_SCOPE.toString());
	
    private UserAccountValidator userAccountValidator;
    private GroupUserRepository groupUserRepository;
    private CJMUserRepository cjmUserRepository;
    private UserRepository userRepository;
    private EmailSender emailSender;
    private EmailInfoBuilder emailInfoBuilder;
    
	public UserAccountService(UserAccountValidator userAccountValidator, GroupUserRepository groupUserRepository,
			CJMUserRepository cjmUserRepository, UserRepository userRepository, EmailSender emailSender,
			EmailInfoBuilder emailInfoBuilder) {
		this.userAccountValidator = userAccountValidator;
		this.groupUserRepository = groupUserRepository;
		this.cjmUserRepository = cjmUserRepository;
		this.userRepository = userRepository;
		this.emailSender = emailSender;
		this.emailInfoBuilder = emailInfoBuilder;
	}

	@Transactional
    public GroupUserDTO createGroupUserAccount(GroupUserDTO userDTO, Army army, CJM cjm) throws AccountValidationException {
        userAccountValidator.validateCreateGroupUserAccount(userDTO);
        String randomPass = RandomStringUtils.random(GENERATED_PASSWORD_LENGTH, true, true);
        
        GroupUser user = EntityMapper.fromDTOToEntity(userDTO);
        user.setPermissionLevel(Role.calcPermissionLevel(defaultRoles));
        user.setArmy(army);
        user.setCjm(cjm);
        user.setPassword(randomPass);
        user.encryptPassword();
        
        userRepository.save(user);
        emailSender.send( emailInfoBuilder.getGeneratedPasswordEmailInfo(randomPass, userDTO.getEmail()) );
        
        return EntityMapper.fromEntityToDTO(user);
    }
    
	@Transactional
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
    
    public List<CJMUser> listCjmUserAccounts(Auditorship auditorship){
    	return cjmUserRepository.findActiveByAuditorship(auditorship.getId()).orElseThrow(); 
    }
    
    @Transactional
    public void deleteGroupUserAccount(Integer accountID, Army army, CJM cjm) {
    	if (groupUserRepository.findById(accountID).isEmpty())
    		throw new AccountNotFoundException("account not found: " + accountID);
    	
    	userAccountValidator.validateDeleteGroupAccount(accountID, army, cjm);
    	
    	groupUserRepository.delete(accountID);
    }
    
    @Transactional
    public void deleteCjmUserAccount(Integer accountID, Auditorship auditorship) {
    	if (cjmUserRepository.findActiveByIdAndAuditorship(accountID, auditorship.getId()).isEmpty())
    		throw new AccountNotFoundException("account not found: " + accountID);
    	
    	cjmUserRepository.disableAccount(accountID);
    }
    
    @Transactional
	public void editPassword(User user, String newPassword) throws AccountValidationException{
		userAccountValidator.editPasswordValidate(newPassword);
		
		user.setCredentialsExpired(!user.getCredentialsExpired());
		user.setPassword(newPassword);
		
		user.encryptPassword();
		userRepository.save(user);
	}
    
    @Transactional
	public void editPassword(String recoveryToken, String newPassword) throws AccountValidationException{
    	User user = findUserByRecoveryToken(recoveryToken).orElseThrow();
		userAccountValidator.editPasswordValidate(newPassword);
		
		user.setPassword(newPassword);
		user.setRecoveryToken(null);
		user.encryptPassword();
		userRepository.save(user);
	}
    
    @Transactional
    public void sendRecoveryEmail(String email) throws EmailNotFoundException{
		User user = findByEmailOrElseThrow(email);
		String recoveryToken = RandomStringUtils.randomAlphanumeric(32);
		
		user.setRecoveryToken(recoveryToken);
		
		emailSender.send( emailInfoBuilder.getRedefinePasswordEmailInfo(user.getUsername(), email, recoveryToken) );
    }
    
    private User findByEmailOrElseThrow(String email) throws EmailNotFoundException{
    	Optional<User> user = groupUserRepository.findActiveByEmail(email);
    	if (user.isPresent())
    		return user.get();
    	
    	return cjmUserRepository.findActiveByEmail(email)
    			.orElseThrow(() -> new EmailNotFoundException("E-mail not found: " + email));
    }
    
    public Optional<User> findUserByRecoveryToken(String recoveryToken){
		Optional<User> user = groupUserRepository.findActiveByRecoveryToken(recoveryToken);
		
		if (user.isPresent())
			return user;
		
		return cjmUserRepository.findActiveByRecoveryToken(recoveryToken);
	}
}
