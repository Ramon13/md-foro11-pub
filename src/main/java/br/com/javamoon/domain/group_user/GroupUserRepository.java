package br.com.javamoon.domain.group_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	public GroupUser findByUsername(String username);
	
	public GroupUser findByEmail(String email);
}
