package br.com.javamoon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.repository.ArmyRepository;
import br.com.javamoon.exception.ArmyNotFoundException;

@Service
public class ArmyService {

	private ArmyRepository armyRepository;
	
	public ArmyService(ArmyRepository armyRepository) {
		this.armyRepository = armyRepository;
	}

	public Army getArmy(Integer armyId) {
		return armyRepository.findById(armyId).orElseThrow(() -> 
			new ArmyNotFoundException("The army cannot be found: " + armyId));
	}
	
	public Army getByAlias(String alias) {
		return armyRepository.findByAlias(alias).orElseThrow(() -> 
			new ArmyNotFoundException("The army cannot be found: " + alias));
	}
	
	public List<Army> list(){
		return armyRepository.findAll();
	}
}
