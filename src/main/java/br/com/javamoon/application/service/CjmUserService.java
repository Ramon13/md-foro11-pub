package br.com.javamoon.application.service;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJMUser;
import br.com.javamoon.domain.cjm_user.CJMUserRepository;
import br.com.javamoon.domain.user.User;

@Service
public class CjmUserService {

	@Autowired
	private CJMUserRepository cjmUserRepo;
	
	@Transactional
	public void saveUser(CJMUser user) throws ValidationException{
		if (!validateUsername(user.getUsername(), null))
			throw new ValidationException("username", "Nome de usuário já cadastrado no sistema.");
		if (!validateEmail(user.getEmail(), null))
			throw new ValidationException("email", "Email já cadastrado no sistema");
		
		if (user.getId() != null) {
			User userDB = cjmUserRepo.findById(user.getId()).orElseThrow(NoSuchElementException::new);
			user.setPassword(userDB.getPassword());
		}else {
			user.setCredentialsExpired(true);
			user.encryptPassword();
		}
		
		cjmUserRepo.save(user);
	}
	
	private boolean validateUsername(String username, Integer id) {
		User user = cjmUserRepo.findByUsername(username);
		
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
		List<CJMUser> users = cjmUserRepo.findByEmail(email);
		
		if (users != null && !StringUtils.isAllBlank(email)) {
			if (id == null)
				return false;
			if (!users.get(0).getId().equals(id))
				return false;
			return true;
		}
		
		return true;
	}
}
