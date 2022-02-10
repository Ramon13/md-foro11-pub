package br.com.javamoon.domain.soldier;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MilitaryRankRepository extends JpaRepository<MilitaryRank, Integer>{

	@Query("SELECT mr FROM Army a JOIN a.militaryRanks mr WHERE a = :army ORDER BY mr.rankWeight ASC")
	List<MilitaryRank> findAllByArmiesIn(@Param("army") Army army);
	
	@Query("SELECT mr.id FROM Army a JOIN a.militaryRanks mr WHERE a = :army")
	List<Integer> findAllIdsByArmiesIn(@Param("army") Army army);
	
	MilitaryRank findByAlias(String alias);
	
	Optional<MilitaryRank> findById(Integer id);
}
