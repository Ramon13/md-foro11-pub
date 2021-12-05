package br.com.javamoon.domain.draw;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.soldier.Army;

public interface DrawListRepository extends JpaRepository<DrawList, Integer>{

	List<DrawList> findByArmyOrderByIdDesc(Army army);
	
	@Query("from DrawList dl where dl.army = :army and dl.creationUser.cjm = :cjm")
	List<DrawList> findByArmyAndCjm(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("from DrawList dl where upper(dl.description) like :description and dl.army = :army")
	DrawList findByDescriptionIgnoreCase(@Param("description") String description, @Param("army") Army army);
}
