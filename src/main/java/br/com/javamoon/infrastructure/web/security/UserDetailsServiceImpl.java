package br.com.javamoon.infrastructure.web.security;

import br.com.javamoon.domain.entity.User;
import br.com.javamoon.domain.repository.CJMUserRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private CJMUserRepository cjmUserRepository;
	private GroupUserRepository groupUserRepository;

	public UserDetailsServiceImpl(CJMUserRepository cjmUserRepository, GroupUserRepository groupUserRepository) {
		this.cjmUserRepository = cjmUserRepository;
		this.groupUserRepository = groupUserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = groupUserRepository.findActiveByUsername(username).orElseGet(
			() -> cjmUserRepository.findActiveByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username)) 
		);
		
		return new LoggedUser(user);
	}
}
