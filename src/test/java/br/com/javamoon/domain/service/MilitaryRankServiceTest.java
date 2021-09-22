package br.com.javamoon.domain.service;

import br.com.javamoon.domain.soldier.MilitaryRank;

public class MilitaryRankServiceTest {

	protected static MilitaryRank getSimpleRank() {
		MilitaryRank rank = new MilitaryRank();
		rank.setId(1);
		rank.setName("CORONEL");
		rank.setAlias("CEL");
		rank.setRankWeight(10);
		
		return rank;
	}
}
