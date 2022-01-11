package br.com.javamoon.infrastructure.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.cjm_user.CJMUserRepository;
import br.com.javamoon.domain.group_user.GroupUserRepository;
import br.com.javamoon.domain.user.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private CJMUserRepository cjmUserRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = groupUserRepository.findByUsername(username).orElseThrow();
		
		if (user == null) {
			user = cjmUserRepository.findByUsername(username);
			if (user == null)
				throw new UsernameNotFoundException(username);
		}
		
		return new LoggedUser(user);
	}
}
