package br.com.javamoon.domain.draw;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.soldier.Army;

public interface DrawListRepository extends JpaRepository<DrawList, Integer>{

	List<DrawList> findByArmyOrderByIdDesc(Army army);
	
	@Query("from DrawList dl where upper(dl.description) like concat('%',upper(:description),'%') and dl.army = :army")
	DrawList findByDescriptionIgnoreCase(@Param("description") String description, @Param("army") Army army);
}
