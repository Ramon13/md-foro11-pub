package br.com.javamoon.service;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryOrganization;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
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
