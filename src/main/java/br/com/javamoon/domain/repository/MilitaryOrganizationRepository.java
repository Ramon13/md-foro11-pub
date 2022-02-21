package br.com.javamoon.domain.repository;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilitaryOrganizationRepository extends JpaRepository<MilitaryOrganization, Integer>{

	public Set<MilitaryOrganization> findSetByArmy(Army army);
	
	public Optional<List<MilitaryOrganization>> findByArmy(Army army);
	
	public MilitaryOrganization findByAlias(String alias);
}