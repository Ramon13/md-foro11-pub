package br.com.javamoon.application.service;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.group_user.GroupUser;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.user.User;

@Service
public class GroupUserService {

	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Transactional
	public void saveUser(GroupUser gpUser) throws ValidationException{
		if (!validateUsername(gpUser.getUsername(), null))
			throw new ValidationException("username", "Nome de usuário já cadastrado no sistema.");
		if (!validateEmail(gpUser.getEmail(), null))
			throw new ValidationException("email", "Email já cadastrado no sistema");
		
		if (gpUser.getId() != null) {
			User userDB = groupUserRepository.findById(gpUser.getId()).orElseThrow(NoSuchElementException::new);
			gpUser.setPassword(userDB.getPassword());
		}else {
			gpUser.setCredentialsExpired(true);
			gpUser.encryptPassword();
		}
		
		groupUserRepository.save(gpUser);
	}
	
	private boolean validateUsername(String username, Integer id) {
		User user = groupUserRepository.findByUsername(username);
		
		if (user != null) {
			if (id == null)
				return false;
			if (!user.getId().equals(id))
				return false;
			return true;
		}
		
		return true;
	}
	
	private boolean validateEmail(String email, Integer id) {
		User user = groupUserRepository.findByEmail(email);
		
		if (user != null && !StringUtils.isAllBlank(email)) {
			if (id == null)
				return false;
			if (!user.getId().equals(id))
				return false;
			return true;
		}
		
		return true;
	}
}
