package br.com.javamoon.domain.soldier;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MilitaryOrganizationRepository extends JpaRepository<MilitaryOrganization, Integer>{

	public Set<MilitaryOrganization> findSetByArmy(Army army);
	
	public List<MilitaryOrganization> findByArmy(Army army);
	
	public MilitaryOrganization findByAlias(String alias);
}
