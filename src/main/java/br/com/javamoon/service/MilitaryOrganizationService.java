package br.com.javamoon.service;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryOrganization;
import br.com.javamoon.domain.repository.MilitaryOrganizationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MilitaryOrganizationService {

	private MilitaryOrganizationRepository militaryOrganizationRepository;

	public MilitaryOrganizationService(MilitaryOrganizationRepository militaryOrganizationRepository) {
		this.militaryOrganizationRepository = militaryOrganizationRepository;
	}
	
	public List<MilitaryOrganization> listOrganizationsByArmy(Army army){
		return militaryOrganizationRepository.findByArmy(army).get();
	}
}
