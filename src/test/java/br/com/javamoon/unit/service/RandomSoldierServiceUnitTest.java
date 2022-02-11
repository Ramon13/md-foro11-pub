package br.com.javamoon.unit.service;

import static br.com.javamoon.util.Constants.DEFAULT_REPLACE_RANK_ID;
import static br.com.javamoon.util.Constants.DEFAULT_REPLACE_SOLDIER_ID;
import static br.com.javamoon.util.TestDataCreator.newDrawDTO;
import static br.com.javamoon.validator.ValidationConstants.DRAW_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.INCONSISTENT_DATA;
import static br.com.javamoon.validator.ValidationConstants.NO_AVALIABLE_SOLDIERS;
import static br.com.javamoon.validator.ValidationConstants.REPLACE_SOLDIER_IS_NOT_IN_THE_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

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
import br.com.javamoon.domain.repository.SoldierRepository;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.ArmyRepository;
import br.com.javamoon.domain.soldier.MilitaryOrganizationRepository;
import br.com.javamoon.domain.soldier.MilitaryRankRepository;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawListNotFoundException;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.service.RandomSoldierService;
import br.com.javamoon.util.Constants;
import br.com.javamoon.util.TestDataCreator;
import br.com.javamoon.validator.ValidationError;

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
		DrawList drawList = getPersistedDrawList(10);
		DrawDTO drawDTO = newDrawDTO();
		setupDrawDTOToRandomize(drawList, drawDTO, false, false);
		
		victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm());
		
		List<Integer> ranks = drawDTO.getSelectedRanks();
		List<SoldierDTO> soldiers = drawDTO.getSoldiers();
		
		assertEquals(drawDTO.getJusticeCouncil().getCouncilSize(), soldiers.size());
		assertEquals(ranks.get(0), soldiers.get(0).getMilitaryRank().getId());
		assertEquals(ranks.get(ranks.size() - 1), soldiers.get(soldiers.size() - 1).getMilitaryRank().getId());
		assertEquals(ranks.get(ranks.size() / 2), soldiers.get(soldiers.size() / 2).getMilitaryRank().getId());
	}
	
	@Test
	void testRandomAllSoldiersWhenThereAreNoDrawableSoldier() {
		DrawList drawList = getPersistedDrawList(5);
		DrawDTO drawDTO = newDrawDTO();
		setupDrawDTOToRandomize(drawList, drawDTO, false, false);
		
		Soldier soldier = drawList.getSoldiers().stream().findFirst().get();
		soldier.setActive(false);
		soldierRepository.saveAndFlush(soldier);
		
		assertThrows(DrawValidationException.class, () -> victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm()));
	}
	
	@Test
	void testRandomAllSoldiersWhenRankAreInconsistent() throws NoAvaliableSoldierException {
		DrawList drawList = getPersistedDrawList(10);
		DrawDTO drawDTO = newDrawDTO();					
		setupDrawDTOToRandomize(drawList, drawDTO, false, false);
		
		Army newArmy = TestDataCreator.newArmy();
		newArmy.setAlias(Constants.DEFAULT_ARMY_ALIAS + "__");
		newArmy.setName(Constants.DEFAULT_ARMY_NAME + "__");
		armyRepository.saveAndFlush(newArmy);
		
		drawDTO.setArmy(newArmy);												// Rank exists, but with different army
		
		assertThrows(IllegalStateException.class,
			() -> victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm()));
		
		drawDTO.setSelectedRanks(List.of(2,1,1,1,1));							// Rank does not exists
		
		assertThrows(IllegalStateException.class,
				() -> victim.randomAllSoldiers(drawDTO, drawList.getCreationUser().getCjm()));
	}
	
	@Test
	void testRandomAllSoldiersWhenListIdAreInconsistent() {
		DrawList drawList = getPersistedDrawList(10); 
		DrawDTO drawDTO = newDrawDTO();
		setupDrawDTOToRandomize(drawList, drawDTO, false, false);
		
		CJM newCjm = TestDataCreator.newCjm();
		newCjm.setAlias(Constants.DEFAULT_CJM_ALIAS + "__");
		newCjm.setName(Constants.DEFAULT_CJM_NAME + "__");
		newCjm.setRegions(Constants.DEFAULT_CJM_REGIONS + "__");
		cjmRepository.saveAndFlush(newCjm);
		
		assertThrows(DrawListNotFoundException.class,
				() -> victim.randomAllSoldiers(drawDTO, newCjm));		// List exists, but with different cjm

		drawDTO.setSelectedDrawList(2);
		assertThrows(DrawListNotFoundException.class,
				() -> victim.randomAllSoldiers(drawDTO, newCjm));		// List does not exists
	}
	
	@Test
	void testReplaceRandomSoldierSuccessfully() throws NoAvaliableSoldierException {
		DrawList drawList = getPersistedDrawList(6); 
		DrawDTO drawDTO = newDrawDTO();
		setupDrawDTOToRandomize(drawList, drawDTO, true, true);
		
		SoldierDTO replaceSoldier = drawDTO.getSoldiers().remove(5);
		drawDTO.getDrawnSoldiers().remove(5);
		
		assertEquals(drawList.getSoldiers().size() - 1, drawDTO.getSoldiers().size());
		assertEquals(drawList.getSoldiers().size() - 1, drawDTO.getDrawnSoldiers().size());
		
		int replaceIndex = victim.replaceRandomSoldier(drawDTO);
		
		assertEquals(replaceSoldier.getId(), drawDTO.getSoldiers().get(replaceIndex).getId());
		assertEquals(drawList.getSoldiers().size(), drawDTO.getDrawnSoldiers().size());
		assertEquals(drawList.getSoldiers().size() - 1, drawDTO.getSoldiers().size());
	}
	
	@Test
	void testReplaceRandomSoldierWhenReplaceSoldierIdNotBelongsToList() {
		DrawList drawList = getPersistedDrawList(5); 
		DrawDTO drawDTO = newDrawDTO();
		setupDrawDTOToRandomize(drawList, drawDTO, true, true);
		
		drawDTO.setReplaceSoldier(DEFAULT_REPLACE_SOLDIER_ID + 10);
		
		IllegalStateException exception = 
				assertThrows(IllegalStateException.class, () -> victim.replaceRandomSoldier(drawDTO));
		
		assertEquals(REPLACE_SOLDIER_IS_NOT_IN_THE_LIST, exception.getMessage());
	}
	
	@Test
	void testReplaceRandomSoldiersWhenRankAreInconsistent() throws NoAvaliableSoldierException {
		DrawList drawList = getPersistedDrawList(5);
		DrawDTO drawDTO = newDrawDTO();					
		setupDrawDTOToRandomize(drawList, drawDTO, true, true);
		
		Army newArmy = TestDataCreator.newArmy();
		newArmy.setAlias(Constants.DEFAULT_ARMY_ALIAS + "__");
		newArmy.setName(Constants.DEFAULT_ARMY_NAME + "__");
		armyRepository.saveAndFlush(newArmy);
		
		drawDTO.setArmy(newArmy);												// Rank exists, but with different army
		
		IllegalStateException exception = 
				assertThrows(IllegalStateException.class, () -> victim.replaceRandomSoldier(drawDTO));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
		
		drawDTO.setSelectedRanks(List.of(2,1,1,1,1));							// Rank does not exists
		
		exception = assertThrows(IllegalStateException.class, () -> victim.replaceRandomSoldier(drawDTO));
		assertEquals(INCONSISTENT_DATA, exception.getMessage());
	}
	
	@Test
	void testReplaceRandomSoldierWhenThereAreNoSoldiersAvaliable() {
		DrawList drawList = getPersistedDrawList(5);
		DrawDTO drawDTO = newDrawDTO();					
		setupDrawDTOToRandomize(drawList, drawDTO, true, true);
		
		DrawValidationException exception = 
				assertThrows(DrawValidationException.class, () -> victim.replaceRandomSoldier(drawDTO));
		
		String rankAlias = militaryRankRepository.findById(1).get().getAlias();
		assertEquals(
			new ValidationError(DRAW_SOLDIERS, NO_AVALIABLE_SOLDIERS + rankAlias), 
			exception.getValidationErrors().getError(0)
		);
	}

	private void setupDrawDTOToRandomize(DrawList drawList, DrawDTO drawDTO, boolean setupSoldiers, boolean setupDrawnSoldiers) {
		if (setupSoldiers) {
    		drawDTO.getSoldiers().addAll(
    			drawList.getSoldiers().stream()
    			.map(s -> EntityMapper.fromEntityToDTO(s))
    			.collect(Collectors.toList())
    		);
		}
		
		if (setupDrawnSoldiers) {
    		drawDTO.getDrawnSoldiers().addAll(
    			drawDTO.getSoldiers().stream().map(s -> s.getId()).collect(Collectors.toList())
    		);
		}
			
    	drawDTO.setArmy(drawList.getArmy());
    	drawDTO.setSelectedRanks(List.of(1,1,1,1,1));
    	drawDTO.setReplaceSoldier(DEFAULT_REPLACE_SOLDIER_ID);
    	drawDTO.setSelectedDrawList(drawList.getId());
	}
	
	private DrawList getPersistedDrawList(int numOfSoldiers) {
		return TestDataCreator.getPersistedDrawLists(
			armyRepository,
			cjmRepository,
			militaryOrganizationRepository,
			militaryRankRepository,
			groupUserRepository,
			soldierRepository,
			drawListRepository,
			1,
			numOfSoldiers).get(0);
	}
}

