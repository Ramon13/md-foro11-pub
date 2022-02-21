package br.com.javamoon.service;

import br.com.javamoon.domain.entity.Army;
import br.com.javamoon.domain.entity.MilitaryRank;
import br.com.javamoon.domain.repository.ArmyRepository;
import br.com.javamoon.domain.repository.MilitaryRankRepository;
import br.com.javamoon.exception.ArmyNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ArmyService {

	private MilitaryRankRepository militaryRankRepo;
	
	private ArmyRepository armyRepository;
	
	public ArmyService(MilitaryRankRepository militaryRankRepo, ArmyRepository armyRepository) {
		this.militaryRankRepo = militaryRankRepo;
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
	
	public boolean isMilitaryRankBelongsToArmy(Army army, MilitaryRank...ranks) {
		List<MilitaryRank> ranksByArmy = militaryRankRepo.findAllByArmiesIn(army);
		for (MilitaryRank rank : ranks) {
			if (!ranksByArmy.contains(rank))
				return false;
		}
		return true;
	}
}
