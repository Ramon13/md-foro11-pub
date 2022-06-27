package br.com.javamoon.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;

public interface MilitaryOrganizationRepository extends JpaRepository<MilitaryOrganization, Integer>{

	public Set<MilitaryOrganization> findSetByArmy(Army army);
	
	@Query("FROM MilitaryOrganization om WHERE om.army.id = :armyId")
	public List<MilitaryOrganization> findByArmy(@Param("armyId") Integer armyId);
	
	public Optional<MilitaryOrganization> findByAlias(String alias);
}
