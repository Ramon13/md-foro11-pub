package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.Constants.DEFAULT_USER_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.service.UserAccountService;
import br.com.javamoon.util.TestDataCreator;

@SpringBootTest
@ActiveProfiles("test")
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
		
		GroupUser user0 =TestDataCreator.newGroupUser(army, cjm);
		GroupUser user1 =TestDataCreator.newGroupUser(army, cjm);
		user1.setUsername(DEFAULT_USER_USERNAME + "x");
		user1.setEmail(DEFAULT_USER_EMAIL + "x");
		
		groupUserRepository.saveAndFlush(user0);
		groupUserRepository.saveAndFlush(user1);
		List<GroupUser> accounts = victim.listGroupUserAccounts(army, cjm);
		
		assertEquals(2, accounts.size());
	}
}
