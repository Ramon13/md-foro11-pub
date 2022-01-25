package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CJMUserRepository extends JpaRepository<CJMUser, Integer>{

	@Query("FROM CJMUser c WHERE c.active = true AND c.username = :username")
	Optional<User> findActiveByUsername(@Param("username") String username);
	
	@Query("FROM CJMUser c WHERE c.active = true AND c.auditorship.id = :auditorshipId")
	Optional<List<User>> findActiveByAuditorship(@Param("auditorshipId") Integer auditorshipId);
	
	public List<CJMUser> findByEmail(String email);
}
