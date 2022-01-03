package br.com.javamoon.domain.group_user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	public GroupUser findByUsername(String username);
	
	public Optional<GroupUser> findByUsernameAndCredentialsExpired(String username, Boolean credentialsExpired);
	
	public Optional<GroupUser> findByEmailAndCredentialsExpired(String email, Boolean credentialsExpired);
}
