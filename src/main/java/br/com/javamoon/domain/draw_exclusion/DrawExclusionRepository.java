package br.com.javamoon.domain.draw_exclusion;

import br.com.javamoon.domain.soldier.Soldier;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawExclusionRepository extends JpaRepository<DrawExclusion, Integer>{

	@Query("from DrawExclusion dx where dx.soldier.id = :soldierId and (dx.startDate between :startDate and :endDate or dx.endDate between :startDate and :endDate)")
	Set<DrawExclusion> findBySoldierBetweenDates(@Param("soldierId") Integer soldierId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	List<DrawExclusion> findAllBySoldier(Soldier soldier);
}
