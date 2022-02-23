package br.com.javamoon.service;

import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_EDIT_LIST_SCOPE;
import static br.com.javamoon.infrastructure.web.security.Role.GroupRole.GROUP_USER;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

@Service
public class UserAccountService {

	public static final Integer DEFAULT_CJM_USER_PERMISSION_LEVEL = 1;
	private final List<String> defaultRoles = List.of( GROUP_USER.toString(), GROUP_EDIT_LIST_SCOPE.toString());
	private final String FORGOT_PASSWORD_SUBJECT = "Recuperação de senha";
	private final String FORGOT_PASSWORD_SENDER = "no-reply@srvforo11.com";
	private final String FORGOT_PASSWORD_HTML_TEMPLATE = "email/password-recovery";
	private final String FORGOT_PASSWORD_RECOVERY_ENDPOINT = "/public/forgot-password/new";
	
	@Value("${md-foro11.server.dns}")
	private String SERVER_DNS;
	
	@Value("${server.servlet.context-path}")
	private String CONTEXT_PATH;
	
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
        
        GroupUser user = EntityMapper.fromDTOToEntity(userDTO);
        user.setPermissionLevel(Role.calcPermissionLevel(defaultRoles));
        user.setArmy(army);
        user.setCjm(cjm);
        user.encryptPassword();
        
        userRepository.save(user);
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
	public void editPassword(User loggedUser, String newPassword) throws AccountValidationException{
		userAccountValidator.editPasswordValidate(newPassword);
		
		loggedUser.setCredentialsExpired(!loggedUser.getCredentialsExpired());
		loggedUser.setPassword(newPassword);
		loggedUser.encryptPassword();
		userRepository.save(loggedUser);
	}
    
    @Transactional
    public void sendRecoveryEmail(String email) throws EmailNotFoundException{
		User user = findByEmailOrElseThrow(email);
		String recoveryToken = RandomStringUtils.randomAlphanumeric(32);
		
		user.setRecoveryToken(recoveryToken);
		String recoverAddress = String.format( "%s%s%s?username=%s&recoveryToken=%s",
				SERVER_DNS, CONTEXT_PATH, FORGOT_PASSWORD_RECOVERY_ENDPOINT, user.getUsername(), recoveryToken);
		
		emailSender.send(
			emailInfoBuilder.createEmailInfo(
					FORGOT_PASSWORD_SENDER,
					email,
					FORGOT_PASSWORD_SUBJECT,
					FORGOT_PASSWORD_HTML_TEMPLATE,
					Map.of("recoveryAddress", recoverAddress)
				)
			);
    }
    
    private User findByEmailOrElseThrow(String email) throws EmailNotFoundException{
    	Optional<User> user = groupUserRepository.findActiveByEmail(email);
    	if (user.isPresent())
    		return user.get();
    	
    	return cjmUserRepository.findActiveByEmail(email)
    			.orElseThrow(() -> new EmailNotFoundException("E-mail not found: " + email));
    }
}
