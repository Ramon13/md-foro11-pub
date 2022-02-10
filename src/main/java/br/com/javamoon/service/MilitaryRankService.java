package br.com.javamoon.service;

import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.exception.MilitaryRankNotFoundException;

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
	
	public MilitaryRank getById(Integer rankId) {
		return militaryRankRepository.findById(rankId)
				.orElseThrow(() -> new MilitaryRankNotFoundException("rank not found for: " + rankId));
	}
}
