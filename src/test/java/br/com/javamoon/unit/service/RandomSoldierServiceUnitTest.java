package br.com.javamoon.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.cjm_user.CJMRepository;
import br.com.javamoon.domain.entity.DrawList;
import br.com.javamoon.domain.repository.DrawListRepository;
import br.com.javamoon.domain.repository.GroupUserRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.domain.soldier.SoldierRepository;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class RandomSoldierServiceUnitTest {
	
	@Autowired
	private RandomSoldierService victim;
	
	@Autowired
	private DrawListRepository drawListRepository;
	
	@Autowired
	private ArmyRepository armyRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private CJMRepository cjmRepository;

	@Autowired
	private MilitaryOrganizationRepository militaryOrganizationRepository;
	
	@Autowired
	private MilitaryRankRepository militaryRankRepository;
	
	@Autowired
	private SoldierRepository soldierRepository;
	
	@Test
	void testRandomAllSoldiersSuccessfully() throws NoAvaliableSoldierException {
		DrawList drawList = getPersistedDrawList();
		
		DrawDTO drawDTO = TestDataCreator.newDrawDTO();
		drawDTO.setSelectedRanks(List.of(1,1,1,1,1));
		drawDTO.setSelectedDrawList(drawList.getId());
		drawDTO.setArmy(drawList.getArmy());
		
		victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm());
		
		List<Integer> ranks = drawDTO.getSelectedRanks();
		List<Soldier> soldiers = drawDTO.getSoldiers();
		
		assertEquals(drawDTO.getJusticeCouncil().getCouncilSize(), soldiers.size());
		assertEquals(ranks.get(0), soldiers.get(0).getMilitaryRank().getId());
		assertEquals(ranks.get(ranks.size() - 1), soldiers.get(soldiers.size() - 1).getMilitaryRank().getId());
		assertEquals(ranks.get(ranks.size() / 2), soldiers.get(soldiers.size() / 2).getMilitaryRank().getId());
	}
	
	@Test
	void testRandomAllSoldiersWhenRankAreInconsistent() throws NoAvaliableSoldierException {
		DrawList drawList = getPersistedDrawList();
		
		Army newArmy = TestDataCreator.newArmy();
		newArmy.setAlias(Constants.DEFAULT_ARMY_ALIAS + "__");
		newArmy.setName(Constants.DEFAULT_ARMY_NAME + "__");
		armyRepository.saveAndFlush(newArmy);
		
		DrawDTO drawDTO = TestDataCreator.newDrawDTO();					// Rank exists, but with different army
		drawDTO.setSelectedRanks(List.of(1,1,1,1,1));
		drawDTO.setSelectedDrawList(drawList.getId());
		drawDTO.setArmy(newArmy);
		
		assertThrows(IllegalStateException.class,
			() -> victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm()));
		
		drawDTO.setSelectedRanks(List.of(2,1,1,1,1));							// Rank does not exists
		
		assertThrows(IllegalStateException.class,
				() -> victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm()));
	}
	
	@Test
	void testRandomAllSoldiersWhenListIdAreInconsistent() {
		DrawList drawList = getPersistedDrawList(); 
		
		CJM newCjm = TestDataCreator.newCjm();
		newCjm.setAlias(Constants.DEFAULT_CJM_ALIAS + "__");
		newCjm.setName(Constants.DEFAULT_CJM_NAME + "__");
		newCjm.setRegions(Constants.DEFAULT_CJM_REGIONS + "__");
		cjmRepository.saveAndFlush(newCjm);
		
		DrawDTO drawDTO = TestDataCreator.newDrawDTO();					
		drawDTO.setSelectedRanks(List.of(1,1,1,1,1));
		drawDTO.setSelectedDrawList(drawList.getId());
		drawDTO.setArmy(drawList.getArmy());
		
		assertThrows(DrawListNotFoundException.class,
				() -> victim.randomAllSoldiers(drawDTO, newCjm));		// List exists, but with different cjm

		drawDTO.setSelectedDrawList(2);
		assertThrows(DrawListNotFoundException.class,
				() -> victim.randomAllSoldiers(drawDTO, newCjm));		// List does not exists
	}
	
	private DrawList getPersistedDrawList() {
		return TestDataCreator.getPersistedDrawLists(
			armyRepository,
			cjmRepository,
			militaryOrganizationRepository,
			militaryRankRepository,
			groupUserRepository,
			soldierRepository,
			drawListRepository,
			1,
			10).get(0);
	}
}

