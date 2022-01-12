package br.com.javamoon.service;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MilitaryRankService {

	private MilitaryRankRepository militaryRankRepository;

	public MilitaryRankService(MilitaryRankRepository militaryRankRepository) {
		this.militaryRankRepository = militaryRankRepository;
	}
	
	public List<MilitaryRank> listRanksByArmy(Army army){
		return militaryRankRepository.findAllByArmiesIn(army);
	}
}
