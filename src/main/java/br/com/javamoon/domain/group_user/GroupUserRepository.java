package br.com.javamoon.domain.group_user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Integer>{

	public Optional<GroupUser> findByUsername(String username);

	public Optional<GroupUser> findByEmail(String email);

	public List<GroupUser> findActiveByArmyAndCjm(Army army, CJM cjm);
}
