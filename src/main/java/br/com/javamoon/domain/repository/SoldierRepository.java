package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.Soldier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SoldierRepository extends JpaRepository<Soldier, Integer>{

	Soldier findByEmail(String email);
	
	@Query("FROM Soldier s WHERE s.email = :email AND s.army = :army AND s.cjm = :cjm AND active = true")
	Optional<Soldier> findActiveByEmailAndArmyAndCjm(@Param("email") String email, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	
	
	Soldier findByName(String name);
	
	List<Soldier> findByNameContaining(String name);
	
	@Query("FROM Soldier s WHERE s.name = :name AND s.army = :army AND s.cjm = :cjm AND active = true")
	Optional<Soldier> findActiveByNameAndArmyAndCjm(@Param("name") String name, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	Soldier findByNameAndCjm(String name, CJM cjm);
	
	
	
	@Query("select s.id from Soldier s where s.id = :id and s.army = :army")
	Integer findByIdAndArmy(@Param("id") Integer soldierId, @Param("army") Army army);
	
	@Query("FROM Soldier s WHERE s.id = :id AND s.army = :army AND s.cjm = :cjm AND s.active = true")
	Optional<Soldier> findByIdAndArmyAndCjmAndActive(@Param("id") Integer id, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("FROM Soldier s WHERE s.active = true AND s.id = :soldierId AND s.cjm.id = :cjmId")
	Optional<Soldier> findActiveByIdAndCJM(@Param("soldierId") Integer soldierId, @Param("cjmId") Integer cjmId);
	
	@Query(
		"FROM Soldier s "
		+ "WHERE s.active = true "
		+ "AND ( s.name like %:key% OR s.email like %:key% ) "
		+ "AND s.army.id = :armyId "
		+ "AND s.cjm.id = :cjmId "
	)
	List<Soldier> findActiveByArmyAndCJMContaining(
		@Param("key") String key,
		@Param("armyId") Integer armyId,
		@Param("cjmId") Integer cjmId
	);
	
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("UPDATE Soldier s SET s.active = false WHERE s.id = :id")
	void delete(@Param("id") Integer soldierId);
	
	
	@Query("FROM Soldier s "
			+ "LEFT JOIN FETCH s.militaryOrganization "
			+ "WHERE s.active = true "
			+ "AND s.army = :army "
			+ "AND s.cjm = :cjm ORDER BY s.name")
	List<Soldier> findAllActiveByArmyAndCjm(
			@Param("army") Army army,
			@Param("cjm") CJM cjm,
			Pageable pageable);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId order by s.militaryRank.rankWeight asc")
	List<Soldier> findAllByDrawOrderByRank(@Param("drawId") Integer drawId);
	
	@Query("select s from Draw d join d.soldiers s where d.id = :drawId")
	List<Soldier> findAllByDraw(@Param("drawId") Integer drawId);
	
	@Query("SELECT s FROM Soldier s "
			+ "JOIN s.drawList dl "
			+ "LEFT JOIN FETCH s.militaryOrganization "
			+ "WHERE dl.id = :drawListId "
			+ "AND s.active = true "
			+ "AND ( :key IS NULL OR s.name like %:key% OR s.email like %:key% ) ")
	List<Soldier> findAllActiveByDrawList(
			@Param("drawListId") Integer drawListId,
			@Param("key") String key,
			Pageable pageable);
	
	@Query("SELECT s FROM Soldier s "
			+ "JOIN s.drawList dl "
			+ "LEFT JOIN FETCH s.militaryOrganization "
			+ "WHERE dl.id = :drawListId "
			+ "AND s.active = true")
	List<Soldier> findAllActiveByDrawList(
			@Param("drawListId") Integer drawListId);
	
	@Query(
		"FROM Soldier s LEFT JOIN FETCH s.militaryOrganization " + 
	    "WHERE s.active = true AND s.army = :army AND s.cjm = :cjm AND s.id IN :ids"
	)
	List<Soldier> findByArmyAndCjmAndIdIn(
		@Param("army") Army army,
		@Param("cjm") CJM cjm,
		@Param("ids") List<Integer> ids
	);
	
	
	@Query("from DrawExclusion dex where dex.soldier = :soldier order by dex.id desc")
	List<DrawExclusion> findAllDrawExclusions(@Param("soldier") Soldier soldier);
	
	@Query("select count(s) from Soldier s")
	Integer getSoldiersNum();
	
	@Query(
		"SELECT COUNT(s) FROM Soldier s "
		+ "JOIN s.drawList dl "
		+ "WHERE dl.id = :drawListId "
		+ "AND s.active = true "
		+ "AND ( :key IS NULL OR s.name like %:key% OR s.email like %:key% ) "
		+ "AND s.army.id = :armyId "
		+ "AND s.cjm.id = :cjmId "
	)
	Integer countActiveByArmyAndCjmContaining(
		@Param("drawListId") Integer drawListId,
		@Param("key") String key,
		@Param("armyId") Integer armyId,
		@Param("cjmId") Integer cjmId
	);
	
	@Query(
			"SELECT s FROM Soldier s "
			+ "JOIN s.drawList dl "
			+ "WHERE dl.id = :drawListId "
			+ "AND s.active = true "
			+ "AND s.id = :soldierId"
		)
		Optional<Soldier> findActiveByDrawList(
		    @Param("soldierId") Integer soldierId,
			@Param("drawListId") Integer drawListId
		);
}
