package br.com.javamoon.domain.soldier;

import java.util.List;

import javax.transaction.Transactional;

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
	
	public Soldier findByName(String name);
	
	public List<Soldier> findByNameContaining(String name);
	
	public List<Soldier> findByArmy(Army army);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId order by s.militaryRank.rankWeight asc")
	public List<Soldier> findAllByDrawOrderByRank(@Param("drawId") Integer drawId);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId")
	public List<Soldier> findAllByDraw(@Param("drawId") Integer drawId);
	
	@Query("from Soldier s where s.army = :army and s.id = :soldierId and s.cjm = :cjm")
	public Soldier findByIdAndArmyAndCjm(@Param("soldierId") Integer soldierId, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("from Soldier s where s.army = :army and s.cjm = :cjm and s.enabledForDraw = true order by s.militaryRank.rankWeight asc")
	public List<Soldier> findByArmyAndCjmAndNotEnabledForDraw(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("from DrawExclusion dex where dex.soldier = :soldier order by dex.id desc")
	public List<DrawExclusion> findAllDrawExclusions(@Param("soldier") Soldier soldier);
	
	@Transactional
	@Modifying
	@Query("update from Soldier set enabledForDraw = false where army = :army")
	public void disableAllSoldiersForDrawByArmy(@Param("army") Army army);
	
	@Query("select count(s) from Soldier s")
	public Integer getSoldiersNum();
}
