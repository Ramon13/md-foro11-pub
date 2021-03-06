package br.com.javamoon.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.DrawList;

public interface DrawListRepository extends JpaRepository<DrawList, Integer>{

	@Query("FROM DrawList dl "
		+ "WHERE dl.active = true "
		+ "AND dl.id = :id "
		+ "AND ( :army IS NULL OR dl.army = :army ) "
		+ "AND dl.creationUser.cjm = :cjm"
	)
	Optional<DrawList> findActiveByIdAndArmyAndCjm(
		@Param("id") Integer id, 
		@Param("army") Army army, 
		@Param("cjm") CJM cjm
	);
	
	@Query("FROM DrawList dl WHERE dl.active = true AND dl.id = :id AND dl.creationUser.cjm = :cjm")
	Optional<DrawList> findActiveByIdAndCjm(@Param("id") Integer id, @Param("cjm") CJM cjm);
	
	List<DrawList> findByArmyOrderByIdDesc(Army army);
	
	@Query("FROM DrawList dl "
		+ "WHERE dl.active = true "
		+ "AND ( :yearQuarter IS NULL OR dl.yearQuarter = :yearQuarter ) "
		+ "AND ( :army IS NULL OR dl.army = :army ) "
		+ "AND dl.creationUser.cjm = :cjm")
	List<DrawList> findAllActiveByQuarterAndArmyAndCJM(
			@Param("yearQuarter") String yearQuarter,
			@Param("army") Army army,
			@Param("cjm") CJM cjm,
			Pageable pageable);
	
	@Query("FROM DrawList dl WHERE dl.active = true AND dl.army = :army AND dl.creationUser.cjm = :cjm ORDER BY dl.id DESC")
	Optional<List<DrawList>> findAllActiveByArmyAndCjm(@Param("army") Army army, @Param("cjm") CJM cjm);
	
	@Query("FROM DrawList dl WHERE dl.active = true AND dl.creationUser.cjm.id = :cjmId ORDER BY dl.id")
	Optional<List<DrawList>> findAllActiveByCjm(@Param("cjmId") Integer cjmId);
	
	
	@Query("from DrawList dl where upper(dl.description) like :description and dl.army = :army")
	DrawList findByDescriptionIgnoreCase(@Param("description") String description, @Param("army") Army army);
	
	@Query("FROM DrawList dl WHERE dl.active = true AND UPPER(dl.description) LIKE UPPER(:description) AND dl.army = :army AND dl.creationUser.cjm = :cjm")
	Optional<DrawList> findAllActiveByDescriptionAndArmyAndCjm(
			@Param("description") String description, @Param("army") Army army, @Param("cjm") CJM cjm);
	
	
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("UPDATE FROM DrawList dl SET dl.active = false WHERE dl.id = :id")
	void disable(@Param("id") Integer listId);
}
