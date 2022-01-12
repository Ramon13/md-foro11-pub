package br.com.javamoon.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.exception.AccountNotFoundException;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationConstants;
import java.util.List;
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
	private GroupUserRepository groupUserRepository;
	
	@Test
	void testListGroupAccountSuccessfully() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
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
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
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
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
		GroupUser user = TestDataCreator.newGroupUserList(army, cjm, 1).get(0);
		groupUserRepository.saveAndFlush(user);
		
		assertThrows(AccountNotFoundException.class, 
				() -> victim.deleteUserAccount(user.getId() + 1, army, cjm));
	}
	
	@Test
	void testDeleteUserWhenLoggedUserHasNotPermission() {
		Army army = armyRepository.saveAndFlush(TestDataCreator.newArmy());
		CJM cjm = cjmRepository.saveAndFlush(TestDataCreator.newCjm());
		
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
}
