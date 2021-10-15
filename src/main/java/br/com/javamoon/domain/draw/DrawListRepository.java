package br.com.javamoon.domain.draw;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.soldier.Army;

public interface DrawListRepository extends JpaRepository<DrawList, Integer>{

	@Query("from DrawList dl where dl.army = :army")
	List<DrawList> findByArmy(@Param("army") Army army);
}
