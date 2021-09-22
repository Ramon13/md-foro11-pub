package br.com.javamoon.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.cjm_user.CJMUserRepository;
import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.user.User;
import br.com.javamoon.util.StringUtils;

@Service
public class LoginService {

	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void editPassword(User loggedUser, User user) throws ValidationException {
		if (loggedUser instanceof GroupUser && !loggedUser.getId().equals(user.getId()))
			throw new IllegalStateException("You cannot has permission to execute this action. ID not valid.");
		
		if (!validatePassword(user.getPassword()))
			throw new ValidationException("password", "A senha deve conter caracteres maiúsculos, minúsculos e números");
		
		user.encryptPassword();
		
		JpaRepository repo = null;
		
		User userDB = (GroupUser) groupUserRepository.findByUsername(user.getUsername());
		repo = groupUserRepository;
		if (userDB == null) {
			userDB = (CJMUser) cjmUserRepository.findByUsername(user.getUsername());
			repo = cjmUserRepository;
		}
		userDB.setCredentialsExpired(!userDB.isCredentialsExpired());
		userDB.setPassword(user.getPassword());
		repo.save(userDB);
		
		
		loggedUser.setCredentialsExpired(false);
	}
	
	private boolean validatePassword(String password) {
		return StringUtils.hasAnyNumber(password) && StringUtils.hasAnyUpperCase(password);
	}
}
