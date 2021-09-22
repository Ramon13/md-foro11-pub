package br.com.javamoon.domain.soldier;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MilitaryRankRepository extends JpaRepository<MilitaryRank, Integer>{

	@Query("select mr from Army a join a.militaryRanks mr where a = :army order by mr.rankWeight asc")
	List<MilitaryRank> findAllByArmiesIn(@Param("army") Army army);
	
	MilitaryRank findByAlias(String alias);
}
