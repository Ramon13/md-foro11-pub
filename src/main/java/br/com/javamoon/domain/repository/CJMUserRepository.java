package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.entity.CJMUser;
import br.com.javamoon.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CJMUserRepository extends JpaRepository<CJMUser, Integer>{

	@Query("FROM CJMUser c WHERE c.active = true AND c.username = :username")
	Optional<User> findActiveByUsername(@Param("username") String username);
	
	@Query("FROM CJMUser c WHERE c.active = true AND c.auditorship.id = :auditorshipId")
	Optional<List<CJMUser>> findActiveByAuditorship(@Param("auditorshipId") Integer auditorshipId);
	
	@Query("FROM CJMUser c WHERE c.active = true AND c.id = :id AND c.auditorship.id = :auditorshipId")
	Optional<CJMUser> findActiveByIdAndAuditorship(
			@Param("id") Integer id,
			@Param("auditorshipId") Integer auditorshipId);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE CJMUser c SET c.active = false WHERE c.id = :id")
	void disableAccount(@Param("id") Integer id);
	
	@Query("FROM CJMUser c WHERE c.active = true AND c.email = :email")
	Optional<User> findActiveByEmail(@Param("email") String email);
}
