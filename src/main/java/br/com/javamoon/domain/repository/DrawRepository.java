package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.cjm_user.Auditorship;
import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw.JusticeCouncil;
import br.com.javamoon.domain.soldier.Army;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawRepository extends JpaRepository<Draw, Integer>{

	@Deprecated
	Draw findByProcessNumber(String processNumber);
	
	@Deprecated
	@Query("from Draw d join d.soldiers s where s.id = :soldierId and d.justiceCouncil.id = 2")
	List<Draw> findBySoldierJusticeCouncil(@Param("soldierId") Integer soldierId);
	
	@Deprecated
	@Query("from Draw d where d.cjmUser.auditorship.cjm = :cjm")
	List<Draw> findByCJM(@Param("cjm") CJM cjm);
	
	@Deprecated
	@Query("from Draw d where d.cjmUser.auditorship.cjm = :cjm and d.army = :army")
	List<Draw> findByCJMAndArmy(@Param("cjm") CJM cjm, @Param("army") Army army);
	
	@Deprecated
	List<Draw> findByJusticeCouncil(JusticeCouncil justiceCouncil);
	
	@Deprecated
	@Query("from Draw d where d.cjmUser.auditorship = :auditorship and d.justiceCouncil = :justiceCouncil")
	List<Draw> findByAuditorshipJusticeCouncil(@Param("auditorship") Auditorship auditorship, @Param("justiceCouncil") JusticeCouncil justiceCouncil);
	
	@Deprecated
	List<Draw> findByProcessNumberIgnoreCaseContaining(String processNumber);
	
	@Deprecated
	@Query("from Draw d where d.cjmUser.auditorship.id = :auditorshipId and d.justiceCouncil.id = 2 and d.finished = false")
	List<Draw> findUnfinishedByAuditorship(@Param("auditorshipId") Integer auditorshipId);
	
	@Deprecated
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
}
