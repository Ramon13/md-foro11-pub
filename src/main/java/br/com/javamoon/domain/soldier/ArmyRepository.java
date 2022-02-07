package br.com.javamoon.domain.soldier;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmyRepository extends JpaRepository<Army, Integer>{

	@Query("FROM Soldier s WHERE s.army = :army order by s.militaryRank.rankWeight asc")
	public List<Soldier> findAllSoldiers(@Param("army") Army army);
	
	Optional<Army> findByAlias(String alias);
}
