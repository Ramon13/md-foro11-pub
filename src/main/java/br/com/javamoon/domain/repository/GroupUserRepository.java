package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.GroupUser;
import br.com.javamoon.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	@Query("FROM GroupUser gu WHERE gu.active = true AND gu.username = :username")
	public Optional<User> findActiveByUsername(@Param("username") String username);

	public Optional<GroupUser> findByEmail(String email);

	@Query("FROM GroupUser gp WHERE gp.active = true and gp.army = :army and gp.cjm = :cjm")
	public List<GroupUser> findActiveByArmyAndCjm(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE FROM GroupUser gp SET gp.active = false WHERE gp.id = :id")
	public void delete(@Param("id") Integer id);
	
	@Query("FROM GroupUser g WHERE g.active = true AND g.email = :email")
	Optional<User> findActiveByEmail(@Param("email") String email);
	
	@Query("FROM GroupUser g WHERE g.active = true AND g.recoveryToken = :recoveryToken")
	Optional<User> findActiveByRecoveryToken(@Param("recoveryToken") String recoveryToken);
}
