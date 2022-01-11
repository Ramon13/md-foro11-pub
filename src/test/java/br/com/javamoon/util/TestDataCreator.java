package br.com.javamoon.util;

import static br.com.javamoon.util.Constants.DEFAULT_USER_EMAIL;
import static br.com.javamoon.util.Constants.DEFAULT_USER_PASSWORD;
import static br.com.javamoon.util.Constants.DEFAULT_USER_USERNAME;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
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
	
	public static GroupUser newGroupUser() {
		return new GroupUser();
	}
}
