package br.com.javamoon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;

@Service
public class ArmyService {

	@Autowired
	private MilitaryRankRepository militaryRankRepo;
	
	public boolean isMilitaryRankBelongsToArmy(Army army, MilitaryRank...ranks) {
		List<MilitaryRank> ranksByArmy = militaryRankRepo.findAllByArmiesIn(army);
		for (MilitaryRank rank : ranks) {
			if (!ranksByArmy.contains(rank))
				return false;
		}
		return true;
	}
}
