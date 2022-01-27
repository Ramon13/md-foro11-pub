package br.com.javamoon.unit.service;

import static br.com.javamoon.util.TestDataCreator.newAuditorship;
import static br.com.javamoon.util.TestDataCreator.newCjm;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_EMAIL_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_PASSWORD;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME;
import static br.com.javamoon.validator.ValidationConstants.ACCOUNT_USERNAME_ALREADY_EXISTS;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_LOWERCASE;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_NUMBER;
import static br.com.javamoon.validator.ValidationConstants.PASSWORD_DOES_NOT_HAVE_UPPERCASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.AuditorshipRepository;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.exception.AccountNotFoundException;
import br.com.javamoon.exception.AccountValidationException;
import br.com.javamoon.mapper.CJMUserDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationConstants;
import br.com.javamoon.validator.ValidationError;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserAccountServiceUnitTest {
	
	@Autowired
	private UserAccountService victim;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private CJMRepository cjmRepository;
	
	@Autowired
	private AuditorshipRepository auditorshipRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@Test
	void testCreateCJMAccountSuccessfully() {
		CJMUserDTO newUser = TestDataCreator.newCJMUserDTO();
		Auditorship auditorship = getPersistedAuditorship();
		
		assertTrue(cjmUserRepository.findAll().isEmpty());
		
		victim.createCJMUserAccount(newUser, auditorship);
		
		Optional<List<User>> users = cjmUserRepository.findActiveByAuditorship(auditorship.getId());
		
		assertTrue(users.isPresent());
		assertEquals(1, users.get().size());
		assertEquals(newUser.getEmail(), users.get().get(0).getEmail());
		assertEquals(UserAccountService.DEFAULT_CJM_USER_PERMISSION_LEVEL, users.get().get(0).getPermissionLevel());
	}
	
	@Test
	void testCreateCJMAccountWhenUsernameIsDuplicated() {
		Auditorship auditorship = getPersistedAuditorship();
		
		CJMUser userDB = EntityMapper.fromDTOToEntity(TestDataCreator.newCJMUserDTO());
		userDB.setUsername("first user");
		userDB.setEmail("first email");
		userDB.setAuditorship(auditorship);
		
		cjmUserRepository.saveAndFlush(userDB);
		
		CJMUserDTO newUser = TestDataCreator.newCJMUserDTO();
		newUser.setUsername("first user");
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class, () -> victim.createCJMUserAccount(newUser, auditorship));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_USERNAME, ACCOUNT_USERNAME_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateCJMAccountWhenEmailIsDuplicated() {
		Auditorship auditorship = getPersistedAuditorship();
		
		CJMUser userDB = EntityMapper.fromDTOToEntity(TestDataCreator.newCJMUserDTO());
		userDB.setUsername("first user");
		userDB.setEmail("first email");
		userDB.setAuditorship(auditorship);
		
		cjmUserRepository.saveAndFlush(userDB);
		
		CJMUserDTO newUser = TestDataCreator.newCJMUserDTO();
		newUser.setEmail("first email");
		
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class, () -> victim.createCJMUserAccount(newUser, auditorship));
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(
				new ValidationError(ACCOUNT_EMAIL, ACCOUNT_EMAIL_ALREADY_EXISTS),
				exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testCreateCJMAccountWhenPasswordIsInvalid() {
		CJMUserDTO newUser = TestDataCreator.newCJMUserDTO();
		Auditorship auditorship = getPersistedAuditorship();
		
		newUser.setPassword("123qqq");
		AccountValidationException exception = 
				assertThrows(AccountValidationException.class, () -> victim.createCJMUserAccount(newUser, auditorship));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_UPPERCASE), exception.getValidationErrors().getError(0));
		
		newUser.setPassword("123QQQ");
		exception = assertThrows(AccountValidationException.class, () -> victim.createCJMUserAccount(newUser, auditorship));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_LOWERCASE), exception.getValidationErrors().getError(0));
		
		newUser.setPassword("qqqQQQ");
		exception = assertThrows(AccountValidationException.class, () -> victim.createCJMUserAccount(newUser, auditorship));
		
		assertEquals(1, exception.getValidationErrors().getNumberOfErrors());
		assertEquals(new ValidationError(ACCOUNT_PASSWORD, PASSWORD_DOES_NOT_HAVE_NUMBER), exception.getValidationErrors().getError(0));
	}
	
	@Test
	void testListGroupAccountSuccessfully() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(newCjm());
		
		List<GroupUser> users = TestDataCreator.newGroupUserList(army, cjm, 3);
		
		users.get(0).setActive(false);											//test should not list nonactive users 												
		
		groupUserRepository.saveAllAndFlush(users);
		
		List<GroupUser> accounts = victim.listGroupUserAccounts(army, cjm);
		
		assertEquals(2, accounts.size());
		assertEquals(users.get(1).getId(), accounts.get(0).getId());
		assertEquals(users.get(2).getId(), accounts.get(1).getId());
	}
	
	@Test
	void testDeleteUserSuccessfully() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(newCjm());
		
		List<GroupUser> users = TestDataCreator.newGroupUserList(army, cjm, 3);
		groupUserRepository.saveAllAndFlush(users);
		
		GroupUser userVictim = users.get(0);
		assertTrue(groupUserRepository.findById(userVictim.getId()).isPresent());
		
		victim.deleteUserAccount(userVictim.getId(), army, cjm);	
		assertFalse(groupUserRepository.findById(userVictim.getId()).get().getActive());
	
		//test if a deleted user is retreaving from database
		List<GroupUser> accounts = victim.listGroupUserAccounts(army, cjm);
		
		assertEquals(2, accounts.size());
		assertEquals(users.get(1).getId(), accounts.get(0).getId());
		assertEquals(users.get(2).getId(), accounts.get(1).getId());
	}
	
	@Test
	void testDeleteUserWhenIdDoesNotExists() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(newCjm());
		
		GroupUser user = TestDataCreator.newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(user);
		
		assertThrows(AccountNotFoundException.class, 
				() -> victim.deleteUserAccount(user.getId() + 1, army, cjm));
	}
	
	@Test
	void testDeleteUserWhenLoggedUserHasNotPermission() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(newCjm());
		
		GroupUser user = TestDataCreator.newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(user);
		
		Army diffArmy = TestDataCreator.newArmy();						// saves a different army
		diffArmy.setAlias(Constants.DEFAULT_ARMY_ALIAS + "x");
		diffArmy.setName(Constants.DEFAULT_ARMY_NAME + "x");
		armyRepository.save(diffArmy);
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, 
				() -> victim.deleteUserAccount(user.getId(), diffArmy, cjm));
		
		assertEquals(ValidationConstants.NO_PERMISSION, exception.getMessage());
	}
	
	private Auditorship getPersistedAuditorship() {
		Auditorship auditorship = newAuditorship();
		auditorship.setCjm(cjmRepository.saveAndFlush(newCjm()));
		auditorshipRepository.saveAndFlush(auditorship);
		return auditorship;
	}
}
