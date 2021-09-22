package br.com.javamoon.domain.cjm_user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CJMUserRepository extends JpaRepository<CJMUser, Integer>{

	public CJMUser findByUsername(String username);
	
	public List<CJMUser> findByEmail(String email);
}
