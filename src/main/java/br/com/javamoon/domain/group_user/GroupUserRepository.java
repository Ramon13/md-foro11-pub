package br.com.javamoon.domain.group_user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	public Optional<GroupUser> findByUsername(String username);
	
	public Optional<GroupUser> findByEmail(String email);
}
