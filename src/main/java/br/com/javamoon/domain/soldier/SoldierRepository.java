package br.com.javamoon.domain.soldier;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;

@Repository
public interface SoldierRepository extends JpaRepository<Soldier, Integer>{

	public Soldier findByEmail(String email);
	
	@Query("FROM Soldier s WHERE s.email = :email AND s.army = :army AND s.cjm = :cjm AND active = true")
	public Optional<Soldier> findActiveByEmailAndArmyAndCjm(@Param("email") String email, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	
	
	public Soldier findByName(String name);
	
	public List<Soldier> findByNameContaining(String name);
	
	@Query("FROM Soldier s WHERE s.name = :name AND s.army = :army AND s.cjm = :cjm AND active = true")
	public Optional<Soldier> findActiveByNameAndArmyAndCjm(@Param("name") String name, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	public Soldier findByNameAndCjm(String name, CJM cjm);
	
	
	
	@Query("select s.id from Soldier s where s.id = :id and s.army = :army")
	public Integer findByIdAndArmy(@Param("id") Integer soldierId, @Param("army") Army army);
	
	@Query("FROM Soldier s WHERE s.id = :id AND s.army = :army AND s.cjm = :cjm AND active = true")
	public Optional<Soldier> findByIdAndArmyAndCjmAndActive(@Param("id") Integer id, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("UPDATE Soldier s SET s.active = false WHERE s.id = :id")
	public void delete(@Param("id") Integer soldierId);
	
	
	@Query("from Soldier s left join fetch s.militaryOrganization where s.army = :army and s.cjm = :cjm and s.active = true order by s.name")
	public List<Soldier> findAllActiveByArmyAndCjm(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId order by s.militaryRank.rankWeight asc")
	public List<Soldier> findAllByDrawOrderByRank(@Param("drawId") Integer drawId);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId")
	public List<Soldier> findAllByDraw(@Param("drawId") Integer drawId);
	
	@Query("select s from DrawList dl "
			+ "join dl.soldiers s "
			+ "left join fetch s.militaryOrganization where dl.id = :drawListId order by s.name")
	public List<Soldier> findAllByDrawList(@Param("drawListId") Integer drawListId);
	
	
	@Query("from DrawExclusion dex where dex.soldier = :soldier order by dex.id desc")
	public List<DrawExclusion> findAllDrawExclusions(@Param("soldier") Soldier soldier);
	
	@Query("select count(s) from Soldier s")
	public Integer getSoldiersNum();
}
