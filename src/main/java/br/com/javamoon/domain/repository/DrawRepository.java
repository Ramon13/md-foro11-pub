package br.com.javamoon.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.draw.Draw;

public interface DrawRepository extends JpaRepository<Draw, Integer>{

	Optional<Draw> findByProcessNumber(String processNumber);
	
	@Query("select count(d) from Draw d join d.soldiers s where s.id = :soldierId")
	Integer countDrawBySoldier(@Param("soldierId") Integer soldierId);
	
	@Query(
		"FROM Draw d JOIN d.soldiers s "
		+ "WHERE s.id = :soldierId "
		+ "AND (d.creationDate BETWEEN :startDate AND :endDate)")
	List<Draw> findBySoldierBetweenDates(
		@Param("soldierId") Integer soldierId, 
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate
	);
	
	@Query(
		"FROM Draw d JOIN d.soldiers s"
		+ " WHERE s.id = :soldierId"
		+ "	AND d.finished = false"
		+ " AND d.justiceCouncil.alias = :councilAlias")
	List<Draw> findUnfinishedByCJM(
		@Param("councilAlias") String councilAlias,
		@Param("soldierId") Integer soldierId
	);
	
	@Query("FROM Draw d WHERE d.cjmUser.auditorship.id = :auditorshipId")
	List<Draw> findAllByAuditorship(@Param("auditorshipId") Integer auditorshipId);
	
	@Query("FROM Draw d WHERE d.army.id = :armyId AND d.cjmUser.auditorship.cjm.id = :cjmId")
	List<Draw> findAllByArmyAndCJM(@Param("armyId") Integer armyId, @Param("cjmId") Integer cjmId);
	
	@Query("FROM Draw d WHERE d.id = :drawId AND d.cjmUser.auditorship.id = :auditorshipId")
	Optional<Draw> findByIdAndAuditorship(@Param("drawId") Integer drawId, @Param("auditorshipId") Integer auditorshipId);
}
