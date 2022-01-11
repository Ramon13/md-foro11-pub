package br.com.javamoon.util;

import static br.com.javamoon.util.Constants.*;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.mapper.UserDTO;
import br.com.javamoon.validator.GroupUserAccountValidator;

public final class TestDataCreator {

	private TestDataCreator () {}
	
	public static GroupUserAccountValidator newUserAccountValidator(GroupUserRepository groupUserRepository) {
		return new GroupUserAccountValidator(groupUserRepository);
	}
	
	public static UserDTO newUserDTO() {
		return new UserDTO(DEFAULT_USER_USERNAME, DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD); 
	}
	
	public static GroupUser newGroupUser(Army army, CJM cjm) {
		GroupUser user = new GroupUser();
		user.setUsername(DEFAULT_USER_USERNAME);
		user.setEmail(DEFAULT_USER_EMAIL);
		user.setPassword(DEFAULT_USER_PASSWORD);
		user.setActive(true);
		user.setCredentialsExpired(false);
		user.setArmy(army);
		user.setCjm(cjm);
		user.setPermissionLevel(7);
		
		return user;
	}
	
	public static Army newArmy() {
		Army army = new Army();
		army.setName(DEFAULT_ARMY_NAME);
		army.setAlias(DEFAULT_ARMY_ALIAS);
		
		return army;
	}
	
	public static CJM newCjm() {
		CJM cjm = new CJM();
		cjm.setName(DEFAULT_CJM_NAME);
		cjm.setAlias(DEFAULT_CJM_ALIAS);
		cjm.setRegions(DEFAULT_CJM_REGIONS);
		return cjm;
	}
}
