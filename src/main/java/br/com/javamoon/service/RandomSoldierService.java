package br.com.javamoon.service;

import br.com.javamoon.domain.cjm_user.CJM;
import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.repository.SoldierRepositoryImpl;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;
import br.com.javamoon.exception.DrawValidationException;
import br.com.javamoon.mapper.DrawDTO;
import br.com.javamoon.mapper.DrawExclusionDTO;
import br.com.javamoon.mapper.EntityMapper;
import br.com.javamoon.mapper.SoldierDTO;
import br.com.javamoon.validator.DrawValidator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RandomSoldierService {

	private SoldierService soldierService;
	private DrawExclusionService drawExclusionService;
	private SoldierRepositoryImpl soldierRepositoryImpl;
	private DrawValidator drawValidator;
	private final DrawListService drawListService;

	public RandomSoldierService(SoldierService soldierService, DrawExclusionService drawExclusionService,
			SoldierRepositoryImpl soldierRepositoryImpl, DrawValidator drawValidator, DrawListService drawListService) {
		this.soldierService = soldierService;
		this.drawExclusionService = drawExclusionService;
		this.soldierRepositoryImpl = soldierRepositoryImpl;
		this.drawValidator = drawValidator;
		this.drawListService = drawListService;
	}

	public void randomAllSoldiers(DrawDTO drawDTO, CJM cjm) throws NoAvaliableSoldierException, DrawValidationException{
		drawValidator.randAllSoldiersValidation(drawDTO);
		drawListService.getList(drawDTO.getSelectedDrawList(), cjm);		// validate if list exists, otherwise throw an exception
		
		List<Integer> ranks = drawDTO.getSelectedRanks();
		Soldier randomSoldier;
		for (int i = 0; i < ranks.size() ; i++) {
			randomSoldier = getRandomSoldier(
				ranks.get(i),
				drawDTO.getArmy(),
				drawDTO.getSelectedDrawList(),
				drawDTO.getSoldiers().stream().map(s -> s.getId()).collect(Collectors.toList()));
			
			drawDTO.getSoldiers().add(EntityMapper.fromEntityToDTO(randomSoldier));
		}
	}
	
	public void setSoldierExclusionMessages(Collection<SoldierDTO> soldiers, String selectedYearQuarter) {
		List<DrawExclusion> exclusions = new ArrayList<>(0);
		for (SoldierDTO soldierDTO : soldiers) {
			exclusions.addAll(drawExclusionService.getByAnnualQuarter(selectedYearQuarter, soldierDTO.getId()));
			exclusions.addAll(drawExclusionService.getBySelectableQuarterPeriod(soldierDTO.getId()));
			
			//TODO: add cej unfinished exclusions
			soldier.setCustomExclusions(exclusions);
		}
	}
	
	public void setSoldierExclusionMessages(Soldier soldier, Draw draw) {
		Set <DrawExclusion> exclusions;
		
		exclusions = new HashSet<>();
		
		String selectedQuarter = draw.getDrawList().getYearQuarter();
		exclusions.addAll( drawExclusionSvc.findByAnnualQuarter(selectedQuarter, soldier) );
		
		exclusions.addAll( drawExclusionSvc.getByLatestDraws(soldier) );
		
		soldier.setCustomExclusions(exclusions);
		
	}
	
	/**
	 * Random a new Soldier using embedded database rand() function.
	 * Set the replaced soldier to the end of the array list.
	 * Set the new soldier in the same position as the old soldier
	 */
	public Soldier replaceRandomSoldier(Soldier replaceSoldier, Draw draw, MilitaryRank replaceRank) throws NoAvaliableSoldierException {
		Integer selectedIndex = getSelectedIndex(draw.getSoldiers(), replaceSoldier);
		
		Army army = draw.getArmy();
		if (draw.getExcludeSoldiers().isEmpty())
			for(Soldier soldier : draw.getSoldiers())
				draw.getExcludeSoldiers().add(soldier.getId());
		
		Soldier newSoldier = soldierSvc.getRandomSoldier(replaceRank, army, draw.getDrawList(), draw.getExcludeSoldiers());
		
		draw.getSoldiers().set(selectedIndex, newSoldier);
		draw.getRanks().set(selectedIndex, replaceRank);
		draw.getExcludeSoldiers().add(newSoldier.getId());
		
		if (replaceSoldier.equals(draw.getSubstitute()))
			draw.setSubstitute(newSoldier);
		
		return newSoldier;
	}
	
	private int getSelectedIndex(List<Soldier> soldiers, Soldier replaceSoldier) {
		for (int i = 0; i < soldiers.size(); i++) {
			if (soldiers.get(i).getId().equals(replaceSoldier.getId())) {
				return i;
			}
		}
		
		throw new IllegalStateException("The replaced soldier is not on the list");
	}
	
	public Soldier getRandomSoldier(
		Integer rankId, 
		Army army,
		Integer drawListId,
		List<Integer> drawnSoldierIds) throws NoAvaliableSoldierException{
		
		return soldierRepositoryImpl.randomByDrawList(rankId, army, drawListId, drawnSoldierIds);
	}
}
