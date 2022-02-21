package br.com.javamoon.domain.draw_exclusion;

import br.com.javamoon.domain.entity.Soldier;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawExclusionRepository extends JpaRepository<DrawExclusion, Integer>{

	@Query(
		"FROM DrawExclusion dx "
		+ "WHERE dx.soldier.id = :soldierId"
		+ " AND (dx.startDate BETWEEN :startDate AND :endDate OR dx.endDate between :startDate and :endDate)"
	)
	List<DrawExclusion> findBySoldierBetweenDates(@Param("soldierId") Integer soldierId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	List<DrawExclusion> findAllBySoldierOrderByIdDesc(Soldier soldier);
	
	Optional<DrawExclusion> findByIdAndSoldier(Integer id, Soldier soldier);
}
