package br.com.javamoon.application.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.javamoon.domain.draw.Draw;
import br.com.javamoon.domain.draw_exclusion.DrawExclusion;
import br.com.javamoon.domain.soldier.Army;
import br.com.javamoon.domain.soldier.MilitaryRank;
import br.com.javamoon.domain.soldier.NoAvaliableSoldierException;
import br.com.javamoon.domain.soldier.Soldier;

@Service
public class RandomSoldierService {

	@Autowired
	private SoldierService soldierSvc;
	
	@Autowired
	private DrawExclusionService drawExclusionSvc;
	
	@Autowired
	private ArmyService armySvc;
	

	
	public void randomAllSoldiers(Army army, MilitaryRank[] ranks, LinkedList<Soldier> soldiers) throws NoAvaliableSoldierException{
		for (int i = 0; i < ranks.length; i++) {
			Soldier randomSoldier = soldierSvc.getRandomSoldiersByRank(ranks[i], army, soldiers);
			soldiers.add(i, randomSoldier);
		}
	}
	
	public void setSoldierExclusionMessages(Draw draw) {
		Set <DrawExclusion> exclusions;
		
		for (Soldier soldier : draw.getSoldiers()) {
			exclusions = new HashSet<>();
			
			exclusions.addAll( drawExclusionSvc.findByAnnualQuarter(draw.getAnnualQuarter(), soldier) );
			
			exclusions.addAll( drawExclusionSvc.getByLatestDraws(soldier) );
			
			soldier.setCustomExclusions(exclusions);
		}
	}
	
	public void setSoldierExclusionMessages(Soldier soldier, Draw draw) {
		Set <DrawExclusion> exclusions;
		
		exclusions = new HashSet<>();
		
		exclusions.addAll( drawExclusionSvc.findByAnnualQuarter(draw.getAnnualQuarter(), soldier) );
		
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
		
		if (!armySvc.isMilitaryRankBelongsToArmy(army, replaceRank))
			throw new IllegalStateException("The rank does not belong to this army");
		
		Soldier oldSoldier = draw.getSoldiers().get(selectedIndex);
		Soldier newSoldier = soldierSvc.getRandomSoldiersByRank(replaceRank, army, draw.getSoldiers());
		
		draw.getSoldiers().set(selectedIndex, newSoldier);
		draw.getSoldiers().add(oldSoldier);
		
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
}
