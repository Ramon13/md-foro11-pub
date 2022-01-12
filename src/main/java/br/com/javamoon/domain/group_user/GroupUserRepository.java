package br.com.javamoon.domain.group_user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	public Optional<GroupUser> findByUsername(String username);

	public Optional<GroupUser> findByEmail(String email);

	@Query("FROM GroupUser gp WHERE gp.active = true and gp.army = :army and gp.cjm = :cjm")
	public List<GroupUser> findActiveByArmyAndCjm(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE FROM GroupUser gp SET gp.active = false WHERE gp.id = :id")
	public void delete(@Param("id") Integer id);
}
